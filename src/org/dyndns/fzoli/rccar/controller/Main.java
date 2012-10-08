package org.dyndns.fzoli.rccar.controller;

import java.awt.GraphicsEnvironment;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import org.dyndns.fzoli.rccar.UIUtil;
import static org.dyndns.fzoli.rccar.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.rccar.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.setDefaultSplashMessage;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.socket.ConnectionHelper;
import org.dyndns.fzoli.ui.SystemTrayIcon;
import static org.dyndns.fzoli.ui.SystemTrayIcon.showMessage;

/**
 * A vezérlő indító osztálya.
 * @author zoli
 */
public class Main {
    
    /**
     * A konfigurációt tartalmazó objektum.
     */
    private static final Config CONFIG = Config.getInstance();
    
    /**
     * A híd szerverrel építi ki a kapcsolatot.
     */
    private static final ConnectionHelper CONN = new ConnectionHelper(CONFIG);
    
    /**
     * Konfiguráció-szerkesztő ablak.
     */
    private static ConfigEditorWindow configEditor;
    
    /**
     * Üzenettípusok.
     */
    private static final String VAL_WARNING = "Figyelmeztetés", VAL_ERROR = "Hiba";
    
    /**
     * Új sor jel.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * Még mielőtt lefutna a main metódus, a nyitóképernyő szövege megjelenik és a rendszer LAF, a kivételkezelő valamint a rendszerikon beállítódik.
     */
    static {
        setDefaultSplashMessage();
        setSystemLookAndFeel();
        setExceptionHandler();
        setSystemTrayIcon();
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
     */
    private static void setSystemTrayIcon() {
        // az ikon beállítása
        SystemTrayIcon.setIcon("Mobile-RC", R.getIconImage());
        
        // kapcsolatbeállítás opció hozzáadása
        SystemTrayIcon.addMenuItem("Kapcsolatbeállítás", new ActionListener() {

            /**
             * Ha a kapcsolatbeállításra kattintottak.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                // kapcsolatbeállító ablak megjelenítése
                showSettingDialog(false, null);
            }
            
        });
        
        //szeparátor hozzáadása
        SystemTrayIcon.addMenuSeparator();
        
        // kilépés opció hozzáadása
        SystemTrayIcon.addMenuItem("Kilépés", new ActionListener() {

            /**
             * Ha a kilépésre kattintottak.
             * TODO: ha ki van építve a kapcsolat, kérdezzen rá a kilépésre.
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
     * TODO
     * A beállításkezelő ablakot jeleníti meg.
     * @param force kényszerítve legyen-e a felhasználó helyes konfiguráció megadására
     * @param tabIndex a megjelenő lapfül
     */
    private static void showSettingDialog(boolean force, Integer tabIndex) {
        if (!CONN.isConnected()) CONN.disconnect();
        if (configEditor == null) configEditor = new ConfigEditorWindow(CONFIG) {

            @Override
            public void dispose() {
                super.dispose();
                if (!CONN.isConnected()) CONN.connect();
            }
            
        };
        configEditor.setTabIndex(tabIndex);
        configEditor.setModal(force);
        configEditor.setVisible(true);
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
        UIUtil.alert(title, text, out, R.getIconImage());
    }
    
    /**
     * A vezérlő main metódusa.
     * Nyitóképernyő tesztelés.
     * Szimulál 5 másodpercnyi töltést, aztán végetér a program.
     */
    public static void main(String[] args) throws InterruptedException {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("A program futtatásához grafikus környezetre van szükség." + LS + "A program kilép.");
            System.exit(1);
        }
        if (!CONFIG.isFileExists()) {
            alert(VAL_ERROR, (CONFIG.isDefault() ? "Az alapértelmezett konfiguráció nem használható, mert" : "A konfiguráció") + " nem létező fájlra hivatkozik." + LS + "A folytatás előtt a hibát helyre kell hozni.", System.err);
            showSettingDialog(true, 1);
        }
        if (CONFIG.isCertDefault()) {
            showMessage(VAL_WARNING, "Az alapértelmezett tanúsítvány használatával a kapcsolat nem megbízható!", TrayIcon.MessageType.WARNING);
        }
        if (CONFIG.isDefault()) {
            showMessage(VAL_WARNING, "A konfiguráció beállítása a menüből érhető el. Most ide kattintva is megteheti.", TrayIcon.MessageType.WARNING, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    showSettingDialog(false, null);
                }

            });
        }
        /* TESZT RÉSZ */
        new ConnectionProgressFrame().setVisible(true);
        //CONFIG.setPassword(new char[] {'a','a','a','a','a','a','a','a'});
        //CONN.connect();
        //Thread.sleep(5000);
        //if (CONFIG.isFileExists()) showSettingDialog(true, null);
        //System.exit(0);
    }
    
}
