package org.dyndns.fzoli.mobilerc;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.NativeComponentWrapper;
import chrriis.dj.nativeswing.swtimpl.NativeComponent;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author zoli
 */
public class Radar extends JDialog {
    
    public static class Position {
        
        private double latitude, longitude;
        
        public Position(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
    }
    
    private double arrowAngle = 0;
    private Position position = new Position(0, 0);
    
    private static final String LS = System.getProperty("line.separator");
    
    private final int MAP_WIDTH = 400, MAP_HEIGHT = 300, RADAR_SIZE = 200, ARROW_SIZE = 30;
    private final RadarArrow ARROW = new RadarArrow(ARROW_SIZE);
    
    private final File TMP_DIR = new File(System.getProperty("user.dir"), "tmp");
    private final File ARROW_FILE = new File(TMP_DIR, "arrow.png");
    
    private final String HTML_SOURCE =
            "<!DOCTYPE html>" + LS +
            "<html>" + LS +
            "  <head>" + LS +
            "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\" />" + LS +
            "    <style type=\"text/css\">" + LS +
            "      html, body { height: 100% }" + LS +
            "      body { margin: 0; padding: 0; }" + LS +
            "      div#map_canvas, #border { width: " + MAP_WIDTH + "px; height: " + MAP_HEIGHT + "px }" + LS +
            "      div#border, div#arrow { z-index: 10; position: fixed }" + LS +
            "      div#border { top: 0px; left: 0px }" + LS +
            "      div#arrow { top: " + ((MAP_HEIGHT / 2) - (ARROW_SIZE / 2)) + "px; left: " + ((MAP_WIDTH / 2) - (ARROW_SIZE / 2)) + "px; width: " + ARROW_SIZE + "px; height: " + ARROW_SIZE + "px }" + LS +
            "    </style>" + LS +
            "    <script type=\"text/javascript\" src=\"http://maps.googleapis.com/maps/api/js?&sensor=false\"></script>" + LS +
            "  </head>" + LS +
            "  <body>" + LS +
            "    <div id=\"map_canvas\"></div>" + LS +
            "    <div id=\"border\"></div>" + LS +
            "    <div id=\"arrow\"></div>" + LS +
            "  </body>" + LS +
            "</html>";
    
    private final JWebBrowser webBrowser;
    
    public Radar(Window owner) {
        super(owner, "Radar");
        if (!TMP_DIR.isDirectory()) TMP_DIR.mkdir();
        
        final JLayeredPane radarPane = new JLayeredPane();
        radarPane.setPreferredSize(new Dimension(RADAR_SIZE, RADAR_SIZE));
        webBrowser = new JWebBrowser();
        Component webComponent = new NativeComponentWrapper(webBrowser).createEmbeddableComponent();
        radarPane.add(webComponent, JLayeredPane.DEFAULT_LAYER);
        webComponent.setBounds((-1 * (MAP_WIDTH / 2)) + (RADAR_SIZE / 2), (-1 * (MAP_HEIGHT / 2)) + (RADAR_SIZE / 2), MAP_WIDTH, MAP_HEIGHT);
        webBrowser.setBarsVisible(false);
        webBrowser.setButtonBarVisible(false);
        webBrowser.setLocationBarVisible(false);
        webBrowser.setMenuBarVisible(false);
        webBrowser.setStatusBarVisible(false);
        webBrowser.setJavascriptEnabled(true);
        webBrowser.setDefaultPopupMenuRegistered(false);
        webBrowser.setHTMLContent(HTML_SOURCE);
        
        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            
            @Override
            public void loadingProgressChanged(WebBrowserEvent e) {
                if (e.getWebBrowser().getLoadingProgress() == 100) {
                    try {
                        // Google API betöltési ideje max. 1,5 másodperc
                        Thread.sleep(1500);
                    }
                    catch(Exception ex) {
                        ;
                    }
                    e.getWebBrowser().executeJavascript(createInitScript());
                    setVisible(true);
                }
            }
            
        });
        
        addWindowStateListener(new WindowAdapter() {

            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
            }
            
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            
            @Override
            public void run() {
                delete(TMP_DIR);
            }
            
        }));
        
        getContentPane().add(radarPane, BorderLayout.CENTER);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        pack();
    }
    
    public Position getPosition() {
        return position;
    }
    
    public double getArrowAngle() {
        return arrowAngle;
    }
    
    public void setPosition(double latitude, double longitude) {
        setPosition(new Position(latitude, longitude));
    }
    
    public void setPosition(Position pos) {
        position = pos;
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                webBrowser.executeJavascript("map.setCenter(new google.maps.LatLng(" + position.getLatitude() + ", " + position.getLongitude() + "));");
            }
            
        });
    }
    
    public void setArrow(double angle) {
        arrowAngle = angle;
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                try {
                    ARROW.rotate(arrowAngle);
                    ImageIO.write(ARROW, "png", ARROW_FILE);
                    webBrowser.executeJavascript("document.getElementById('arrow').innerHTML = '<img src=\"" + ARROW_FILE.getPath() + "?nocache=" + Math.random() + "\" />';");
                }
                catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            
        });
    }
    
    private String createInitScript() {
        return "var mapOptions = {" + LS +
               "  zoom: 17," + LS +
               "  center: new google.maps.LatLng(" + position.getLatitude() + ", " + position.getLongitude() + ")," + LS +
               "  disableDefaultUI: true," + LS +
               "  mapTypeId: google.maps.MapTypeId.HYBRID" + LS +
               "}" + LS +
               "var map = new google.maps.Map(document.getElementById(\"map_canvas\"), mapOptions);";
    }
    
    private static void delete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        f.delete();
    }
    
    public static void loadSwtJar(Class clazz) {
        File swtFile = null;
        try {
            String osName = System.getProperty("os.name").toLowerCase();
            String osArch = System.getProperty("os.arch").toLowerCase();
            URLClassLoader classLoader = (URLClassLoader) clazz.getClassLoader();
            Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);

            String swtFileNameOsPart = 
                osName.contains("win") ? "win32" :
                osName.contains("mac") ? "macosx" :
                osName.contains("linux") || osName.contains("nix") ? "linux" : "";

            if (swtFileNameOsPart.isEmpty()) {
                throw new RuntimeException("Unknown OS name: " + osName);
            }
            
            String swtFileName = "swt-" + swtFileNameOsPart + "-" + (osArch.contains("64") ? "x86_64" : "x86") + ".jar";
            swtFile = new File(new File(System.getProperty("user.dir"), "lib/swt"), swtFileName);
            addUrlMethod.invoke(classLoader, swtFile.toURL());
        }
        catch(Exception e) {
            throw new RuntimeException("Unable to add the swt jar to the class path: " + swtFile);
        }
    }
    
    public static void main(String[] args) {
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                Radar radar = new Radar(null);
                radar.setPosition(47.35021, 19.10237);
                radar.setArrow(29);
                radar.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                    
                });
            }
            
        });
        NativeInterface.runEventPump();
    }
    
}