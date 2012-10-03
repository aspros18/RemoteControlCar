package org.dyndns.fzoli.rccar.bridge;

import java.awt.MenuItem;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.SystemTrayIcon;
import static org.dyndns.fzoli.rccar.SystemTrayIcon.showMessage;
import static org.dyndns.fzoli.rccar.UIUtil.alert;
import static org.dyndns.fzoli.rccar.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.rccar.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.UncaughtExceptionHandler.showException;
import org.dyndns.fzoli.rccar.bridge.resource.R;
import org.dyndns.fzoli.rccar.bridge.socket.BridgeDisconnectProcess;
import org.dyndns.fzoli.rccar.bridge.socket.BridgeHandler;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.MultipleCertificateException;
import org.dyndns.fzoli.socket.handler.SecureHandlerException;

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
     * A szerver socket referenciája arra kell, hogy eseménykezelővel ki lehessen lépni.
     */
    private static SSLServerSocket SERVER_SOCKET;
    
    /**
     * Még mielőtt lefutna a main metódus, beállítódik a rendszer LAF, a saját kivételkezelő, a rendszerikon és az erőforrás-felszabadító szál.
     */
    static {
        setSystemLookAndFeel();
        setExceptionHandler();
        setSystemTrayIcon();
        addShutdownHook();
    }
    
    /**
     * Beállítja a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    private static void setExceptionHandler() {
        UncaughtExceptionHandler.apply();
    }
    
    /**
     * Beállítja a rendszerikont.
     * Hozzáadja a kapcsolatjelzés és kilépés menüopciót beállítja az ikont és megjeleníti azt.
     */
    private static void setSystemTrayIcon() {
        // az ikon beállítása
        SystemTrayIcon.setIcon("Mobile-RC híd", R.getBridgeImage());
        
        // kapcsolatjelzés beállító opció létrehozása és beállítása
        final MenuItem miConnLog = new MenuItem(BridgeDisconnectProcess.getLogOption());
        miConnLog.addActionListener(new ActionListener() {

            /**
             * Ha a naplózás beállítását kérik.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // naplózás beállítása az ellenkezőjére, mint volt
                BridgeDisconnectProcess.setLogEnabled(!BridgeDisconnectProcess.isLogEnabled());
                // a megváltozott opció frissítése
                miConnLog.setLabel(BridgeDisconnectProcess.getLogOption());
            }
            
        });
        
        // kapcsolatjelzés beállító opció hozzáadása
        SystemTrayIcon.addMenuItem(miConnLog);
        
        // kilépés opció hozzáadása
        SystemTrayIcon.addMenuItem("Kilépés", new ActionListener() {

            /**
             * Ha a kilépésre kattintottak.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // a program kilép
                System.exit(0);
            }
            
        });
        
        // a rendszerikon megjelenítése
        SystemTrayIcon.setVisible(true);
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
     * A szerver elindítása előtt a konzolon beadott paramétereket feldolgozza.
     * A paraméterek szükségtelenek, ha van grafikus felület a rendszeren.
     * Ha a -v paraméter meg lett adva, ki fogja jelezni a kapcsolódásokat a program.
     */
    private static void readArguments(String[] args) {
        if (args.length == 1 && args[0].equals("-v")) {
            BridgeDisconnectProcess.setLogEnabled(true);
        }
    }
    
    /**
     * SSL Server socket létrehozása a konfig fájl alapján.
     * @throws Error ha nem sikerül a szerver socket létrehozása
     */
    private static SSLServerSocket createServerSocket() {
        try {
            return SSLSocketUtil.createServerSocket(CONFIG.getPort(), CONFIG.getCAFile(), CONFIG.getCertFile(), CONFIG.getKeyFile(), CONFIG.getPassword());
        }
        catch(Exception ex) {
            throw new Error(ex.getMessage());
        }
    }
    
    /**
     * Figyelmeztetőüzenet jelzése a sikertelen kapcsolódásról.
     */
    public static void showWarning(SSLSocket s, String msg) {
        if (s != null) showMessage(VAL_MESSAGE, msg + " a " + s.getInetAddress().getHostName() + " címről.", TrayIcon.MessageType.WARNING);
    }
    
    /**
     * A socket szerver elindítása, a program értelme.
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
            catch (SecureHandlerException ex) {
                // ha nem megbízható kliens kapcsolódott, információ közlése a felhasználónak
                showWarning(s, "Nem megbízható kapcsolódás");
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
     * A program futása előtt ha nem létezik az admin adatbázis, létrehozza és figyelmezteti a felhasználót.
     * Ezek után a híd program elkezdi futását.
     */
    public static void main(String[] args) {
        if (CONFIG.isCorrect()) {
            if (AdminDAO.isNew()) {
                if (AdminDAO.exists()) {
                    alert(VAL_MESSAGE, "A rendszergazdákat tartalmazó adatbázist létrehoztam." + LS + "Mostantól használható az adatbázis." + LS + "A program fut tovább.", System.out);
                }
                else {
                    alert(VAL_ERROR, "Hiba a rendszergazdákat tartalmazó adatbázis létrehozása során!" + LS + "A program rendszergazdamentesen fut tovább.", System.err);
                }
            }
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
