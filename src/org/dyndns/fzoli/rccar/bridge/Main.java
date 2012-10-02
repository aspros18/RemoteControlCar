package org.dyndns.fzoli.rccar.bridge;

import java.awt.Dialog;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.exceptiondialog.UncaughtExceptionDialog;
import static org.dyndns.fzoli.rccar.UIUtil.alert;
import static org.dyndns.fzoli.rccar.UIUtil.setSystemLookAndFeel;
import static org.dyndns.fzoli.rccar.bridge.SystemTrayIcon.showMessage;
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
     * Beállítja a rendszerikon menüjét.
     * Hozzáadja a kilépés menüopciót és megjeleníti a rendszerikont.
     */
    private static void setSystemTrayIcon() {
        SystemTrayIcon.addMenuItem("Kilépés", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
            
        });
        SystemTrayIcon.setVisible(true);
    }
    
    /**
     * Beállítja a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket.
     */
    private static void setExceptionHandler() {
        if (SystemTrayIcon.isSupported()) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(final Thread t, final Throwable ex) {
                    if (SystemTrayIcon.isVisible()) {
                        SystemTrayIcon.showMessage("Nem várt hiba", "Részletekért kattintson ide.", TrayIcon.MessageType.ERROR, new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent e) {
                                UncaughtExceptionDialog.showException(t, ex, Dialog.ModalityType.MODELESS, null, null);
                            }
                            
                        });
                    }
                    else {
                        ex.printStackTrace();
                    }
                }
                
            });
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
        Integer i = null; // TODO: teszt után törölni
        System.out.println(i * 3); // TODO: teszt után törölni
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
                    showMessage(VAL_MESSAGE, "Nem megbízható kapcsolódás a " + s.getInetAddress() + " címről.", TrayIcon.MessageType.WARNING);
                }
            }
        }
        catch (Exception ex) {
            System.out.println(i * 3); // TODO: teszt után törölni
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
