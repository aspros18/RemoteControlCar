package org.dyndns.fzoli.rccar.bridge;

import chrriis.dj.nativeswing.swtimpl.NativeInterfaceAdapter;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.rccar.bridge.resource.R;
import org.dyndns.fzoli.rccar.bridge.socket.BridgeHandler;
import static org.dyndns.fzoli.rccar.SplashScreenLoader.closeSplashScreen;
import static org.dyndns.fzoli.rccar.SplashScreenLoader.setSplashMessage;
import org.dyndns.fzoli.rccar.ui.UIUtil;
import static org.dyndns.fzoli.rccar.ui.UIUtil.showPasswordInput;
import static org.dyndns.fzoli.rccar.ui.UIUtil.initNativeInterface;
import static org.dyndns.fzoli.rccar.ui.UIUtil.addNativeInterfaceListener;
import static org.dyndns.fzoli.rccar.ui.UIUtil.runNativeEventPump;
import org.dyndns.fzoli.rccar.ui.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.ui.UncaughtExceptionHandler.showException;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import static org.dyndns.fzoli.ui.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.ui.systemtray.SystemTrayIcon;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;
import static org.dyndns.fzoli.util.OSUtils.setApplicationName;
import org.dyndns.fzoli.ui.systemtray.MenuItem;
import org.dyndns.fzoli.ui.systemtray.MenuItemReference;
import org.dyndns.fzoli.util.Folders;
import static org.dyndns.fzoli.util.MacApplication.setDockIcon;
import org.dyndns.fzoli.util.OSUtils;

/**
 * A Híd indító osztálya.
 * @author zoli
 */
public class Main {
    
    /**
     * Általános rendszerváltozók.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * A híd konfigurációja.
     */
    public static final Config CONFIG = getConfig();
    
    /**
     * A szótár.
     */
    private static final ResourceBundle STRINGS = createResource(CONFIG.getLanguage());
    
    /**
     * Üzenettípus.
     */
    private static final String VAL_MESSAGE = getString("bridge_message"), VAL_ERROR = getString("bridge_error");
    
    /**
     * Több helyen is használt szövegek.
     */
    public static final String VAL_WARNING = getString("warning"), VAL_CONN_LOG = getString("conn_log");
    
    /**
     * A szerver socket referenciája arra kell, hogy eseménykezelővel ki lehessen lépni.
     */
    private static SSLServerSocket SERVER_SOCKET;
    
    /**
     * A szerző dialógust megjelenítő menüelem, ami inaktív, míg a dialógus látható.
     */
    private static MenuItem MI_ABOUT;
    
    /**
     * A kliens alkalmazást elindító menüelem, ami inaktív, míg fut a kliens alkalmazás.
     */
    private static MenuItem MI_RUN_CONTROLLER;
    
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
     * A forrásfájlra mutató objektum.
     * Ha nem sikerült lekérni, a jelenlegi könyvtár, csak hogy ne legyen null.
     */
    private static final File SRC_FILE = Folders.getSourceFile() == null ? new File(".") : Folders.getSourceFile();
    
    /**
     * Megadja, hogy jar-ból fut-e az alkalmazás.
     */
    private static final boolean IN_JAR = SRC_FILE.getName().endsWith("jar");
    
    /**
     * A vezérlő program jar-ból való indításának paramétereit tartalmazza.
     * Mivel Mac-en a grafikus felületről nem indítható ugyan azon alkalmazás többször,
     * a szervert indítva egyetlen kliens alkalmazás indítására adok lehetőséget a
     * rendszerikon menüjéből. Ha esetleg grafikus admin klienst is írok egyszer,
     * ugyan ezzel a módszerrel lehet lesz majd indítani azt is.
     */
    private static final List<String> CTRL_ARGS = new ArrayList<String>() {
        {
            final String srcPath = SRC_FILE.getAbsolutePath(); // a forrásfájl pontos helye
            if (OSUtils.isOS(OSUtils.OS.MAC) && srcPath.contains(".app/Contents/Resources/Java/")) { // ha Mac-en JarBundler-ből fut a szerver
                add("open"); // akkor open paranccsal megnyitható az alkalmazás
                add("-n"); // új alkalmazás példányként
                add("-W"); // bevárva az alkalmazás befejeződését
                add(srcPath.substring(0, srcPath.indexOf(".app") + 4)); // az alkalmazáskönyvtár útvonalát megadva
                add("--args"); // és argumentumként
                add("client"); // a kliens programot indítva
            }
            else if (IN_JAR) { // csak akkor indítható a kliens program, ha jar-ból fut a szerver
                add("java"); // az alkalmazás, amit indít: java
                if (OSUtils.isOS(OSUtils.OS.MAC)) { // Mac alatt a JarBundler helyettesítése paraméterekkel
                    // az első szálból indul az alkalmazás
                    add("-XstartOnFirstThread");
                    // beszédes név megadása a processnek
                    add("-Xdock:name=Mobile-RC Client");
                    // feltételezés, hogy a JarBundler által készült alkalmazáskönyvtárból fut a program és az ikon beállítható
                    // ha az ikon nem található, kezdetben az alapértelmezés látható pár másodpercre, végül a kódból beállítódik
                    add("-Xdock:icon=" + new File(Folders.getSourceDir().getParentFile(), "controller.icns").getAbsolutePath());
                }
                add("-jar"); // közli a Javaval, hogy jar fájlt kell indítania
                add(srcPath); // a jar fájl helye
                add("client"); // paraméter megadása a jar main metódusának, hogy a kliens induljon el
            }
        }
    };
    
    /**
     * Beállítja a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    private static void setExceptionHandler() {
        UncaughtExceptionHandler.apply(R.getBridgeImage());
    }
    
    /**
     * A konfiguráció alkalmazása.
     * Megnézi, hogy a csendes indulás be van-e állítva a konfig fájlban és ha igen,
     * a figyelmeztetéseket kikapcsolja, majd értelmezi a paramétereket, amik ezt felüldefiniálhatják.
     */
    private static void applyConfig() {
        if (CONFIG.isQuiet()) {
            BridgeHandler.setWarnEnabled(false);
            ConnectionAlert.setLogEnabled(false);
        }
    }
    
    /**
     * Beállítja a rendszerikont, ha a konfiguráció nem tiltja.
     * Hozzáadja a kapcsolatjelzés és kilépés menüopciót beállítja az ikont és megjeleníti azt.
     */
    private static void setSystemTrayIcon() {
        if (CONFIG.isHidden()) return;
        if (SystemTrayIcon.init(!UIUtil.isNativeSwingAvailable()) && SystemTrayIcon.isSupported()) {
            // az ikon beállítása
            SystemTrayIcon.setIcon(getString("app_name"), R.resize(R.getBridgeImage(), SystemTrayIcon.getIconWidth()));
            // kapcsolatjelzés beállító opció hozzáadása
            SystemTrayIcon.addCheckboxMenuItem(VAL_CONN_LOG, ConnectionAlert.isLogEnabled(), new Runnable() {

                @Override
                public void run() {
                    // naplózás beállítása az ellenkezőjére, mint volt
                    ConnectionAlert.setLogEnabled(!ConnectionAlert.isLogEnabled());
                }
                
            });

            // figyelmeztetés beállító opció hozzáadása
            SystemTrayIcon.addCheckboxMenuItem(VAL_WARNING, BridgeHandler.isWarnEnabled(), new Runnable() {

                @Override
                public void run() {
                    // warn beállítása az ellenkezőjére, mint volt
                    BridgeHandler.setWarnEnabled(!BridgeHandler.isWarnEnabled());
                }
                
            });
            
            // Az alkalmazásindító opció csak Mac-en látható, de más rendszereken is kérhető a konfigban
            if (OSUtils.isOS(OSUtils.OS.MAC) || CONFIG.isAppMenu()) {
            
                // szeparátor hozzáadása a menühöz, alkalmazásindító blokk jön
                SystemTrayIcon.addMenuSeparator();

                // megadja, hogy engedélyezett-e a vezérlő kliens indítása
                boolean runControllerEnabled = true;
                if (MI_RUN_CONTROLLER != null) runControllerEnabled = MI_RUN_CONTROLLER.isEnabled();

                // vezérlő klienst indító menüelem inicializálása
                MI_RUN_CONTROLLER = SystemTrayIcon.addMenuItem(getString("run_controller"), org.dyndns.fzoli.rccar.controller.resource.R.getSmallIconImage(), new Runnable() {

                    @Override
                    public void run() {
                        if (IN_JAR) {
                            MI_RUN_CONTROLLER.setEnabled(false); // a program indulása előtt opció letiltása
                            final ProcessBuilder builder = new ProcessBuilder(CTRL_ARGS); // kliens-process létrehozó létrehozása
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        final Process p = builder.start(); // process indítása
                                        p.waitFor(); // új szálban várakozik, míg fut a kliens-process (így a GUI továbbra is válaszolni tud az eseményekre)
                                    }
                                    catch (Exception ex) {
                                        ex.printStackTrace(); // segítség tesztelés idejére
                                    }
                                    MI_RUN_CONTROLLER.setEnabled(true); // ha befejeződött a process, opció engedélyezése, hogy újra el lehessen indítani
                                }

                            }).start();
                        }
                    }

                });

                // csak akkor van engedélyezve a kliens futtatása, ha jar-ból fut a program, valamint ha volt előző menu item, az is engedélyezve volt
                MI_RUN_CONTROLLER.setEnabled(IN_JAR && runControllerEnabled);
            
            }
            
            // szeparátor hozzáadása a menühöz, alkalmazás-leállító blokk jön
            SystemTrayIcon.addMenuSeparator();
            
            // megadja, hogy engedélyezett-e a névjegy megjelenítése
            boolean showAboutEnabled = true;
            if (MI_ABOUT != null) showAboutEnabled = MI_ABOUT.isEnabled();
            
            // az aktuális névjegy-megjelenítő opció referenciáját adja meg
            final MenuItemReference mirAuthor = new MenuItemReference() {

                @Override
                public synchronized MenuItem getMenuItem() {
                    return MI_ABOUT;
                }

            };
            
            // névjegy opció hozzáadása
            MI_ABOUT = SystemTrayIcon.addMenuItem(getString("about"), R.getQuestionImage(), new Runnable() {

                @Override
                public void run() {
                    UIUtil.showAuthorDialog(mirAuthor, R.getBridgeImage());
                }
                
            });
            
            // csak egyetlen névjegy ablak lehet látható egyazon időben
            MI_ABOUT.setEnabled(showAboutEnabled);
            
            // kilépés opció hozzáadása
            SystemTrayIcon.addMenuItem(getString("exit"), R.getExitImage(), new Runnable() {

                /**
                 * Ha a kilépésre kattintottak, a program kilép.
                 */
                @Override
                public void run() {
                    SystemTrayIcon.dispose();
                    System.exit(0);
                }
                
            });
        }
    }
    
    /**
     * A program leállítása előtt nem árt az erőforrásokat felszabadítani.
     * Leállításkor ha sikerült a szerver socket létrehozása, bezárja azt
     * és naplózza a leállást.
     */
    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                if (SERVER_SOCKET != null) try {
                    logInfo(VAL_MESSAGE, getString("log_stop"), false);
                    SERVER_SOCKET.close();
                }
                catch (IOException ex) {
                    ;
                }
            }
            
        }));
    }
    
    /**
     * Egy tályékoztató szöveget jelenít meg a felhasználónak.
     * Ha a grafikus felület elérhető, modális ablakban jelenik meg az üzenet,
     * különben a kimenet streamre megy ki a fejléc és a szöveg.
     * Ha a kimeneti stream System.err, akkor hibaüzenetes ablakikon,
     * egyébként figyelmeztetőikon kerül az ablakra.
     * @param title a fejléc
     * @param text a megjelenő szöveg
     * @param out a kimenet stream
     */
    private static void alert(String title, String text, PrintStream out) {
        UIUtil.alert(title, text, out, R.getBridgeImage(), true);
    }
    
    /**
     * Naplózza az átadott üzenetet és ha kell, meg is jeleníti azt a felhasználónak.
     * @param title az üzenet címsora
     * @param text a naplózandó üzenet
     * @param show true esetén az üzenet megjelenik a naplózás után
     */
    private static void logInfo(String title, String text, boolean show) {
        ConnectionAlert.logMessage(title, text, IconType.INFO, show);
    }
    
    /**
     * A szerver elindítása előtt a konzolon beadott paramétereket feldolgozza.
     * A paraméterek szükségtelenek, ha van grafikus felület a rendszeren.
     * Ha a -v paraméter meg lett adva, a program ki fogja jelezni a figyelmeztetéseket.
     * Ha a -vv paraméter meg lett adva, a program ki fogja jelezni a figyelmeztetéseket és a kapcsolódásokat is.
     * Ha a -m paraméter meg lett adva, a program nem jelez se figyelmeztetéseket, se kapcsolódásokat.
     */
    private static void readArguments(String[] args) {
        if (args.length == 1) {
            if (args[0].equals("-v")) {
                BridgeHandler.setWarnEnabled(true);
                ConnectionAlert.setLogEnabled(false);
            }
            else if (args[0].equals("-vv")) {
                BridgeHandler.setWarnEnabled(true);
                ConnectionAlert.setLogEnabled(true);
            }
            else if (args[0].equals("-m")) {
                BridgeHandler.setWarnEnabled(false);
                ConnectionAlert.setLogEnabled(false);
            }
            else {
                showCommandLineHelp();
            }
        }
        else if (args.length > 0) {
            showCommandLineHelp();
        }
    }
    
    /**
     * Parancssoros súgót jelenít meg.
     */
    private static void showCommandLineHelp() {
        System.out.println(getString("arg_msg0") + ':');
        System.out.println("-v   " + getString("arg_msg1") + '.');
        System.out.println("-vv  " + getString("arg_msg2") + '.');
        System.out.println("-m   " + getString("arg_msg3") + '.');
        System.exit(1);
    }
    
    /**
     * SSL Server socket létrehozása a konfig fájl alapján.
     * Ha valamiért nem sikerül a tanúsítvány használata, jelszó bevitel jelenik meg.
     * @param count ha jelszóvédett a tanúsítvány, a hibás próbálkozások számát jelzi (rekurzívan) a naplózáshoz
     * @throws Error ha nem sikerül a szerver socket létrehozása
     */
    private static SSLServerSocket createServerSocket(int count) {
        try {
            return SERVER_SOCKET = SSLSocketUtil.createServerSocket(CONFIG.getPort(), CONFIG.getCAFile(), CONFIG.getCertFile(), CONFIG.getKeyFile(), CONFIG.getPassword());
        }
        catch (KeyStoreException ex) {
            if (ex.getMessage().startsWith("failed to extract")) {
                if (count > 0) ConnectionAlert.logMessage(VAL_WARNING, getString("msg_wrong_passwd1") + ' ' + count + ' ' + getString("msg_wrong_passwd2" + (count == 1 ? 'a' : 'b')), IconType.WARNING, false);
                CONFIG.setPassword(showPasswordInput(R.getBridgeImage(), false, true).getPassword());
                return createServerSocket(++count);
            }
            MI_RUN_CONTROLLER.setEnabled(false);
            alert(VAL_ERROR, getString("msg_cert_error"), System.err);
            System.exit(1);
            return null;
        }
        catch(Exception ex) {
            MI_RUN_CONTROLLER.setEnabled(false);
            alert(VAL_ERROR, getString("msg_port_error") + ": " + CONFIG.getPort() + LS + getString("msg_os") + ": " + ex.getMessage(), System.err);
            System.exit(1);
            return null;
        }
    }
    
    /**
     * A szerver socket elindítása, a program értelme.
     * Ha nem megbízható kapcsolat jön létre, jelzi a felhasználónak.
     * Ha nem várt kivétel képződik, kivételt dob, ami a felhasználó tudtára lesz adva.
     * A szerver socket sikeres létrehozása után naplózza és közli a felhasználóval, hogy fut a szerver.
     * @throws RuntimeException ha nem várt kivétel képződik
     */
    private static void runServer() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                final SSLServerSocket ss = createServerSocket(0); // socket szerver létrehozása kezdetben nincs 1 hibás próbálkozás sem a jelszóbevitelnél
                closeSplashScreen(); // sikeres létrehozás esetén splash screen eltüntetése és naplózás; hiba esetén hibaüzenet jelenik meg és a splash screen magától eltűnik
                logInfo(VAL_MESSAGE, getString("log_start"), !CONFIG.isQuiet() && Permissions.getConfig().canRead());
                while (!ss.isClosed()) { // ameddig nincs lezárva a socket szerver
                    SSLSocket s = null;
                    try {
                        s = (SSLSocket) ss.accept(); // kliensre várakozik, és ha kapcsolódtak, ...
                        new Thread(new BridgeHandler(s)).start(); // ... új szálban kezeli a kapcsolatot
                    }
                    catch (Exception ex) {
                        // ha bármilyen kivétel keletkezik, nem áll le a szerver, csak közli a kivételt
                        showException(ex);
                    }
                }
            }
            
        }).start();
    }
    
    /**
     * A szótárból kikeresi a megadott kulcshoz tartozó szót.
     */
    public static String getString(String key) {
        return STRINGS.getString(key);
    }
    
    /**
     * Létrehozza a konfigurációs objektumot.
     * Ha az inicializálása közben hiba történik, üzen a felhasználónak.
     */
    private static Config getConfig() {
        try {
            return Config.getInstance();
        }
        catch (Exception ex) {
            setSystemLookAndFeel();
            ResourceBundle res = createResource(Locale.getDefault());
            alert(res.getString("bridge_error"), res.getString("msg_conf_error1") + LS + res.getString("msg_conf_error2"), System.err);
            System.exit(1);
            return null;
        }
    }
    
    /**
     * Létrehoz egy szótárat a kért nyelvhez és az UIManager-ben megadott, több helyen is használt szövegeket beállítja.
     */
    private static ResourceBundle createResource(Locale locale) {
        return UIUtil.createResource("org.dyndns.fzoli.rccar.l10n.bridge", locale, false);
    }
    
    /**
     * A híd main metódusa.
     * Beállítódik a rendszer LAF, a saját kivételkezelő, a rendszerikon és az erőforrás-felszabadító szál, majd:
     * Ha a konfiguráció még nem létezik, lérehozza és figyelmezteti a felhasználót, hogy állítsa be és kilép.
     * Ha a konfiguráció létezik, de rosszul paraméterezett, figyelmezteti a felhasználót és kilép.
     * Ha a program nem lépett ki, a híd szerver elkezdi futását.
     */
    public static void main(final String[] args) {
        setApplicationName("Mobile-RC Server");
        setSplashMessage(getString("please_wait"));
        initNativeInterface();
        addNativeInterfaceListener(NI_LISTENER);
        setSystemLookAndFeel();
        setDockIcon(R.getBridgeImage());
        setExceptionHandler();
        applyConfig();
        addShutdownHook();
        if (CONFIG.getFile().exists() && !CONFIG.getFile().canRead()) { // ha nincs olvasási jog a konfig fájlon
            alert(VAL_ERROR, getString("msg_need_permission") + LS + getString("msg_file_path") + ": " + CONFIG.getFile().getAbsolutePath() + LS + getString("msg_exit"), System.err);
            System.exit(1); // hibakóddal lép ki
        }
        if (CONFIG.isCorrect()) {
            setSplashMessage(getString("start_server"));
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setSystemTrayIcon();
                    if (!Permissions.getConfig().canRead()) { // ha a jogosultságokat leíró fájl nem olvasható, figyelmezteti a felhasználót
                        SystemTrayIcon.showMessage(VAL_WARNING, getString("warn_without_permissions1") + ' ' + LS + getString("warn_without_permissions2"), IconType.WARNING);
                    }
                    readArguments(args);
                    runServer();
                }
                
            });
            runNativeEventPump();
        }
        else {
            final StringBuilder msg = new StringBuilder();
            if (CONFIG.isNew()) {
                String title;
                PrintStream out;
                if (CONFIG.getFile().exists()) {
                    title = VAL_MESSAGE;
                    out = System.out;
                    msg.append(getString("msg_conf_created1")).append(LS)
                       .append(getString("msg_conf_created2")).append(LS).append(LS)
                       .append(getString("msg_file_path")).append(':').append(LS).append(CONFIG.getFile());
                }
                else {
                    title = VAL_ERROR;
                    out = System.err;
                    msg.append(getString("config_save_error1")).append(LS)
                       .append(getString("config_save_error2"));
                }
                alert(title, msg.toString(), out);
                System.exit(0);
            }
            else {
                msg.append(getString("msg_conf_incorrect1")).append(LS).append(LS);
                msg.append(getString("msg_conf_incorrect2")).append(':').append(LS);
                if (CONFIG.getPort() == null) msg.append("- ").append(getString("msg_conf_incorrect3")).append('.').append(LS);
                if (CONFIG.getCAFile() == null) msg.append("- ").append(getString("msg_conf_incorrect4")).append('.').append(LS);
                if (CONFIG.getCertFile() == null) msg.append("- ").append(getString("msg_conf_incorrect5")).append('.').append(LS);
                if (CONFIG.getKeyFile() == null) msg.append("- ").append(getString("msg_conf_incorrect6")).append('.').append(LS);
                msg.append(LS).append(getString("msg_file_path")).append(':').append(LS).append(CONFIG.getFile());
                msg.append(LS);
                alert(VAL_ERROR, msg.toString(), System.err);
                System.exit(1);
            }
        }
    }
    
}
