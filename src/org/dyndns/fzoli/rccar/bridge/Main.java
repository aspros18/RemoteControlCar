package org.dyndns.fzoli.rccar.bridge;

import java.awt.Dialog;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.exceptiondialog.UncaughtExceptionDialog;
import org.dyndns.fzoli.exceptiondialog.UncaughtExceptionParameters;
import org.dyndns.fzoli.exceptiondialog.event.UncaughtExceptionAdapter;
import org.dyndns.fzoli.rccar.SystemTrayIcon;
import static org.dyndns.fzoli.rccar.SystemTrayIcon.showMessage;
import static org.dyndns.fzoli.rccar.UIUtil.alert;
import static org.dyndns.fzoli.rccar.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.rccar.bridge.resource.R;
import org.dyndns.fzoli.rccar.test.TestServerProcess;
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
     * Beállítja a rendszerikont.
     * Hozzáadja a kilépés menüopciót beállítja az ikont és megjeleníti azt.
     */
    private static void setSystemTrayIcon() {
        SystemTrayIcon.addMenuItem("Kilépés", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
            
        });
        SystemTrayIcon.setIcon("Mobile-RC híd", R.getBridgeImage());
        SystemTrayIcon.setVisible(true);
    }
    
    /**
     * Beállítja a híd kivételkezelő metódusát.
     * Ha a rendszerikonok támogatva vannak, dialógusablak jeleníti meg a nem kezelt kivételeket,
     * egyébként nem változik az eredeti kivételkezelés.
     */
    private static void setExceptionHandler() {
        if (SystemTrayIcon.isSupported()) {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                String title = "Nem várt hiba";
                UncaughtExceptionParameters params = new UncaughtExceptionParameters(title, "Nem várt hiba keletkezett a program futása alatt.", "Részletek", "Bezárás", "Másolás", "Mindet kijelöl");
                
                /**
                 * Megjeleníti a kivételt.
                 * Ha nem kivétel, hanem hiba keletkezett, egyből modálisan megjelenik az ablak és bezárása után leáll a program.
                 */
                void showExceptionDialog(final Thread t, final Throwable ex, final boolean error) {
                    UncaughtExceptionDialog.showException(t, ex, error ? Dialog.ModalityType.APPLICATION_MODAL : Dialog.ModalityType.MODELESS, params, new UncaughtExceptionAdapter() {

                        @Override
                        public void exceptionDialogClosed() {
                            if (error) System.exit(1);
                        }
                                    
                    });
                }
                
                /**
                 * Ha nem kezelt hiba történik, ez a metódus fut le.
                 * Ha a rendszerikon nem látható, akkor a konzolra íródik a kivétel.
                 * Throwable lehet kivétel vagy hiba is.
                 */
                @Override
                public void uncaughtException(final Thread t, final Throwable ex) {
                    if (SystemTrayIcon.isVisible()) {
                        final boolean error = ex instanceof Error;
                        if (error) {
                            showExceptionDialog(t, ex, error);
                        }
                        else {
                            SystemTrayIcon.showMessage(title, "További részletekért kattintson ide.", TrayIcon.MessageType.ERROR, new ActionListener() {

                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    showExceptionDialog(t, ex, error);
                                }
                            
                            });
                        }
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
     * @throws Error ha nem sikerül a szerver socket létrehozása
     */
    private static SSLServerSocket createServerSocket() {
        try {
            return SecureUtil.createServerSocket(CONFIG.getPort(), CONFIG.getCAFile(), CONFIG.getCertFile(), CONFIG.getKeyFile(), CONFIG.getPassword());
        }
        catch(Exception ex) {
            throw new Error(ex.getMessage());
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
                s = (SSLSocket) ss.accept(); // kliensre várakozik, és ha kapcsolódtak ...
                new Thread(new TestServerProcess(s, 8)).start(); // ... feldolgozza az új szálban ; TODO: teszt, a kapcsolatazonosító: 8
            }
            catch (SecureProcessException ex) {
                if (s != null) showMessage(VAL_MESSAGE, "Nem megbízható kapcsolódás a " + s.getInetAddress() + " címről.", TrayIcon.MessageType.WARNING);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
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
                    alert(VAL_MESSAGE, "A rendszergazdákat tartalmazó adatbázist létrehoztam." + LS + "Mostantól használható az adatbázis.", System.out);
                }
                else {
                    alert(VAL_ERROR, "Hiba a rendszergazdákat tartalmazó adatbázis létrehozása során!" + LS + "A program rendszergazdamentesen indul.", System.err);
                }
            }
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
