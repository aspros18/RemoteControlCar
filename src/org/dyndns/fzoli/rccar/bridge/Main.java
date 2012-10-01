package org.dyndns.fzoli.rccar.bridge;

import java.awt.AWTException;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.exceptiondialog.UncaughtExceptionDialog;
import static org.dyndns.fzoli.rccar.UIUtil.*;
import org.dyndns.fzoli.rccar.bridge.resource.R;
import org.dyndns.fzoli.socket.process.SecureProcessException;
import org.dyndns.fzoli.socket.process.SecureUtil;

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
     * A SystemTray null, ha a rendszer nem támogatja vagy nem található az ikon képe.
     */
    private static final SystemTray SYSTEM_TRAY;
    
    /**
     * A szerver socket referenciája arra kell, hogy eseménykezelővel ki lehessen lépni.
     */
    private static SSLServerSocket SERVER_SOCKET;
    
    /**
     * Még mielőtt lefutna a main metódus, beállítódik a saját kivételkezelő, a rendszer LAF, a rendszerikon és az erőforrás-felszabadító szál.
     */
    static {
        SYSTEM_TRAY = createSystemTrayIcon();
        setSystemLookAndFeel();
        setExceptionHandler();
        addShutdownHook();
    }
    
    /**
     * Ha van grafikus felület, rendszerikont jelenít meg.
     * Több funkciója is van az ikonnak:
     * - a felhasználó látja, hogy a program fut
     * - a felhasználó ha nem konzolból indította a programot, csak itt képes leállítani
     * - nem kezelt kivétel esetén buborékablak tályékoztatja a felhasználót, amire kattintva megtekintheti a hibát
     * @return SystemTray ha a rendszer támogatja és elérhető az ikon képe, egyébként null
     */
    private static SystemTray createSystemTrayIcon() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            PopupMenu menu = new PopupMenu();
            TrayIcon icon = new TrayIcon(R.getBridgeImage(), "Mobile-RC Híd", menu);
            //TODO: ikon és menü hozzáadás
            tray.add(icon);
            return tray;
        }
        catch (UnsupportedOperationException | SecurityException | IOException | AWTException ex) {
            return null;
        }
    }
    
    /**
     * Beállítja a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket.
     */
    private static void setExceptionHandler() {
        if (SYSTEM_TRAY != null) {
            //TODO: ezt a metódust lecserélni, mert ide nem lesz jó
            UncaughtExceptionDialog.applyHandler();
        }
    }
    
    /**
     * SSL Server socket létrehozása a konfig fájl alapján.
     */
    private static SSLServerSocket createServerSocket() throws IOException, GeneralSecurityException {
        return SecureUtil.createServerSocket(CONFIG.getPort(), CONFIG.getCAFile(), CONFIG.getCertFile(), CONFIG.getKeyFile(), CONFIG.getPassword());
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
     * A híd main metódusa.
     * Ha a konfiguráció még nem létezik, lérehozza és figyelmezteti a felhasználót, hogy állítsa be és kilép.
     * Ha a konfiguráció létezik, de rosszul paraméterezett, figyelmezteti a felhasználót és kilép.
     * A program futása előtt ha nem létezik az admin adatbázis, létrehozza és figyelmezteti a felhasználót.
     * Ezek után a híd program elkezdi futását.
     */
    public static void main(String[] args) { //TODO: a kivételkezelés átgondolása és jó lenne külön metódusba tenni a tényleges futást
        if (CONFIG.isCorrect()) try {
            if (AdminDAO.isNew()) {
                if (AdminDAO.exists()) {
                    alert(VAL_MESSAGE, "A rendszergazdákat tartalmazó adatbázist létrehoztam." + LS + "Mostantól használható az adatbázis.", System.out);
                }
                else {
                    alert(VAL_ERROR, "Hiba a rendszergazdákat tartalmazó adatbázis létrehozása során!" + LS + "A program rendszergazdamentesen indul.", System.err);
                }
            }
            final SSLServerSocket ss = createServerSocket();
            while (!ss.isClosed()) { // ameddig nincs lezárva a socket szerver
                SSLSocket s = (SSLSocket) ss.accept(); // kliensre várakozik, és ha kapcsolódtak ...
                try {
                    // TODO: ... feldolgozza
                }
                catch (SecureProcessException ex) {
                    System.err.println("Nem megbízható kapcsolódás a " + s.getInetAddress() + " címről.");
                }
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        else {
            final StringBuilder msg = new StringBuilder();
            if (CONFIG.isNew()) {
                msg.append("A konfigurációs fájlt létrehoztam.").append(LS)
                   .append("Kérem, állítsa be megfelelően!").append(LS).append(LS)
                   .append("Konfig fájl útvonala:").append(LS).append(Config.FILE_CONFIG);
                alert(VAL_MESSAGE, msg.toString(), System.out);
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
