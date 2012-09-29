package org.dyndns.fzoli.mobilerc;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.NativeComponentWrapper;
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
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author zoli
 */
public class Radar extends JDialog {
    
    private RadarPosition position = new RadarPosition(0, 0);
    
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
            "      div#border, div#arrow { position: fixed }" + LS +
            "      div#border { z-index: 1000003; top: 0px; left: 0px }" + LS +
            "      div#arrow { z-index: 1000002; top: " + ((MAP_HEIGHT / 2) - (ARROW_SIZE / 2)) + "px; left: " + ((MAP_WIDTH / 2) - (ARROW_SIZE / 2)) + "px; width: " + ARROW_SIZE + "px; height: " + ARROW_SIZE + "px }" + LS +
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

    public Radar() {
        this(null, null);
    }

    public Radar(Window owner) {
        this(owner, null);
    }
    
    public Radar(RadarLoadListener callback) {
        this(null, callback);
    }
    
    public Radar(final Window owner, final RadarLoadListener callback) {
        super(owner, "Radar");
        
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
                    if (callback == null) setVisible(true);
                    else callback.loadFinished(Radar.this);
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
    
    public RadarPosition getPosition() {
        return position;
    }
    
    public Double getArrowRotation() {
        return ARROW.getRotation();
    }
    
    public void setPosition(double latitude, double longitude) {
        setPosition(new RadarPosition(latitude, longitude));
    }
    
    public void setPosition(final RadarPosition pos) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                position = pos;
                webBrowser.executeJavascript("map.setCenter(new google.maps.LatLng(" + position.getLatitude() + ", " + position.getLongitude() + "));");
            }
            
        });
    }
    
    public void setArrow(final Double rotation) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                try {
                    ARROW.setRotation(rotation);
                    if (!TMP_DIR.isDirectory()) TMP_DIR.mkdir();
                    ImageIO.write(ARROW, "png", ARROW_FILE);
                    webBrowser.executeJavascript("document.getElementById('arrow').innerHTML = '<img src=\"" + ARROW_FILE.toURI().toURL() + "?nocache=" + Math.random() + "\" />';");
                }
                catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            
        });
    }
    
    public void removeArrow() {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                webBrowser.executeJavascript("document.getElementById('arrow').innerHTML = '';");
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
        if (f.exists()) {
            f.delete();
        }
    }
    
    public static void main(String[] args) {
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                final Timer timer = new Timer();
                Radar radar = new Radar(new RadarLoadListener() {

                    @Override
                    public void loadFinished(final Radar radar) {
                        radar.setArrow(null);
                        timer.schedule(new TimerTask() {
                            
                            double angle = 0;
                            
                            @Override
                            public void run() {
                                angle += 10;
                                radar.setArrow(angle);
                            }
                            
                        }, 5000, 1000);
                        radar.setPosition(47.35021, 19.10236);
                        radar.setVisible(true);
                    }
                    
                });
                
                radar.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        timer.cancel();
                        System.exit(0);
                    }
                    
                });
            }
            
        });
        NativeInterface.runEventPump();
    }
    
}