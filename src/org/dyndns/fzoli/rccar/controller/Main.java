package org.dyndns.fzoli.rccar.controller;

import chrriis.dj.nativeswing.swtimpl.NativeInterfaceAdapter;
import org.dyndns.fzoli.rccar.SplashScreenLoader;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;
import static org.dyndns.fzoli.rccar.SplashScreenLoader.setSplashMessage;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.socket.ConnectionHelper;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame.Status;
import org.dyndns.fzoli.rccar.controller.view.config.ConfigEditorFrame;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.ui.UIUtil;
import org.dyndns.fzoli.rccar.ui.UncaughtExceptionHandler;
import org.dyndns.fzoli.ui.LanguageChooserFrame;
import org.dyndns.fzoli.ui.OptionPane;
import org.dyndns.fzoli.ui.OptionPane.PasswordData;
import static org.dyndns.fzoli.rccar.ui.UIUtil.setSystemLookAndFeel;
import static org.dyndns.fzoli.rccar.ui.UIUtil.initNativeInterface;
import static org.dyndns.fzoli.rccar.ui.UIUtil.addNativeInterfaceListener;
import static org.dyndns.fzoli.rccar.ui.UIUtil.runNativeEventPump;
import org.dyndns.fzoli.ui.systemtray.SystemTrayIcon;
import static org.dyndns.fzoli.ui.systemtray.SystemTrayIcon.showMessage;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;
import static org.dyndns.fzoli.util.OSUtils.setApplicationName;
import org.dyndns.fzoli.ui.systemtray.MenuItem;
import static org.dyndns.fzoli.util.MacApplication.setDockIcon;

/**
 * A vezérlő indító osztálya.
 * @author zoli
 */
public class Main {
    
    /**
     * Új sor jel.
     */
    public static final String LS = System.getProperty("line.separator");
    
    /**
     * Callback, ami megjeleníti a kapcsolatbeállító ablakot.
     */
    private static final Runnable CALLBACK_SETTING = new Runnable() {

        @Override
        public void run() {
            // kapcsolatbeállító ablak megjelenítése
            showSettingFrame(false, null);
        }

    };
    
    /**
     * Callback, ami akkor fut le, ha ki szeretnének lépni a programból.
     */
    private static final Runnable CALLBACK_EXIT = new Runnable() {
        
        /**
         * Ha a kilépésre kattintottak.
         * A program azonnal végetér, ha nincs kiépítve kapcsolat,
         * egyébként megkérdezi a felhasználót, hogy biztos ki akar-e lépni
         * és csak akkor lép ki, ha Igen a válasza.
         */
        @Override
        public void run() {
            if (CONN.isConnected()) { // ha van kiépített kapcsolat
                // megkérdi, biztos-e a kilépésben
                int opt = OptionPane.showYesNoDialog(SELECTION_FRAME, getString("confirm_exit"), getString("confirmation"));
                // ha igen, akkor a program kilép
                if (opt == 0) {
                    exit();
                }
            }
            else { // ha nincs kiépített kapcsolat, a program kilép
                exit();
            }
        }

    };
    
    /**
     * Eseményfigyelő, ami akkor fut le, ha ki szeretnének lépni a programból.
     */
    private static final ActionListener AL_EXIT = new ActionListener() {
        
        @Override
        public void actionPerformed(ActionEvent e) {
            CALLBACK_EXIT.run();
        }

    };
    
    /**
     * A konfiguráció beállító ablak bezárásakor figyelmeztetést jelentít meg, ha kell.
     */
    private static final WindowAdapter WL_CFG = new WindowAdapter() {

        @Override
        public void windowClosed(WindowEvent e) {
            configAlert(false);
        }

    };
    
    /**
     * A Natív Interfész bezárásakor lefutó eseményfigyelő, ami a rendszerikont újrainicializálja AWT alapokon.
     */
    private static final NativeInterfaceAdapter NI_LISTENER = new NativeInterfaceAdapter() {

        private boolean closed = false;
        
        @Override
        public void nativeInterfaceClosed() {
            if (closed) return;
            closed = true;
            setSystemTrayIcon();
        }
        
    };
    
    /**
     * A konfigurációt tartalmazó objektum.
     */
    private static final Config CONFIG = Config.getInstance();
    
    /**
     * A szótár.
     */
    private static ResourceBundle STRINGS = createResource(CONFIG.getLanguage());
    
    /**
     * A híd szerverrel építi ki a kapcsolatot.
     */
    private static final ConnectionHelper CONN = new ConnectionHelper(CONFIG);
    
    /**
     * Ha az értéke true, akkor a program leállítás alatt van.
     * Ez esetben a kapcsolódáskezelő ablak nem jelenik meg, amikor megszakad a híddal a kapcsolat.
     */
    private static boolean exiting = false;
    
    /**
     * Nyelvkiválasztó ablak.
     */
    public static LanguageChooserFrame LNG_FRAME;
    
    /**
     * Konfiguráció-szerkesztő ablak.
     */
    private static ConfigEditorFrame CONFIG_EDITOR;
    
    /**
     * Kapcsolódásjelző- és kezelő ablak.
     */
    private static ConnectionProgressFrame PROGRESS_FRAME;
    
    /**
     * Járműválasztó ablak.
     */
    private static HostSelectionFrame SELECTION_FRAME;
    
    /**
     * A kiválasztott járműhöz tartozó ablakok konténere.
     */
    private static ControllerWindows CONTROLLER_WINDOWS;
    
    /**
     * A szerző dialógust megjelenítő menüelem, ami inaktív,
     * míg a dialógus látható.
     */
    private static MenuItem MI_AUTHOR;
    
    /**
     * Újrakapcsolódás menüopció.
     */
    private static MenuItem MI_RECONNECT;
    
    /**
     * Segédváltozó kapcsolódás kérés detektálására.
     */
    private static boolean connecting = false;
    
    /**
     * A program leállítása.
     * Akkor fut le, amikor a felhasználó ki szeretne lépni a programból.
     */
    private static void exit() {
        exiting = true;
        SystemTrayIcon.dispose();
        System.exit(0);
    }
    
    /**
     * Beállítja a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    private static void setExceptionHandler() {
        UncaughtExceptionHandler.apply(R.getIconImage());
    }
    
    /**
     * Beállítja a rendszerikont.
     */
    private static void setSystemTrayIcon() {
        if (SystemTrayIcon.init(!UIUtil.isNativeSwingAvailable()) && SystemTrayIcon.isSupported()) {
            // az ikon beállítása
            SystemTrayIcon.setIcon(getString("app_name"), R.getIconImage());
            
            // nyelv választó opció hozzáadása
            String lngText = getString("language");
            if (!lngText.equalsIgnoreCase("language")) lngText += " (language)";
            SystemTrayIcon.addMenuItem(lngText, R.getImage("languages.png"), new Runnable() {

                @Override
                public void run() {
                    if (LNG_FRAME != null) LNG_FRAME.setVisible(true);
                }

            });

            //szeparátor hozzáadása
            SystemTrayIcon.addMenuSeparator();

            // kapcsolatbeállítás opció hozzáadása
            SystemTrayIcon.addMenuItem(getString("connection_settings"), R.getImage("preferences.png"), CALLBACK_SETTING);

            // újrakapcsolódás opció hozzáadása
            MI_RECONNECT = SystemTrayIcon.addMenuItem(getString("reconnect"), R.getImage("connect.png"), new Runnable() {

                @Override
                public void run() {
                    reconnect();
                }

            });

            //szeparátor hozzáadása
            SystemTrayIcon.addMenuSeparator();

            // szerző opció hozzáadása
            MI_AUTHOR = SystemTrayIcon.addMenuItem(getString("author"), R.getQuestionImage(), new Runnable() {

                @Override
                public void run() {
                    UIUtil.showAuthorDialog(MI_AUTHOR, R.getIconImage());
                }
                
            });
            
            // kilépés opció hozzáadása
            SystemTrayIcon.addMenuItem(getString("exit"), R.getExitImage(), CALLBACK_EXIT);
        }
    }
    
    /**
     * A beállításkezelő ablakot jeleníti meg.
     * Ha a helyes konfiguráció kényszerített, az ablak bezárása után figyelmeztetés jelenhet meg a beállításokkal kapcsolatban.
     * @param force kényszerítve legyen-e a felhasználó helyes konfiguráció megadására
     * @param tabIndex a megjelenő lapfül
     */
    public static void showSettingFrame(boolean force, Integer tabIndex) {
        if (!CONN.isConnected()) {
            CONN.disconnect();
        }
        if (PROGRESS_FRAME != null) {
            PROGRESS_FRAME.setVisible(false);
        }
        if (CONFIG_EDITOR != null) {
            if (SELECTION_FRAME != null && SELECTION_FRAME.isVisible()) {
                CONFIG_EDITOR.setLocationRelativeTo(SELECTION_FRAME);
            }
            CONFIG_EDITOR.setTabIndex(tabIndex);
            CONFIG_EDITOR.setForce(force);
            CONFIG_EDITOR.setVisible(true);
        }
    }
    
    /**
     * Hibaüzenetet küld a felhasználónak modális dialógusablakban.
     * @param text a megjelenő szöveg
     */
    private static void showSettingError(String text) {
        UIUtil.alert(getString("error"), text, System.err, R.getIconImage());
    }
    
    /**
     * Figyelmeztetést küld a felhasználónak a buborékablakra.
     * Az üzenetre kattintva a kapcsolatbeállító ablak jelenik meg.
     * @param text a megjelenő szöveg
     * @param showSettings jelenjen-e meg a beállítások ablak kattintás esetén
     */
    private static void showSettingWarning(String text, boolean showSettings) {
        showMessage(getString("warning"), text, IconType.WARNING, showSettings ? CALLBACK_SETTING : null);
    }
    
    /**
     * Közli a felhasználóval, hogy a kapcsolódás folyamatban van.
     * Ha a nyitóképernyő még látható, akkor azon történik a jelzés, egyébként
     * a kapcsolódásjelző ablak jelenik meg.
     */
    private static void showConnecting() {
        if (SplashScreenLoader.isVisible()) {
            SplashScreenLoader.setSplashMessage(getString("connect_to_server"));
        }
        else {
            showConnectionStatus(Status.CONNECTING);
        }
    }
    
    /**
     * Beállítja a kapcsolatjelző ablakon a látható ikont és szöveget, majd elrejti a többi ablakot.
     * Ha nincs megadva státusz, akkor az ablak eltűnik, egyébként a megadott státusz jelenik meg.
     * Ha éppen kapcsolódás van, csak a kapcsolódás státusz állítható be.
     * Ha a kapcsolatbeállító ablak látható vagy a program leállítás alatt van,
     * nem jelenik meg a kapcsolatjelző ablak.
     * @param status a kapcsolat státusza
     */
    public static void showConnectionStatus(Status status) {
        if (exiting || CONFIG_EDITOR.isVisible()) return;
        if (connecting && status != Status.CONNECTING) return;
        PROGRESS_FRAME.setStatus(status, MI_RECONNECT);
        SELECTION_FRAME.setVisible(false);
        CONTROLLER_WINDOWS.setVisible(false);
    }
    
    /**
     * Megjeleníti a járműválasztó ablakot és elrejti a járművel kapcsolatos ablakokat.
     * @param l a teljes lista, ami az összes jármű nevét tartalmazza
     */
    public static void showHostSelectionFrame(HostList l) {
        SELECTION_FRAME.refresh(l.getHosts());
        if (!SELECTION_FRAME.isVisible()) {
            SELECTION_FRAME.setVisible(true);
            CONTROLLER_WINDOWS.setVisible(false);
        }
    }
    
    /**
     * A kiválasztott járműhöz tartozó ablakokat jeleníti meg és elrejti a járműválasztót.
     */
    public static void showControllerWindows() {
        CONTROLLER_WINDOWS.setVisible(true);
        SELECTION_FRAME.setVisible(false);
    }
    
    /**
     * Megjeleníti a jelszóbekérő dialógust és ha megadták, elmenti a jelszót a memóriába vagy a konfig fájlba.
     * Ha a kapcsolódásjelző ablak nem látható, akkor a jelszókérő dialógus megjelenik a tálcán.
     * @return a megadott jelszó adat, ami akkor null, ha a Beállítások gombra kattintottak
     */
    public static PasswordData showPasswordDialog() {
        PasswordData data = UIUtil.showPasswordInput(R.getIconImage(), true, !PROGRESS_FRAME.isVisible(), getString("settings"), new Runnable() {

            @Override
            public void run() {
                showSettingFrame(false, 1);
            }

        });
        if (data != null) {
            CONFIG.setPassword(data.getPassword(), data.isSave());
            if (data.isSave()) Config.save(CONFIG);
        }
        return data;
    }
    
    /**
     * Frissíti az MJPEG képkockát a felületen.
     * @param frame a képkocka, ami ha null, fekete kitöltésű kép jelenik meg
     */
    public static void setMjpegFrame(BufferedImage frame) {
        CONTROLLER_WINDOWS.setMjpegFrame(frame);
    }
    
    /**
     * Ha van kiépítve kapcsolat, bontja azt és új kapcsolatot alakít ki.
     */
    public static void reconnect() {
        if (CONN.isConnecting()) return;
        CONN.disconnect();
        runClient(true, false);
    }
    
    /**
     * A program értelme.
     * Kijelzi, hogy elkezdődött a kapcsolódás és kapcsolódik a szerverhez (ha még nem történt meg).
     * Innentől kezdve már a kommunikációtól függ, hogyan folytatódik a program futása.
     * @param reloadMap legyen-e újratöltve a térkép dialóus
     */
    public static void runClient(boolean reloadMap) {
        runClient(false, reloadMap);
    }
    
    /**
     * A program értelme.
     * Kijelzi, hogy elkezdődött a kapcsolódás és kapcsolódik a szerverhez (ha még nem történt meg).
     * Fél másodperc késleltetés van beállítva, hogy legyen ideje a felhasználónak észlelni a folyamatot.
     * Innentől kezdve már a kommunikációtól függ, hogyan folytatódik a program futása.
     * @param delay legyen-e késleltetés
     * @param reloadMap legyen-e újratöltve a térkép dialóus
     */
    public static void runClient(boolean delay, final boolean reloadMap) {
        if (CONN.isConnected()) return;
        connecting = true;
        showConnecting();
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                connecting = false;
                CONN.connect();
            }
            
        }, delay ? 500 : 0);
    }
    
    /**
     * A szótárból kikeresi a megadott kulcshoz tartozó szót.
     */
    public static String getString(String key) {
        return STRINGS.getString(key);
    }
    
    /**
     * Létrehoz egy szótárat a kért nyelvhez és az UIManager-ben megadott, több helyen is használt szövegeket beállítja.
     */
    private static ResourceBundle createResource(Locale locale) {
        return UIUtil.createResource("org.dyndns.fzoli.rccar.l10n.controller", locale, true);
    }
    
    /**
     * Figyelmeztetést jelenít meg a konfigurációval kapcsolatban, ha az alapértelmezett tanúsítvány van használatban.
     * @param help true esetén megjelenik, hogy hol érhetőek el a beállítások, ha alapértelmezett a konfig; false esetén meg figyelmeztetés
     */
    private static void configAlert(boolean help) {
        if (help) {
            if (CONFIG.isDefault()) {
                showSettingWarning(getString("msg_config_hint"), true);
            }
        }
        if (!help || !CONFIG.isDefault()) {
            if (CONFIG.isReplacedCerts()) {
                showSettingWarning(getString("msg_config_replaced"), false);
            }
            else if (CONFIG.isCertDefault()) {
                showSettingWarning(getString("msg_config_warn"), false);
            }
        }
    }
    
    /**
     * Megadja, hogy a vezérlő kliens kapcsolódva van-e a Híd szerverhez.
     */
    public static boolean isConnected() {
        return CONN.isConnected();
    }
    
    /**
     * Beállítja a kért nyelvet.
     * Ha a nyelv megváltozott, szótár cseréje,
     * feliratok lecserélése és ha a konfigszerkesztő-ablak nem látható,
     * az új nyelv elmentése.
     */
    public static void setLanguage(Locale l) {
        if (CONFIG.getLanguage().equals(l)) return;
        STRINGS = createResource(l);
        CONFIG_EDITOR.relocalize();
        PROGRESS_FRAME.relocalize();
        SELECTION_FRAME.relocalize();
        CONTROLLER_WINDOWS.relocalize();
        setSystemTrayIcon();
        synchronized (CONFIG) {
            CONFIG.setLanguage(l);
            if (!CONFIG_EDITOR.isVisible()) Config.save(CONFIG);
            else LNG_FRAME.setLanguage(l);
        }
    }
    
    /**
     * A vezérlő main metódusa.
     * A nyitóképernyő szövege megjelenik és a rendszer LAF valamint a kivételkezelő beállítódik, majd:
     * Ha a grafikus felület nem érhető el, konzolra írja a szomorú tényt és a program végetér.
     * Ha a konfigurációban megadott tanúsítványfájlok nem léteznek, közli a hibát és kényszeríti a kijavítását úgy,
     * hogy feldobja a konfiguráció beállító ablakot és addig nem lehet elhagyni, míg nincs létező fájl beállítva.
     * Ezek után megnézi a program, hogy a publikus teszt tanúsítványok vannak-e használva és ha igen, figyelmezteti a felhasználót.
     * Ha a konfiguráció teljes egészében megegyezik az eredeti beállításokkal, a program közli, hol állítható át.
     * Végül a kliens program elkezdi futását.
     */
    public static void main(String[] args) {
        setApplicationName("Mobile-RC"); // alkalmazásnév beállítása
        setSplashMessage(getString("please_wait")); // jelzés a felhasználónak, hogy tölt a program
        initNativeInterface(); // natív interfész inicializálása a webböngészőhöz és a rendszerikonhoz
        addNativeInterfaceListener(NI_LISTENER); // natív interfész eseménykezelő hozzáadása
        setSystemLookAndFeel(); // rendszer LAF beállítása
        setDockIcon(R.getIconImage()); // Mac OS X-en dock ikon beállítása az alkalmazásválasztó ikonjára
        setExceptionHandler(); // kivételkezelő átállítása a kivételt megjelenítő dialógusra
        if (GraphicsEnvironment.isHeadless()) { // ha a grafikus felület nem érhető el
            System.err.println(getString("msg_need_gui") + LS + getString("msg_exit"));
            System.exit(1); // hibakóddal lép ki
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                setSystemTrayIcon(); // rendszerikon létrehozása
                
                if (!Config.STORE_FILE.exists()) { // ha a konfig fájl nem létezik
                    try {
                        if (!Config.ROOT.exists()) Config.ROOT.mkdirs(); // könyvtár létrehozása, ha nem létezik még
                        Config.STORE_FILE.createNewFile(); // megpróbálja létrehozni
                        Config.STORE_FILE.delete(); // és törli, ha sikerült a létrehozás
                    }
                    catch (IOException ex) { // ha nem lehet létrehozni a fájlt: jogosultság gond
                        showSettingError(getString("msg_need_dir_permission") + LS + getString("msg_exit"));
                        System.exit(1);
                    }
                }
                if (Config.STORE_FILE.exists() && (!Config.STORE_FILE.canRead() || !Config.STORE_FILE.canWrite())) {
                    showSettingError(getString("msg_need_cfg_permission") + LS + getString("msg_exit"));
                    System.exit(1);
                }
                
                // előinicializálom az ablakokat, míg a nyitóképernyő fent van,
                // hogy később ne menjen el ezzel a hasznos idő
                // a nyelvkiválasztó ablakkal kezdem, mivel a konfig-szerkesztő ablak használja a referenciáját
                LNG_FRAME = new LanguageChooserFrame(R.getIconImage(), "org.dyndns.fzoli.rccar.l10n", "controller", CONFIG.getLanguage(), Locale.ENGLISH, new Locale("hu")) {

                    /**
                     * Ha a nyelv megváltozott, kért nyelv beállítása.
                     */
                    @Override
                    protected void onLanguageSelected(Locale l) {
                        Main.setLanguage(l);
                    }
                    
                };
                PROGRESS_FRAME = new ConnectionProgressFrame(CALLBACK_EXIT);
                CONFIG_EDITOR = new ConfigEditorFrame(CONFIG, WL_CFG);
                if (!CONFIG.isCorrect()) { // ha a tanúsítvány fájlok egyike nem létezik
                    showSettingError(getString("warn_config_error1" + (CONFIG.isDefault() ? 'b' : 'a')) + ' ' + getString("warn_config_error2") + LS + getString("warn_config_error3"));
                    showSettingFrame(true, 1); // kényszerített beállítás és tanúsítvány lapfül előtérbe hozása
                }
                SELECTION_FRAME = new HostSelectionFrame(AL_EXIT);
                CONTROLLER_WINDOWS = new ControllerWindows();
                if (CONFIG.isCorrect()) { // ha a konfiguráció megfelelő, kliens indítása
                    configAlert(true); // súgó figyelmeztetés, ha kell
                    runClient(false); // és végül a lényeg
                }
            }

        });
        runNativeEventPump(); // natív interfész eseménypumpáló indítása
    }
    
}
