package org.dyndns.fzoli.rccar.bridge;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStoreException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.bridge.resource.R;
import org.dyndns.fzoli.rccar.bridge.socket.BridgeHandler;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.closeSplashScreen;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.setDefaultSplashMessage;
import static org.dyndns.fzoli.rccar.ui.UIUtil.showPasswordInput;
import org.dyndns.fzoli.rccar.ui.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.ui.UncaughtExceptionHandler.showException;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.ui.UIUtil;
import static org.dyndns.fzoli.ui.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.ui.systemtray.SystemTrayIcon;

/**
 * A híd indító osztálya.
 * @author zoli
 */
public class Main {
    
    /**
     * A híd konfigurációja.
     */
    private static final Config CONFIG = Config.getInstance();
    
    /**
     * Általános rendszerváltozók.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * Üzenettípusok.
     */
    private static final String VAL_MESSAGE = "Híd üzenet", VAL_ERROR = "Híd hiba";
    
    /**
     * Több helyen is használt szövegek.
     */
    public static final String VAL_WARNING = "Figyelmeztetés", VAL_CONN_LOG = "Kapcsolatjelzés";
    
    /**
     * A szerver socket referenciája arra kell, hogy eseménykezelővel ki lehessen lépni.
     */
    private static SSLServerSocket SERVER_SOCKET;
    
    /**
     * Még mielőtt lefutna a main metódus, beállítódik a rendszer LAF, a saját kivételkezelő, a rendszerikon és az erőforrás-felszabadító szál.
     */
    static {
        setDefaultSplashMessage(); //TODO: erre nem lesz szükség
        setSystemLookAndFeel();
        setExceptionHandler();
        setSystemTrayIcon();
        addShutdownHook();
        closeSplashScreen(); //TODO: erre nem lesz szükség
    }
    
    /**
     * Beállítja a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    private static void setExceptionHandler() {
        UncaughtExceptionHandler.apply(R.getBridgeImage());
    }
    
    /**
     * Beállítja a rendszerikont, ha a konfiguráció nem tiltja.
     * Hozzáadja a kapcsolatjelzés és kilépés menüopciót beállítja az ikont és megjeleníti azt.
     */
    private static void setSystemTrayIcon() {
        if (SystemTrayIcon.isSupported() && !CONFIG.isHidden()) {
            // az ikon beállítása
            SystemTrayIcon.setIcon("Mobile-RC híd", R.getBridgeImageStream());
            
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
            
            // szeparátor hozzáadása a menühöz
            SystemTrayIcon.addMenuSeparator();

            // kilépés opció hozzáadása
            SystemTrayIcon.addMenuItem("Kilépés", new Runnable() {

                /**
                 * Ha a kilépésre kattintottak.
                 */
                @Override
                public void run() {
                    // a program kilép
                    System.exit(0);
                }
                
            });
        }
    }
    
    /**
     * A program leállítása előtt nem árt az erőforrásokat felszabadítani.
     * Leállításkor ha sikerült a szerver socket létrehozása, bezárja azt.
     */
    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {
                if (SERVER_SOCKET != null) try {
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
        UIUtil.alert(title, text, out, R.getBridgeImage());
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
        }
    }
    
    /**
     * SSL Server socket létrehozása a konfig fájl alapján.
     * Ha valamiért nem sikerül a tanúsítvány használata, jelszó bevitel jelenik meg.
     * @throws Error ha nem sikerül a szerver socket létrehozása
     */
    private static SSLServerSocket createServerSocket() {
        try {
            return SSLSocketUtil.createServerSocket(CONFIG.getPort(), CONFIG.getCAFile(), CONFIG.getCertFile(), CONFIG.getKeyFile(), CONFIG.getPassword());
        }
        catch (KeyStoreException ex) {
            if (ex.getMessage().startsWith("failed to extract")) {
                CONFIG.setPassword(showPasswordInput(R.getBridgeImage()).getPassword());
                return createServerSocket();
            }
            alert(VAL_ERROR, "Nem sikerült a szerver elindítása, mert a tanúsítvány hibás vagy nincs jól beállítva", System.err);
            System.exit(1);
            return null;
        }
        catch(Exception ex) {
            alert(VAL_ERROR, "Nem sikerült a szerver elindítása a megadott porton: " + CONFIG.getPort() + LS + "Az operációsrendszer üzenete: " + ex.getMessage(), System.err);
            System.exit(1);
            return null;
        }
    }
    
    /**
     * A szerver socket elindítása, a program értelme.
     * Ha nem megbízható kapcsolat jön létre, jelzi a felhasználónak.
     * Ha nem várt kivétel képződik, kivételt dob, ami a felhasználó tudtára lesz adva.
     * @throws RuntimeException ha nem várt kivétel képződik
     */
    private static void runServer() {
        final SSLServerSocket ss = createServerSocket();
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
    
    /**
     * A híd main metódusa.
     * Ha a konfiguráció még nem létezik, lérehozza és figyelmezteti a felhasználót, hogy állítsa be és kilép.
     * Ha a konfiguráció létezik, de rosszul paraméterezett, figyelmezteti a felhasználót és kilép.
     * Ha a program nem lépett ki, a híd szerver elkezdi futását.
     */
    public static void main(String[] args) {
        final File dir = new File("./");
        if (!dir.canRead()) {
            alert(VAL_ERROR, "A program futtatásához olvasási jogra van szükség." + LS + "A program kilép.", System.err);
            System.exit(1); // hibakóddal lép ki
        }
        if (CONFIG.isCorrect()) {
            readArguments(args);
            runServer();
        }
        else {
            final StringBuilder msg = new StringBuilder();
            if (CONFIG.isNew()) {
                msg.append("A konfigurációs fájlt létrehoztam.").append(LS)
                   .append("Kérem, állítsa be megfelelően!").append(LS).append(LS)
                   .append("Konfig fájl útvonala:").append(LS).append(Config.FILE_CONFIG);
                alert(VAL_MESSAGE, msg.toString(), System.out);
                System.exit(0);
            }
            else {
                msg.append("Nem megfelelő konfiguráció!").append(LS).append(LS);
                msg.append("A ").append(Config.FILE_CONFIG).append(" fájl hibásan van paraméterezve:").append(LS);
                if (CONFIG.getPort() == null) msg.append("- Adjon meg érvényes portot.").append(LS);
                if (CONFIG.getCAFile() == null) msg.append("- Adjon meg létező ca fájl útvonalat.").append(LS);
                if (CONFIG.getCertFile() == null) msg.append("- Adjon meg létező cert fájl útvonalat.").append(LS);
                if (CONFIG.getKeyFile() == null) msg.append("- Adjon meg létező key fájl útvonalat.").append(LS);
                alert(VAL_ERROR, msg.toString(), System.err);
                System.exit(1);
            }
        }
    }
    
}
