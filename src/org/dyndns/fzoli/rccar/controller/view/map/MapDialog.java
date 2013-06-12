package org.dyndns.fzoli.rccar.controller.view.map;

import chrriis.dj.nativeswing.NSComponentOptions;
import chrriis.dj.nativeswing.NativeComponentWrapper;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.NativeInterfaceAdapter;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.controller.Config;
import static org.dyndns.fzoli.rccar.controller.ControllerModels.getData;
import org.dyndns.fzoli.rccar.controller.ControllerWindows;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_MAP;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;
import static org.dyndns.fzoli.rccar.controller.Main.getString;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.view.AbstractDialog;
import org.dyndns.fzoli.rccar.controller.view.ControllerFrame;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.controller.HostState;
import static org.dyndns.fzoli.util.Folders.delete;
import org.imgscalr.Scalr;

/**
 * Térkép ablak.
 * Google Map alapú térkép.
 * Natív böngésző segítségével jelenik meg, de ha a térkép betöltése nem sikerül,
 * a felhasználó kérésére megjelenik egy Swing alapú iránytű.
 * @author zoli
 */
public class MapDialog extends AbstractDialog {
    
    /**
     * A térkép pozíciója.
     */
    private Point3D position;
    
    /**
     * Új sor jel az aktuális rendszeren.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * Fok-koordináta formázó az információs panelhez.
     */
    private static final DecimalFormat DF = new DecimalFormat("0.00");
    
    /**
     * Magasság-koordináta formázó az információs panelhez.
     */
    private static final DecimalFormat DF2 = new DecimalFormat("0.##");
    
    /**
     * A térképhez tartozó méretek.
     */
    private static final int MAP_WIDTH = 400, MAP_HEIGHT = 300, RADAR_SIZE = 200, ARROW_SIZE = 30;
    
    /**
     * A térkép nyila.
     */
    private final MapArrow ARROW = new MapArrow(ARROW_SIZE);
    
    /**
     * Ideiglenes könyvtár.
     */
    private static final File TMP_DIR = createTmpDirectory();
    
    /**
     * A nyilat ábrázoló png kép helye az ideiglenes könyvtárban.
     */
    private static final File ARROW_FILE = new File(TMP_DIR, "arrow.png");
    
    /**
     * Az iránytűt ábrázoló png kép helye az ideiglenes könyvtárban.
     */
    private static final File COMPASS_FILE = new File(TMP_DIR, "compass.png");
    
    /**
     * Az ablak méretéhez igazított iránytűt ábrázoló kép.
     */
    private static final BufferedImage IMG_COMPASS = Scalr.resize(R.getImage("compass.png"), Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, RADAR_SIZE, Scalr.OP_ANTIALIAS);
    
    /**
     * A térképet megjelenítő HTML kód.
     */
    private final String HTML_SOURCE =
            "<!DOCTYPE html>" + LS +
            "<html>" + LS +
            "  <head>" + LS +
            "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\" />" + LS +
            "    <style type=\"text/css\">" + LS +
            "      html, body { height: 100%; overflow: hidden }" + LS +
            "      body { margin: 0; padding: 0 }" + LS +
            "      div#map_canvas, div#border { width: " + MAP_WIDTH + "px; height: " + MAP_HEIGHT + "px }" + LS +
            "      div#border, div#arrow, div#info, div#compass { position: fixed }" + LS +
            "      div#border { z-index: 1000004; top: 0px; left: 0px }" + LS +
            "      div#compass { z-index: 1000002; top: " + (MAP_HEIGHT - (1.25 * RADAR_SIZE)) + "px; left: " + (MAP_WIDTH - (1.5 * RADAR_SIZE)) + "px; width: " + RADAR_SIZE + "px; height: " + RADAR_SIZE + "px; background-color: white; background-image: url('" + fileToUrl(COMPASS_FILE) + "'); background-size: " + RADAR_SIZE + "px " + RADAR_SIZE + "px; background-repeat: no-repeat }" + LS +
            "      div#info { z-index: 1000003; cursor: default; font-family: \"Arial\"; font-size: 12px; text-shadow: -1px 0 black, 0 1px black, 1px 0 black, 0 -1px black; color: white; visibility: hidden; left: " + (((MAP_WIDTH / 2)) - (RADAR_SIZE / 2)) + "px; top: " + (((MAP_HEIGHT / 2)) - (RADAR_SIZE / 2)) + "px; padding: 2px; background-color: rgba(0, 0, 0, 0.3); filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#4C000000,endColorstr=#4C000000); -ms-filter: \"progid:DXImageTransform.Microsoft.gradient(startColorstr=#4C000000,endColorstr=#4C000000)\" }" + LS +
            "      div#arrow { z-index: 1000003; top: " + ((MAP_HEIGHT / 2) - (ARROW_SIZE / 2)) + "px; left: " + ((MAP_WIDTH / 2) - (ARROW_SIZE / 2)) + "px; width: " + ARROW_SIZE + "px; height: " + ARROW_SIZE + "px }" + LS +
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
            "    <div id=\"compass\" style=\"visibility: hidden\"></div>" + LS +
            "  </body>" + LS +
            "</html>";
    
    /**
     * A natív böngésző.
     */
    private final JWebBrowser WEB_BROWSER;
    
    /**
     * Sikertelen betöltődés üzenetet jelző panel.
     * Kattintásra megpróbálja újratölteni a térképet.
     */
    private final JPanel PANEL_WARN;
    
    /**
     * Ha a térkép nem elérhető, ez a panel jelenik meg, amin
     * jelezve van, hogy a térkép nem érhető el vagy inicializálás van folyamatban.
     */
    private final JPanel PANEL_PRE;
    
    /**
     * Az iránytű módban megjelenő címke, ami az iránytű nyilát jeleníti meg.
     */
    private final JLabel LB_ARROW = new JLabel(new ImageIcon(ARROW));
    
    /**
     * Figyelmeztető üzenet a sikertelen betöltéshez.
     */
    private final JLabel LB_WARN = new JLabel(getWarningLabelText(), SwingConstants.CENTER);
    
    /**
     * Böngésző támogatás detektálás előtt látható címke.
     */
    private final JLabel LB_PRE_LOADING = new JLabel(getPreLoadingLabelText(), SwingConstants.CENTER);
    
    /**
     * Betöltés szöveg címkéje.
     */
    private final JLabel LB_LOADING = new JLabel(getString("loading"), SwingConstants.CENTER);
    
    /**
     * Az iránytű módba való váltást elvégző komponenseket tároló lista.
     * Nyelv módosulás során a címkék felirata frissül.
     */
    private final List<JLabel> SWITCHERS = new ArrayList<JLabel>();
    
    /**
     * Megadja, hogy a fade effekt engedélyezve van-e.
     */
    private boolean fadeEnabled = false;
    
    /**
     * Megadja, hogy elérhető-e a böngésző.
     * Amíg nem tudni, null az értéke.
     */
    private Boolean enabled;
    
    /**
     * Megadja, hogy tíltva van-e a térkép.
     * Akkor fordulhat elő, ha bezárul a natív interfész valami oknál fogva.
     */
    private boolean disabled = false;
    
    /**
     * Megadja, hogy az ablak iránytű módban van-e.
     */
    private boolean compassMode = false;
    
    /**
     * Megadja, hogy az előtöltő üzenet eltűnt-e.
     */
    private boolean preloadRemoved = false;
    
    static {
        // az ideiglenes könyvtár létrehozása és feltöltése
        chkTmpFiles();
        // a program leállása előtt az ideiglenes könyvtár rekurzív törlése
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            
            @Override
            public void run() {
                delete(TMP_DIR);
            }
            
        }));
    }
    
    /**
     * Ideiglenes könyvtárhoz ad referenciát.
     */
    private static File createTmpDirectory() {
        try {
            // megkísérli a rendszerhez igazított helyre létrehozni a tmp könyvtárat
            return Files.createTempDirectory("mobilerc").toFile();
        }
        catch (Throwable ex) {
            // ha bármi okból nem sikerült létrehozni a tmp könyvtárat az ajánlott helyre
            // pl. JRE7 alatt (mondjuk JRE6) a Files osztály nem létezik
            // akkor használja a jelenlegi könyvtárat a tmp fájl létrehozásához
            int i = 0;
            File f;
            // keres egy nem létező tmp könyvtárat
            while ((f = new File(Config.ROOT, "tmp" + i)).exists()) {
                if (i == Integer.MAX_VALUE) break; // végtelen ciklus elkerülése csak a rend kedvéért
                i++;
            }
            return f;
        }
    }
    
    /**
     * Ha az ideiglenes könyvtár nem létezik, létrehozza és feltölti.
     */
    private static void chkTmpFiles() {
        if (!TMP_DIR.isDirectory()) TMP_DIR.mkdir(); // tmp könyvtár létrehozása, ha nem létezik
        if (!COMPASS_FILE.isFile()) writeImage(IMG_COMPASS, COMPASS_FILE); // a tmp könyvtárba menti az iránytű átméretezett képét, ha még nem létezik
    }
    
    public MapDialog(ControllerFrame owner, ControllerWindows windows) {
        this(owner, windows, null, true);
    }
    
    public MapDialog(MapLoadListener callback, ControllerWindows windows) {
        this(null, windows, callback, true);
    }
    
    public MapDialog(ControllerFrame owner, ControllerWindows windows, MapLoadListener callback, boolean enabled) {
        this(owner, windows, callback, enabled, false);
    }
    
    public MapDialog(final ControllerFrame owner, ControllerWindows windows, final MapLoadListener callback, final boolean enabled, final boolean compassMode) {
        super(owner, getString("map"), windows);
        getData().setMapDialog(this);
        setIconImage(IC_MAP.getImage());
        getContentPane().setBackground(Color.WHITE);
        
        final JLayeredPane compassPane = new JLayeredPane(); // az iránytű módhoz használt többrétegű komponens
        compassPane.setPreferredSize(new Dimension(RADAR_SIZE, RADAR_SIZE)); // a méret megadása
        
        JLabel lbCompass = new JLabel(new ImageIcon(IMG_COMPASS)); // az iránytűt megjelenítő címke
        compassPane.add(lbCompass, JLayeredPane.DEFAULT_LAYER);
        lbCompass.setBounds(0, 0, RADAR_SIZE, RADAR_SIZE); // teljes helykitöltéssel
        
        final BufferedImage imgClose = Scalr.resize(R.getImage("close-icon.png"), Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, RADAR_SIZE / 10, Scalr.THRESHOLD_QUALITY_BALANCED); // átméretezett bezárás ikon
        final JLabel lbClose = new JLabel(new ImageIcon(imgClose)); // iránytű mód elhagyása "gomb"
        compassPane.add(lbClose, JLayeredPane.POPUP_LAYER); // az iránytű kép fölé
        lbClose.setBounds(RADAR_SIZE - imgClose.getWidth() - 6, 5, imgClose.getWidth(), imgClose.getHeight()); // jobb, felső sarokba 5 x 5 behúzással
        lbClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // hand cursor a bezáráshoz
        
        compassPane.add(LB_ARROW, JLayeredPane.DRAG_LAYER); // az iránytű nyila
        LB_ARROW.setBounds(RADAR_SIZE / 2 - ARROW_SIZE / 2, RADAR_SIZE / 2 - ARROW_SIZE / 2, ARROW_SIZE, ARROW_SIZE); // középen
        
        getContentPane().add(compassPane, BorderLayout.EAST); // az iránytű mód komponense jobb oldalra kerül, hogy megjelenve a hibaüzenet panelek ne látszódjanak
        compassPane.setVisible(false); // kezdetben az iránytű mód inaktív, tehát a panel nem látszódik
        
        final JLayeredPane mapPane = new JLayeredPane(); // a komponens pontos pozíciójának beállítására használom
        mapPane.setPreferredSize(new Dimension(RADAR_SIZE, RADAR_SIZE)); // a méret megadása
        
        // indikátor jelenik meg, míg a térkép töltődik
        final JPanel pInd = createPanel(mapPane, compassPane, LB_LOADING, R.getIndicatorIcon(), false, false);
        getContentPane().add(pInd, BorderLayout.SOUTH);
        pInd.setVisible(false);
        
        // figyelmeztető üzenet jelenik meg, ha a térkép betöltése nem sikerült
        PANEL_WARN = createPanel(mapPane, compassPane, LB_WARN, R.getErrorIcon(), true, true);
        PANEL_WARN.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent ev) {
                reload();
            }

        });
        getContentPane().add(PANEL_WARN, BorderLayout.WEST);
        PANEL_WARN.setVisible(false);
        
        // kezdetben úgy tesz, mint ha nem lenne böngésző támogatás
        PANEL_PRE = createPanel(mapPane, compassPane, LB_PRE_LOADING, R.getWarningIcon(), true, false);
        getContentPane().add(PANEL_PRE, BorderLayout.NORTH); // a figyelmeztető üzenet az ablak felső részére kerül
        
        getContentPane().add(mapPane, BorderLayout.CENTER); // a térkép középre igazítva jelenik meg

        // az iránytű mód panelje felüldefiniálja az alatta lévő komponens egér-eseményfigyelőjét
        // és ha a bezárás "gombra" kattintottak, megpróbálja újra betölteni a térképet és eltünteti a swing alapú iránytűt
        // ez azt eredményezi, hogy újra előjön a betöltést jelző indikátor panel
        compassPane.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (lbClose.getBounds().contains(e.getPoint())) {
                    if (WEB_BROWSER == null || !preloadRemoved) PANEL_PRE.setVisible(true);
                    reload(true);
                    compassPane.setVisible(false);
                    MapDialog.this.compassMode = false;
                }
            }
            
        });
        
        if (enabled) {
            JWebBrowser webBrowser;
            try {
                webBrowser = new JWebBrowser(NSComponentOptions.destroyOnFinalization());
            }
            catch (Throwable t) {
                webBrowser = null;
            }
            this.WEB_BROWSER = webBrowser;
            if (webBrowser != null) {
                Component webComponent = new NativeComponentWrapper(webBrowser).createEmbeddableComponent();
                mapPane.add(webComponent, JLayeredPane.DEFAULT_LAYER); // a böngésző a méretezett pane-re kerül
                webComponent.setBounds((-1 * (MAP_WIDTH / 2)) + (RADAR_SIZE / 2), (-1 * (MAP_HEIGHT / 2)) + (RADAR_SIZE / 2), MAP_WIDTH, MAP_HEIGHT); // és a pozíciója úgy van beállítva, hogy a Google reklám ne látszódjon
            }
        }
        else {
            WEB_BROWSER = null;
        }
        
        this.enabled = WEB_BROWSER != null;
        if (WEB_BROWSER == null) {
            LB_PRE_LOADING.setText(getNoSupportLabelText());
        }
        
        setResizable(false); // ablak átméretezésének tiltása
        PANEL_PRE.setVisible(false); // hibaüzenet elrejtése a pack hívása előtt, hogy ne vegye számításba
        pack(); // ablakméret minimalizálása
        PANEL_PRE.setVisible(true); // hibaüzenet megjelenítése
        mapPane.setVisible(false); // térkép láthatatlanná tétele, míg nem tölt be
        
        if (compassMode) {
            PANEL_PRE.setVisible(false);
            compassPane.setVisible(true);
            this.compassMode = true;
        }
        
        if (WEB_BROWSER != null) {
            // a natív böngésző lecsupaszítása
            WEB_BROWSER.setBarsVisible(false);
            WEB_BROWSER.setButtonBarVisible(false);
            WEB_BROWSER.setLocationBarVisible(false);
            WEB_BROWSER.setMenuBarVisible(false);
            WEB_BROWSER.setStatusBarVisible(false);
            WEB_BROWSER.setJavascriptEnabled(true);
            WEB_BROWSER.setDefaultPopupMenuRegistered(false);

            // HTML forráskód betöltése, ha a térképre szükség van
            if (!compassMode) WEB_BROWSER.setHTMLContent(HTML_SOURCE);
            
            // ha a natív interfészt bezárják, térkép inaktiválása és hiba közlése
            NativeInterface.addNativeInterfaceListener(new NativeInterfaceAdapter() {

                @Override
                public void nativeInterfaceClosed() {
                    if (disabled || disposed) return;
                    disabled = true;
                    relocalize();
                    mapPane.setVisible(false);
                    getContentPane().remove(mapPane);
                    getContentPane().add(PANEL_PRE, BorderLayout.CENTER);
                    PANEL_PRE.setVisible(true);
                }
                
            });
            
            // várakozás a térkép api betöltésére
            WEB_BROWSER.addWebBrowserListener(new WebBrowserAdapter() {

                private boolean indApp = true, fired = false;

                private boolean test = false; // teszt annak kiderítésére, hogy betöltődött-e a Google Map

                @Override
                public void loadingProgressChanged(final WebBrowserEvent e) {
                    if (!preloadRemoved) { // hibaüzenet eltávolítása és indikátor megjelenítése, mivel van böngésző támogatás
                        preloadRemoved = true;
                        remove(PANEL_PRE);
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
                                                try {
                                                    e.getWebBrowser().executeJavascript(createInitScript()); // térkép inicializálás
                                                    test = isIdAvailable("map_canvas");
                                                }
                                                catch (Exception ex) {
                                                    ;
                                                }
                                            }

                                        });
                                        if (new Date().getTime() - startDate.getTime() > 10000) {
                                            test = false; // újratesztelés a legközelebbi betöltéskor
                                            indApp = true; // indikátor megjelenítése a legközelebbi betöltéskor
                                            pInd.setVisible(false); // indikátor elrejtése ...
                                            mapPane.setVisible(false); // ... térkép elrejtése ...
                                            PANEL_WARN.setVisible(true); // ... és figyelmeztető üzenet megjelenítése, mert nem tudott betöltődni a térkép
                                            break; // ha 10 mp alatt nem sikerült inicializálni, feladja és kilép a ciklusból
                                        }
                                        Thread.sleep(100); // újratesztelés kicsit később
                                    }
                                    catch (Exception ex) {
                                        ;
                                    }
                                } while (!test);
                                if (test) { // ha sikerült a térkép betöltése
                                    MapDialog.this.compassMode = false; // extra stabilitás érdekében az iránytű módból kilépés ...
                                    compassPane.setVisible(false); // ... és az iránytű panel elrejtése
                                    pInd.setVisible(false); // indikátor eltüntetése
                                    mapPane.setVisible(true); // térkép megjelenítése
                                    // és adatok frissítése, hátha változtak idő közben:
                                    setArrow(ARROW.getRotation());
                                    setPosition(position);
                                    setFade(fadeEnabled);
                                    executeJavascript( // az összes HTML teg onClick eseményét blokkolja, ezzel kivédve a TAB + ENTER billentyűzet-kombinációval való URL hívás
                                      "var ls = document.getElementsByTagName('*');" + LS +
                                      "for (var i = 0, s = ls.length; i < s; i++) {" + LS +
                                      "    ls[i].onclick = function() {" + LS +
                                      "        return false;" + LS +
                                      "    }" + LS +
                                      "}"
                                    );
                                }
                                if (!fired) { // csak az első betöltéskor van eseménykezelés
                                    fired = true;
                                    if (callback != null) callback.loadFinished(MapDialog.this); // eseményfigyelő futtatása, ha van
                                }
                            }

                        }).start();
                    }
                }

            });
        }
    }
    
    /**
     * Gyárt egy panelt, amin egy ikon alatt egy címke látható.
     * @param mapPane a komponens, amire kerül a panel
     * @param compassPane az iránytűt ábrázoló komponens, ami hiba esetén megjelenik a felhasználó kérésére
     * @param lb a címke
     * @param icon az ikon
     * @param switching true esetén az iránytű módba való váltás engedélyezve van
     * @param hand true esetén hand cursor, egyébként default cursor
     */
    private JPanel createPanel(final JLayeredPane mapPane, final JLayeredPane compassPane, final JLabel lb, final Icon icon, final boolean switching, final boolean hand) {
        return new JPanel() {
            {
                setCursor(Cursor.getPredefinedCursor(hand ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                final JLabel lbIcon = new JLabel(icon);
                GridBagConstraints c = new GridBagConstraints();
                setLayout(new GridBagLayout());
                setPreferredSize(mapPane.getPreferredSize());
                setOpaque(false);
                c.gridy = 1;
                add(lbIcon, c);
                c.gridy = 2;
                c.weightx = 1;
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = new Insets(5, 5, 5, 5);
                add(lb, c);
                if (switching) {
                    final JLabel lbSwitch = new JLabel(getString("compass_mode"), SwingConstants.CENTER) {
                        {
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            setForeground(new Color(20, 160, 20));
                            addMouseListener(new MouseAdapter() {

                                Font original;

                                @Override
                                public void mouseEntered(MouseEvent e) {
                                    original = e.getComponent().getFont();
                                    Map attributes = original.getAttributes();
                                    attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
                                    e.getComponent().setFont(original.deriveFont(attributes));
                                }

                                @Override
                                public void mouseExited(MouseEvent e) {
                                    e.getComponent().setFont(original);
                                }

                                @Override
                                public void mousePressed(MouseEvent e) {
                                    if (WEB_BROWSER == null || !preloadRemoved) PANEL_PRE.setVisible(false);
                                    compassPane.setVisible(true);
                                    compassMode = true;
                                }

                            });
                        }
                    };
                    final JPanel pDummy = new JPanel() {

                        {
                            setOpaque(false);
                        }

                        @Override
                        public Dimension getPreferredSize() {
                            return lbSwitch.getSize();
                        }
                        
                        @Override
                        public Dimension getMinimumSize() {
                            return getPreferredSize();
                        }
                        
                    };
                    c = new GridBagConstraints();
                    c.weighty = 1;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    c.insets = new Insets(2, 0, 0, 0);
                    c.anchor = GridBagConstraints.FIRST_LINE_START;
                    add(pDummy, c);
                    c.gridy = 3;
                    c.insets = new Insets(0, 0, 2, 0);
                    c.anchor = GridBagConstraints.LAST_LINE_START;
                    add(lbSwitch, c);
                    SWITCHERS.add(lbSwitch);
                }
            }
        };
    }
    
    /**
     * Betöltődés megkezdés előtti szöveg a címkéhez.
     */
    private String getPreLoadingLabelText() {
        return "<html><p style=\"text-align:center\">" + getString("map_preload") + "</p></html>";
    }
    
    /**
     * Nincs térképtámogatás szöveg a címkéhez.
     */
    private String getNoSupportLabelText() {
        return "<html><p style=\"text-align:center\">" + getString("map_unsupported") + "</p></html>";
    }
    
    /**
     * Betöltés hiba szövege a címkéhez.
     */
    private String getWarningLabelText() {
        return "<html><p style=\"text-align:center; color:red; font-weight: 900\">" + getString("map_load_error") + "</p><br><p style=\"text-align:center\">" + getString("click_to_reload") + "</p></html>";
    }
    
    /**
     * A felület feliratait újra beállítja.
     * Ha a nyelvet megváltoztatja a felhasználó, ez a metódus hívódik meg.
     */
    @Override
    public void relocalize() {
        setTitle(getString("map"));
        LB_LOADING.setText(getString("loading"));
        LB_WARN.setText(getWarningLabelText());
        if (WEB_BROWSER != null && !preloadRemoved) LB_PRE_LOADING.setText(getPreLoadingLabelText());
        else LB_PRE_LOADING.setText(getNoSupportLabelText());
        for (JLabel switcher : SWITCHERS) {
            switcher.setText(getString("compass_mode"));
        }
    }
    
    /**
     * Hibaüzenet eltüntetése és térkép újratöltése, ha legutóbb nem sikerült betölteni.
     */
    private void reload() {
        reload(false);
    }
    
    /**
     * Hibaüzenet eltüntetése és térkép újratöltése, ha legutóbb nem sikerült betölteni.
     */
    private void reload(boolean force) {
        if (!force && !PANEL_WARN.isVisible()) return;
        if (disposed || disabled) return;
        if (WEB_BROWSER == null) return;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                PANEL_WARN.setVisible(false);
                WEB_BROWSER.setHTMLContent(HTML_SOURCE);
            }
            
        });
    }

    /**
     * Megjeleníti vagy elrejti az ablakot.
     * Ha megjelenítést kértek, és nem sikerült leutóbb betölteni a térképet, újratöltés.
     * Ha az ablak látható és elrejtést kértek, jelzi a konténernek, hogy elrejtődik.
     */
    @Override
    public void setVisible(boolean b) {
        boolean v = isVisible();
        super.setVisible(b);
        if (b && !v && !compassMode) reload();
        if (!b && v) getControllerWindows().onMapHiding();
    }

    /**
     * Megadja, hogy a térkép iránytű módban van-e.
     */
    public boolean isCompassMode() {
        return compassMode;
    }
    
    /**
     * Megadja, hogy a térkép elérhető-e.
     * @return null, ha még nem tudni; true, ha elérhető; egyébként false
     */
    public Boolean isMapEnabled() {
        return enabled;
    }

    /**
     * Megadja, hogy az áttünés aktív-e.
     */
    public boolean isFade() {
        return fadeEnabled;
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
     * @param pos GPS koordináta, null referencia esetén térkép helyett iránytű jelenik meg
     */
    public void setPosition(final Point3D pos) {
        if (WEB_BROWSER == null || disposed || disabled) return;
        chkTmpFiles();
        position = pos;
        executeJavascript("map.setCenter(new google.maps.LatLng(" + (position == null ? 1 : position.X) + ", " + (position == null ? 0 : position.Y) + ")); var tag = document.getElementById('info'); tag.style.visibility = '" + (pos == null ? "hidden" : "visible") + "'; tag.innerHTML = '" + (pos == null ? "" : "W " + DF.format(pos.X) + "° H " + DF.format(pos.Y) + "°" + (pos.Z == 0 ? "" : " " + DF2.format(pos.Z) + " m")) + "'; document.getElementById('compass').style.visibility = '" + (pos != null ? "hidden" : "visible") + "';");
    }
    
    /**
     * Az iránymutató nyíl irányának megadása.
     * Legenerálja a képet, elmenti az ideignlenes könyvtárba, majd
     * JavaScript segítségével beállítja az új képet.
     * @param rotation északtól való eltérés, vagy null, ha nincs irány megadva
     */
    public void setArrow(final Double rotation) {
        if (disposed || disabled) return;
        ARROW.setRotation(rotation); // nyíl frissítése
        LB_ARROW.repaint(); // swing alapú nyíl újrarajzolása
        if (WEB_BROWSER == null) return;
        chkTmpFiles();
        writeImage(ARROW, ARROW_FILE); // png formátumban a nyíl mentése a tmp könyvtárba
        executeJavascript("document.getElementById('arrow').innerHTML = '<img src=\"" + fileToUrl(ARROW_FILE) + "?nocache=" + Math.random() + "\" />';"); // a kép frissítése a böngészőben
    }
    
    /**
     * Szürke átfedés engedélyezése illetve tiltása a térképen.
     * JavaScript és CSS 3 alapú metódus.
     */
    public void setFade(final boolean enabled) {
        if (WEB_BROWSER == null || disposed || disabled) return;
        fadeEnabled = enabled;
        executeJavascript("document.getElementById('map_canvas').className = 'fadeprep" + (enabled ? " fadeon" : "") + "';");
    }
    
    /**
     * JavaScript kódot futtat le.
     * Nem ellenőrzött metódus, mivel a hívó metódusban van az ellenőrzés.
     */
    private void executeJavascript(final String script) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                WEB_BROWSER.executeJavascript(script);
            }
            
        });
    }
    
    /**
     * PNG formátumban menti el a képet a megadott fájlba.
     */
    private static void writeImage(BufferedImage img, File f) {
        try {
            ImageIO.write(img, "png", f);
        }
        catch (Exception ex) {
            ;
        }
    }
    
    /**
     * HTML kódból elérhető URL-t ad vissza.
     */
    private static String fileToUrl(File f) {
        try {
            return f.toURI().toURL().toString();
        }
        catch (Exception ex) {
            return "";
        }
    }
    
    /**
     * A Google Map inicializáló JavaScript kódja.
     */
    private String createInitScript() {
        return "var mapOptions = {" + LS +
               "  zoom: 17," + LS +
               "  center: new google.maps.LatLng(" + (position == null ? 1 : position.X) + ", " + (position == null ? 0 : position.Y) + ")," + LS +
               "  disableDefaultUI: true," + LS +
               "  mapTypeId: google.maps.MapTypeId.HYBRID" + LS +
               "}" + LS +
               "var map = new google.maps.Map(document.getElementById(\"map_canvas\"), mapOptions);";
    }

    /**
     * Az ablak típusa: térkép.
     */
    @Override
    public WindowType getWindowType() {
        return WindowType.MAP;
    }
    
    /**
     * Frissíti a térkép összes adatát az adatmodel alapján.
     * @see #refreshArrow()
     * @see #refreshFade()
     * @see #refreshPosition()
     */
    public void refresh() {
        refreshArrow();
        refreshFade();
        refreshPosition();
    }
    
    /**
     * Beállítja az áttünést az adatmodel alapján.
     * Használt getterek:
     * - {@link ClientControllerData#isUp2Date()}
     * - {@link ClientControllerData#isHostUnderTimeout()}
     * - {@link ClientControllerData#isVehicleConnected()}
     * - {@link ClientControllerData#isConnected()}
     */
    public void refreshFade() {
        setFade(getData().isUp2Date() == null || !getData().isUp2Date() || !getData().isVehicleAvailable(false, false));
    }
    
    /**
     * Frissíti a nyilat az adatmodel alapján.
     * Használt getterek:
     * - {@link ClientControllerData#getHostState()}
     */
    public void refreshArrow() {
        HostState hs = getData().getHostState();
        Double oldVal = ARROW.getRotation();
        Double newVal = hs == null || hs.AZIMUTH == null ? null : hs.AZIMUTH.doubleValue();
        if (oldVal == null && newVal == null) return;
        if (oldVal != null && newVal == oldVal) return;
        setArrow(newVal);
    }
    
    /**
     * Frissíti a pozíciót az adatmodel alapján.
     * Használt getterek:
     * - {@link ClientControllerData#getHostState()}
     */
    public void refreshPosition() {
        HostState hs = getData().getHostState();
        setPosition(hs == null ? null : hs.LOCATION);
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
                final MapDialog map = new MapDialog(null, null, new MapLoadListener() {

                    @Override
                    public void loadFinished(final MapDialog map) {
                        map.setArrow(null); // kezdetben nincs irány ...
                        timer.schedule(new TimerTask() {
                            
                            double angle = 0;
                            
                            @Override
                            public void run() {
                                angle += 10;
                                map.setArrow(angle);
                                map.setFade(angle % 3 == 0);
                            }
                            
                        }, 5000, 1000); // ... 5 másodperccel később másodpercenként változik az irány és az átfedés ki/be kapcsol
                        map.setPosition(47.35021, 19.10236, -100); // a hely Dunaharaszti egyik utcája
                        map.setFade(true);
                    }
                    
                }, true, false);
                
                map.setVisible(true); // az ablak megjelenítése azonnal tesztelés céljából
                
                // az ablak bezárásakor:
                map.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        map.dispose(); // felszabadítás
                        timer.cancel(); // időzítő leállítása
                        System.exit(0); // kilépés a programból
                    }
                    
                });
            }
            
        });
        NativeInterface.runEventPump();
    }
    
}
