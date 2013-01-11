package org.dyndns.fzoli.rccar.controller.view.map;

import chrriis.dj.nativeswing.NativeComponentWrapper;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_MAP;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.view.AbstractDialog;
import org.dyndns.fzoli.rccar.controller.view.ControllerFrame;
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
     * Fok-koordináta formázó az információs panelhez.
     */
    private static final DecimalFormat DF = new DecimalFormat("#.00");
    
    /**
     * Magasság-koordináta formázó az információs panelhez.
     */
    private static final DecimalFormat DF2 = new DecimalFormat("#.##");
    
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
            "      div#border, div#arrow, div#info { position: fixed }" + LS +
            "      div#border { z-index: 1000003; top: 0px; left: 0px }" + LS +
            "      div#info { z-index: 1000002; cursor: default; font-family: \"Arial\"; font-size: 12px; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black; color: white; visibility: hidden; left: " + (((MAP_WIDTH / 2)) - (RADAR_SIZE / 2)) + "px; top: " + (((MAP_HEIGHT / 2)) - (RADAR_SIZE / 2)) + "px; padding: 2px; background-color: rgba(0, 0, 0, 0.3); filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#4C000000,endColorstr=#4C000000); -ms-filter: \"progid:DXImageTransform.Microsoft.gradient(startColorstr=#4C000000,endColorstr=#4C000000)\" }" + LS +
            "      div#arrow { z-index: 1000002; top: " + ((MAP_HEIGHT / 2) - (ARROW_SIZE / 2)) + "px; left: " + ((MAP_WIDTH / 2) - (ARROW_SIZE / 2)) + "px; width: " + ARROW_SIZE + "px; height: " + ARROW_SIZE + "px }" + LS +
            "      div.fadeprep { opacity: 1; transition: opacity .25s ease-in-out; -moz-transition: opacity .25s ease-in-out; -webkit-transition: opacity .25s ease-in-out; }" + LS +
            "      div.fadeon { -ms-filter:\"progid:DXImageTransform.Microsoft.Alpha(Opacity=50)\"; filter: alpha(opacity=50); -moz-opacity:0.5; -khtml-opacity: 0.5; opacity: 0.5; }" + LS +
            "    </style>" + LS +
            "    <script type=\"text/javascript\" src=\"http://maps.googleapis.com/maps/api/js?sensor=false\"></script>" + LS +
            "  </head>" + LS +
            "  <body>" + LS +
            "    <div id=\"map_canvas\" class=\"fadeprep\"></div>" + LS +
            "    <div id=\"border\"></div>" + LS +
            "    <div id=\"arrow\"></div>" + LS +
            "    <div id=\"info\"></div>" + LS +
            "  </body>" + LS +
            "</html>";
    
    /**
     * A natív böngésző.
     */
    private final JWebBrowser webBrowser;
    
    /**
     * Figyelmeztető üzenet a sikertelen betöltéshez.
     * Kattintásra megpróbálja újratölteni a térképet.
     */
    private final JLabel lbWarn;
    
    /**
     * Megadja, hogy a fade effekt engedélyezve van-e.
     */
    private boolean fadeEnabled = false;
    
    public MapDialog(ControllerFrame owner, ControllerWindows windows) {
        this(owner, windows, null, true);
    }
    
    public MapDialog(MapLoadListener callback, ControllerWindows windows) {
        this(null, windows, callback, true);
    }
    
    public MapDialog(final ControllerFrame owner, ControllerWindows windows, final MapLoadListener callback, final boolean enabled) {
        super(owner, "Térkép", windows);
        setIconImage(IC_MAP.getImage());
        getContentPane().setBackground(Color.WHITE);
        
        final JLayeredPane mapPane = new JLayeredPane(); // a komponens pontos pozíciójának beállítására használom
        mapPane.setPreferredSize(new Dimension(RADAR_SIZE, RADAR_SIZE)); // a méret megadása
        
        // indikátor jelenik meg, míg a térkép töltődik
        final JPanel pInd = new JPanel() {
            {
                final JLabel lbInd = new JLabel(R.getIndicatorIcon());
                GridBagConstraints c = new GridBagConstraints();
                setLayout(new GridBagLayout());
                setPreferredSize(mapPane.getPreferredSize());
                setOpaque(false);
                add(lbInd, c);
                c.gridy = 1;
                c.insets = new Insets(5, 5, 5, 5);
                add(new JLabel("Betöltés"), c);
            }
        };
        getContentPane().add(pInd, BorderLayout.SOUTH);
        pInd.setVisible(false);
        
        // figyelmeztető üzenet jelenik meg, ha a térkép betöltése nem sikerült
        lbWarn = new JLabel("<html><p style=\"text-align:center; color:red; font-weight: 900\">A térkép betöltése nem sikerült!</p><br><p style=\"text-align:center\">Kattintson ide az újratöltéshez.</p></html>", SwingConstants.CENTER);
        lbWarn.setPreferredSize(mapPane.getPreferredSize());
        lbWarn.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent ev) {
                reload();
            }

        });
        getContentPane().add(lbWarn, BorderLayout.WEST);
        lbWarn.setVisible(false);
        
        // kezdetben úgy tesz, mint ha nem lenne böngésző támogatás
        final JLabel lbErr = new JLabel("<html><p style=\"text-align:center\">Ha a térkép hamarosan nem tölt be, telepítsen Mozilla Firefox böngészőt.</p></html>", SwingConstants.CENTER);
        lbErr.setPreferredSize(mapPane.getPreferredSize()); // a hibaüzenet mérete megegyezik a térképével
        getContentPane().add(lbErr, BorderLayout.NORTH); // a hibaüzenet az ablak felső részére kerül
        
        getContentPane().add(mapPane, BorderLayout.CENTER); // a térkép középre igazítva jelenik meg
        
        if (enabled) {
            JWebBrowser webBrowser;
            try {
                webBrowser = new JWebBrowser();
            }
            catch (Throwable t) {
                webBrowser = null;
            }
            this.webBrowser = webBrowser;
            if (webBrowser != null) {
                Component webComponent = new NativeComponentWrapper(webBrowser).createEmbeddableComponent();
                mapPane.add(webComponent, JLayeredPane.DEFAULT_LAYER); // a böngésző a méretezett pane-re kerül
                webComponent.setBounds((-1 * (MAP_WIDTH / 2)) + (RADAR_SIZE / 2), (-1 * (MAP_HEIGHT / 2)) + (RADAR_SIZE / 2), MAP_WIDTH, MAP_HEIGHT); // és a pozíciója úgy van beállítva, hogy a Google reklám ne látszódjon
            }
        }
        else {
            webBrowser = null;
        }
        
        if (webBrowser == null) {
            lbErr.setText("<html><p style=\"text-align:center\">A térkép nincs támogatva.<br>A JRE frissítése segíthet.</p></html>");
        }
        
        setResizable(false); // ablak átméretezésének tiltása
        lbErr.setVisible(false); // hibaüzenet elrejtése a pack hívása előtt, hogy ne vegye számításba
        pack(); // ablakméret minimalizálása
        lbErr.setVisible(true); // hibaüzenet megjelenítése
        mapPane.setVisible(false); // térkép láthatatlanná tétele, míg nem tölt be
        
        if (webBrowser != null) {
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

                private boolean indApp = true, errRemoved = false, fired = false;

                private boolean test = false; // teszt annak kiderítésére, hogy betöltődött-e a Google Map

                @Override
                public void loadingProgressChanged(final WebBrowserEvent e) {
                    if (!errRemoved) { // hibaüzenet eltávolítása és indikátor megjelenítése, mivel van böngésző támogatás
                        errRemoved = true;
                        remove(lbErr);
                    }
                    if (indApp) { // indikátor megjelenítése, ha még nem látszik
                        indApp = false;
                        pInd.setVisible(true);
                    }
                    repaint(); // egyes rendszereken (Windows) nem minden esetben frissül le a panelcsere
                    if (e.getWebBrowser().getLoadingProgress() == 100) { // ha betöltődött az oldal
                        // ciklus amíg nincs a térkép betöltve:
                        new Thread(new Runnable() {

                            private boolean isIdAvailable(String id) {
                                String val = "document.getElementById('" + id + "').innerHTML";
                                val = "return " + val + " != null && " + val + " != '';";
                                Object ret = e.getWebBrowser().executeJavascriptWithResult(val);
                                if (ret == null) return false;
                                return Boolean.valueOf(ret.toString());
                            }

                            @Override
                            public void run() {
                                Date startDate = new Date(); // inicializálás kezdetének ideje
                                do {
                                    try {
                                        SwingUtilities.invokeAndWait(new Runnable() {

                                            @Override
                                            public void run() {
                                                e.getWebBrowser().executeJavascript(createInitScript()); // térkép inicializálás
                                                test = isIdAvailable("map_canvas");
                                            }

                                        });
                                        if (new Date().getTime() - startDate.getTime() > 10000) {
                                            test = false; // újratesztelés a legközelebbi betöltéskor
                                            indApp = true; // indikátor megjelenítése a legközelebbi betöltéskor
                                            pInd.setVisible(false); // indikátor elrejtése ...
                                            mapPane.setVisible(false); // ... térkép elrejtése ...
                                            lbWarn.setVisible(true); // ... és figyelmeztető üzenet megjelenítése, mert nem tudott betöltődni a térkép
                                            break; // ha 10 mp alatt nem sikerült inicializálni, feladja és kilép a ciklusból
                                        }
                                        Thread.sleep(100); // újratesztelés kicsit később
                                    }
                                    catch (Exception ex) {
                                        ;
                                    }
                                } while (!test);
                                if (test) { // ha sikerült a térkép betöltése
                                    pInd.setVisible(false); // indikátor eltüntetése
                                    mapPane.setVisible(true); // térkép megjelenítése
                                    // és adatok frissítése, hátha változtak idő közben:
                                    setArrow(ARROW.getRotation());
                                    setPosition(position);
                                    setFade(fadeEnabled);
                                }
                                if (!fired) { // csak az első betöltéskor van eseménykezelés
                                    fired = true;
                                    if (callback == null) setVisible(true); // ablak megjelenítése, ha nincs eseményfigyelő
                                    else callback.loadFinished(MapDialog.this); // egyébként eseményfigyelő futtatása
                                }
                            }

                        }).start();
                    }
                }

            });
        }
        // a program leállása előtt az ideignlenes könyvtár rekurzív törlése
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            
            @Override
            public void run() {
                delete(TMP_DIR);
            }
            
        }));
    }
    
    /**
     * Hibaüzenet eltüntetése és térkép újratöltése, ha legutóbb nem sikerült betölteni.
     */
    private void reload() {
        if (!lbWarn.isVisible()) return;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lbWarn.setVisible(false);
                webBrowser.setHTMLContent(HTML_SOURCE);
            }
            
        });
    }

    /**
     * Megjeleníti vagy elrejti az ablakot.
     * Ha megjelenítést kértek, és nem sikerült leutóbb betölteni a térképet, újratöltés.
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) reload();
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
     * @param latitude szélességi fok
     * @param longitude hosszúsági fok
     * @param altitude magasság méterben
     */
    public void setPosition(double latitude, double longitude, double altitude) {
        setPosition(new Point3D(latitude, longitude, altitude));
    }
    
    /**
     * A térkép pozíciójának beállítása.
     * JavaScript alapú metódus.
     * @param pos GPS koordináta
     */
    public void setPosition(final Point3D pos) {
        if (webBrowser == null) return;
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                position = pos;
                webBrowser.executeJavascript("map.setCenter(new google.maps.LatLng(" + position.X + ", " + position.Y + ")); var tag = document.getElementById('info'); tag.style.visibility = '" + (pos == null ? "hidden" : "visible") + "'; tag.innerHTML = '" + (pos == null ? "" : "W " + DF.format(pos.X) + "° H " + DF.format(pos.Y) + "° " + DF2.format(pos.Z) + " m") + "';");
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
        if (webBrowser == null) return;
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
     * Szürke átfedés engedélyezése illetve tiltása a térképen.
     * JavaScript és CSS 3 alapú metódus.
     */
    public void setFade(final boolean enabled) {
        if (webBrowser == null) return;
        fadeEnabled = enabled;
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                webBrowser.executeJavascript("document.getElementById('map_canvas').className = 'fadeprep" + (enabled ? " fadeon" : "") + "';");
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
                                radar.setFade(angle % 3 == 0);
                            }
                            
                        }, 5000, 1000); // ... 5 másodperccel később másodpercenként változik az irány és az átfedés ki/be kapcsol
                        radar.setPosition(47.35021, 19.10236, -100); // a hely Dunaharaszti egyik utcája
                        radar.setFade(true);
                    }
                    
                }, null);
                
                radar.setVisible(true); // az ablak megjelenítése azonnal tesztelés céljából
                
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
