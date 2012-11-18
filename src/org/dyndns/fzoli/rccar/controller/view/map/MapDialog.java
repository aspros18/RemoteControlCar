package org.dyndns.fzoli.rccar.controller.view.map;

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
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_MAP;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import org.dyndns.fzoli.rccar.controller.view.AbstractDialog;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * Térkép ablak.
 * Google Map alapú térkép.
 * Natív böngésző segítségével jelenik meg.
 * @author zoli
 */
public class MapDialog extends AbstractDialog {
    
    /**
     * A térkép kezdőpozíciója (0, 0, 0) valahol az óceánon.
     */
    private Point3D position = new Point3D(0, 0, 0);
    
    /**
     * Új sor jel az aktuális rendszeren.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * A térképhez tartozó méretek.
     */
    private final int MAP_WIDTH = 400, MAP_HEIGHT = 300, RADAR_SIZE = 200, ARROW_SIZE = 30;
    
    /**
     * A térkép nyila.
     */
    private final MapArrow ARROW = new MapArrow(ARROW_SIZE);
    
    /**
     * Ideignlenes könyvtár.
     */
    private final File TMP_DIR = new File(System.getProperty("user.dir"), "tmp");
    
    /**
     * A nyilat ábrázoló png kép helye az ideignlenes könyvtárban.
     */
    private final File ARROW_FILE = new File(TMP_DIR, "arrow.png");
    
    /**
     * A térképet megjelenítő HTML kód.
     */
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
    
    /**
     * A natív böngésző.
     */
    private final JWebBrowser webBrowser;

    public MapDialog(Window owner, ControllerWindows windows) {
        this(owner, windows, null);
    }
    
    public MapDialog(MapLoadListener callback, ControllerWindows windows) {
        this(null, windows, callback);
    }
    
    public MapDialog(final Window owner, ControllerWindows windows, final MapLoadListener callback) {
        super(owner, "Térkép", windows);
        setIconImage(IC_MAP.getImage());
        
        final JLayeredPane mapPane = new JLayeredPane(); // a komponens pontos pozíciójának beállítására használom
        mapPane.setPreferredSize(new Dimension(RADAR_SIZE, RADAR_SIZE)); // a méret megadása
        webBrowser = new JWebBrowser();
        Component webComponent = new NativeComponentWrapper(webBrowser).createEmbeddableComponent();
        mapPane.add(webComponent, JLayeredPane.DEFAULT_LAYER); // a böngésző a méretezett pane-re kerül
        webComponent.setBounds((-1 * (MAP_WIDTH / 2)) + (RADAR_SIZE / 2), (-1 * (MAP_HEIGHT / 2)) + (RADAR_SIZE / 2), MAP_WIDTH, MAP_HEIGHT); // és a pozíciója úgy van beállítva, hogy a Google reklám ne látszódjon
        
        // a natív böngésző lecsupaszítása
        webBrowser.setBarsVisible(false);
        webBrowser.setButtonBarVisible(false);
        webBrowser.setLocationBarVisible(false);
        webBrowser.setMenuBarVisible(false);
        webBrowser.setStatusBarVisible(false);
        webBrowser.setJavascriptEnabled(true);
        webBrowser.setDefaultPopupMenuRegistered(false);
        
        // HTML forráskód betöltése
        webBrowser.setHTMLContent(HTML_SOURCE);
        
        // várakozás a térkép api betöltésére
        webBrowser.addWebBrowserListener(new WebBrowserAdapter() {
            
            @Override
            public void loadingProgressChanged(WebBrowserEvent e) {
                if (e.getWebBrowser().getLoadingProgress() == 100) { // ha letöltődött a script
                    try {
                        // Google API betöltési ideje max. 1,5 másodperc
                        Thread.sleep(1500);
                    }
                    catch(Exception ex) {
                        ;
                    }
                    e.getWebBrowser().executeJavascript(createInitScript()); // térkép inicializálás
                    if (callback == null) setVisible(true); // ablak megjelenítése, ha nincs eseményfigyelő
                    else callback.loadFinished(MapDialog.this); // egyébként eseményfigyelő futtatása
                }
            }
            
        });
        
        // a program leállása előtt az ideignlenes könyvtár rekurzív törlése
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            
            @Override
            public void run() {
                delete(TMP_DIR);
            }
            
        }));
        
        getContentPane().add(mapPane, BorderLayout.CENTER); // térkép középre igazíása
        setResizable(false); // ablak átméretezésének tiltása
        pack(); // minimum méret beállítása
    }
    
    /**
     * A térkép pozíciója.
     */
    public Point3D getPosition() {
        return position;
    }
    
    /**
     * A nyíl iránya.
     */
    public Double getArrowRotation() {
        return ARROW.getRotation();
    }
    
    /**
     * A térkép pozíciójának beállítása.
     * @param latitude szélesség
     * @param longitude hosszúság
     */
    public void setPosition(double latitude, double longitude) {
        setPosition(new Point3D(latitude, longitude, 0));
    }
    
    /**
     * A térkép pozíciójának beállítása.
     * JavaScript alapú metódus.
     * @param pos GPS koordináta
     */
    public void setPosition(final Point3D pos) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                position = pos;
                webBrowser.executeJavascript("map.setCenter(new google.maps.LatLng(" + position.X + ", " + position.Y + "));");
            }
            
        });
    }
    
    /**
     * Az iránymutató nyíl irányának megadása.
     * Legenerálja a képet, elmenti az ideignlenes könyvtárba, majd
     * JavaScript segítségével beállítja az új képet.
     * @param rotation északtól való eltérés, vagy null, ha nincs irány megadva
     */
    public void setArrow(final Double rotation) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                try {
                    ARROW.setRotation(rotation); // nyíl frissítése
                    if (!TMP_DIR.isDirectory()) TMP_DIR.mkdir(); // tmp könyvtár létrehozása, ha nem létezik
                    ImageIO.write(ARROW, "png", ARROW_FILE); // png formátumban a nyíl mentése a tmp könyvtárba
                    webBrowser.executeJavascript("document.getElementById('arrow').innerHTML = '<img src=\"" + ARROW_FILE.toURI().toURL() + "?nocache=" + Math.random() + "\" />';"); // a kép frissítése a böngészőben
                }
                catch(Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            
        });
    }
    
    /**
     * Az irányjelző nyíl eltüntetése.
     * JavaScript alapú metódus.
     */
    public void removeArrow() {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                webBrowser.executeJavascript("document.getElementById('arrow').innerHTML = '';");
            }
            
        });
    }
    
    /**
     * A Google Map inicializáló JavaScript kódja.
     */
    private String createInitScript() {
        return "var mapOptions = {" + LS +
               "  zoom: 17," + LS +
               "  center: new google.maps.LatLng(" + position.X + ", " + position.Y + ")," + LS +
               "  disableDefaultUI: true," + LS +
               "  mapTypeId: google.maps.MapTypeId.HYBRID" + LS +
               "}" + LS +
               "var map = new google.maps.Map(document.getElementById(\"map_canvas\"), mapOptions);";
    }
    
    /**
     * Rekurzívan törli a megadott fájlt.
     */
    private static void delete(File f) {
        if (f.isDirectory()) { // ha könyvtár
            for (File c : f.listFiles()) { // a benne lévő összes fájl...
                delete(c); // ... rekurzív törlése
            }
        }
        if (f.exists()) { // ha a fájlnak már nincs gyermeke és még létezik...
            f.delete(); // ... a fájl törlése
        }
    }

    /**
     * Az ablak típusa: térkép.
     */
    @Override
    public WindowType getWindowType() {
        return WindowType.MAP;
    }
    
    /**
     * Teszt.
     */
    public static void main(String[] args) {
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                
                final Timer timer = new Timer();
                MapDialog radar = new MapDialog(new MapLoadListener() {

                    @Override
                    public void loadFinished(final MapDialog radar) {
                        radar.setArrow(null); // kezdetben nincs irány ...
                        timer.schedule(new TimerTask() {
                            
                            double angle = 0;
                            
                            @Override
                            public void run() {
                                angle += 10;
                                radar.setArrow(angle);
                            }
                            
                        }, 5000, 1000); // ... 5 másodperccel később másodpercenként változik az irány
                        radar.setPosition(47.35021, 19.10236); // a hely Dunaharaszti egyik utcája
                        radar.setVisible(true); // az ablak megjelenítése 
                    }
                    
                }, null);
                
                // az ablak bezárásakor:
                radar.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        timer.cancel(); // időzítő leállítása
                        System.exit(0); // kilépés a programból
                    }
                    
                });
            }
            
        });
        NativeInterface.runEventPump();
    }
    
}