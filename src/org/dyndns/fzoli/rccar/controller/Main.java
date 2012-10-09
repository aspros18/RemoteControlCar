package org.dyndns.fzoli.rccar.controller;

import java.awt.GraphicsEnvironment;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.dyndns.fzoli.rccar.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.setDefaultSplashMessage;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.socket.ConnectionHelper;
import org.dyndns.fzoli.ui.OptionPane;
import org.dyndns.fzoli.ui.SystemTrayIcon;
import static org.dyndns.fzoli.ui.SystemTrayIcon.showMessage;
import org.dyndns.fzoli.ui.UIUtil;
import static org.dyndns.fzoli.ui.UIUtil.setSystemLookAndFeel;

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
     * Üzenettípusok.
     */
    private static final String VAL_WARNING = "Figyelmeztetés", VAL_ERROR = "Hiba";
    
    /**
     * Új sor jel.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * Eseményfigyelő, ami esemény hatására megjeleníti a kapcsolatbeállító ablakot.
     */
    private static final ActionListener AL_SETTING = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            // kapcsolatbeállító ablak megjelenítése
            showSettingDialog(false, null);
        }

    };
    
    /**
     * Konfiguráció-szerkesztő ablak.
     */
    private static final ConfigEditorWindow CONFIG_EDITOR;
    
    /**
     * Kapcsolódásjelző- és kezelő ablak.
     */
    private static final ConnectionProgressFrame PROGRESS_FRAME;
    
    /**
     * Még mielőtt lefutna a main metódus, a nyitóképernyő szövege megjelenik és a rendszer LAF, a kivételkezelő valamint a rendszerikon beállítódik.
     */
    static {
        setDefaultSplashMessage();
        setSystemLookAndFeel();
        setExceptionHandler();
        setSystemTrayIcon();
        PROGRESS_FRAME = new ConnectionProgressFrame();
        CONFIG_EDITOR = new ConfigEditorWindow(CONFIG);
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
        SystemTrayIcon.addMenuItem("Kapcsolatbeállítás", AL_SETTING);
        
        //szeparátor hozzáadása
        SystemTrayIcon.addMenuSeparator();
        
        // kilépés opció hozzáadása
        SystemTrayIcon.addMenuItem("Kilépés", new ActionListener() {

            /**
             * Ha a kilépésre kattintottak.
             * A program azonnal végetér, ha nincs kiépítve kapcsolat,
             * egyébként megkérdezi a felhasználót, hogy biztos ki akar-e lépni
             * és csak akkor lép ki, ha Igen a válasza.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if (CONN.isConnected()) { // ha van kiépített kapcsolat
                    // megkérdi, biztos-e a kilépésben
                    int opt = OptionPane.showYesNoDialog(R.getIconImage(), "Biztos, hogy kilép a programból?", "Megerősítés");
                    // ha igen, akkor a program kilép
                    if (opt == 0) System.exit(0);
                }
                else { // ha nincs kiépített kapcsolat
                    // a program kilép
                    System.exit(0);
                }
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
    public static void showSettingDialog(boolean force, Integer tabIndex) {
        if (!CONN.isConnected()) CONN.disconnect();
        PROGRESS_FRAME.setVisible(false);
        CONFIG_EDITOR.setTabIndex(tabIndex);
        CONFIG_EDITOR.setModal(force);
        CONFIG_EDITOR.setVisible(true);
    }
    
    /**
     * Hibaüzenetet küld a felhasználónak modális dialógusablakban.
     * @param text a megjelenő szöveg
     */
    private static void showSettingError(String text) {
        UIUtil.alert(VAL_ERROR, text, System.err, R.getIconImage());
    }
    
    /**
     * Figyelmeztetést küld a felhasználónak a buborékablakra.
     * Az üzenetre kattintva a kapcsolatbeállító ablak jelenik meg.
     * @param text a megjelenő szöveg
     */
    private static void showSettingWarning(String text) {
        showMessage(VAL_WARNING, text, TrayIcon.MessageType.WARNING, AL_SETTING);
    }
    
    /**
     * Közli a felhasználóval, hogy a kapcsolódás folyamatban van.
     * Ha a nyitóképernyő még látható, akkor azon történik a jelzés, egyébként
     * a kapcsolódásjelző ablak jelenik meg.
     */
    private static void showConnecting() {
        String msg = "Kapcsolódás a szerverhez...";
        if (SplashScreenLoader.isVisible()) {
            SplashScreenLoader.setSplashMessage(msg);
        }
        else {
            PROGRESS_FRAME.setProgress(true);
            PROGRESS_FRAME.setVisible(true);
        }
    }
    
    /**
     * A vezérlő main metódusa.
     */
    public static void main(String[] args) throws InterruptedException {
        if (GraphicsEnvironment.isHeadless()) {
            System.err.println("A program futtatásához grafikus környezetre van szükség." + LS + "A program kilép.");
            System.exit(1);
        }
        if (!CONFIG.isFileExists()) {
            showSettingError((CONFIG.isDefault() ? "Az alapértelmezett konfiguráció nem használható, mert" : "A konfiguráció") + " nem létező fájlra hivatkozik." + LS + "A folytatás előtt a hibát helyre kell hozni.");
            showSettingDialog(true, 1);            
        }
        if (CONFIG.isCertDefault()) {
            showSettingWarning("Az alapértelmezett tanúsítvány használatával a kapcsolat nem megbízható!");
        }
        if (CONFIG.isDefault()) {
            showSettingWarning("A konfiguráció beállítása a menüből érhető el. Most ide kattintva is megteheti.");
        }
        showConnecting();
        CONN.connect();
    }
    
}
