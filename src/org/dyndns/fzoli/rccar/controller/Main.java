package org.dyndns.fzoli.rccar.controller;

import java.awt.GraphicsEnvironment;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import org.dyndns.fzoli.rccar.UncaughtExceptionHandler;
import org.dyndns.fzoli.rccar.controller.ConnectionProgressFrame.Status;
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
    private static final ConfigEditorWindow CONFIG_EDITOR;
    
    /**
     * Kapcsolódásjelző- és kezelő ablak.
     */
    private static final ConnectionProgressFrame PROGRESS_FRAME;
    
    /**
     * Segédváltozó kapcsolódás kérés detektálására.
     */
    private static boolean connecting = false;
    
    /**
     * Még mielőtt lefutna a main metódus,
     * a nyitóképernyő szövege megjelenik és a rendszer LAF,
     * a kivételkezelő valamint a rendszerikon beállítódik
     * és inicilizálódnak azok az ablakok, melyek jó eséllyel használva lesznek.
     */
    static {
        setDefaultSplashMessage();
        setSystemLookAndFeel();
        setExceptionHandler();
        setSystemTrayIcon();
        // előinicializálom az ablakokat, míg a nyitóképernyő fent van,
        // hogy később ne menjen el ezzel a hasznos idő
        PROGRESS_FRAME = new ConnectionProgressFrame();
        CONFIG_EDITOR = new ConfigEditorWindow(CONFIG);
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
        // az ikon beállítása
        SystemTrayIcon.setIcon("Mobile-RC", R.getIconImage());
        
        // kapcsolatbeállítás opció hozzáadása
        SystemTrayIcon.addMenuItem("Kapcsolatbeállítás", AL_SETTING);
        
        SystemTrayIcon.addMenuItem("Újrakapcsolódás", new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                reconnect();
            }
            
        });
        
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
        if (SplashScreenLoader.isVisible()) {
            SplashScreenLoader.setSplashMessage("Kapcsolódás a szerverhez");
        }
        else {
            showConnectionStatus(Status.CONNECTING);
        }
    }
    
    
    
    /**
     * Beállítja a kapcsolatjelző ablakon a látható ikont és szöveget.
     * Ha nincs megadva státusz, akkor az ablak eltűnik, egyébként a megadott státusz jelenik meg.
     * Ha éppen kapcsolódás van, csak a kapcsolódás státusz állítható be.
     * @param status a kapcsolat státusza
     */
    public static void showConnectionStatus(Status status) {
        if (connecting && status != Status.CONNECTING) return;
        PROGRESS_FRAME.setStatus(status);
    }
    
    /**
     * Ha van kiépítve kapcsolat, bontja azt és új kapcsolatot alakít ki.
     */
    public static void reconnect() {
        if (CONN.isConnected()) {
            CONN.disconnect();
            runClient(500);
        }
    }
    
    /**
     * A program értelme.
     * Kijelzi, hogy elkezdődött a kapcsolódás és kapcsolódik a szerverhez (ha még nem történt meg).
     * Innentől kezdve már a kommunikációtól függ, hogyan folytatódik a program futása.
     */
    public static void runClient() {
        runClient(0);
    }
    
    /**
     * A program értelme.
     * Kijelzi, hogy elkezdődött a kapcsolódás és kapcsolódik a szerverhez (ha még nem történt meg).
     * Késleltetés van beállítva, hogy legyen ideje a felhasználónak észlelni a folyamatot.
     * Innentől kezdve már a kommunikációtól függ, hogyan folytatódik a program futása.
     * @param delay a késleltetés
     */
    public static void runClient(int delay) {
        if (CONN.isConnected()) return;
        connecting = true;
        showConnecting();
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                connecting = false;
                CONN.connect();
            }
            
        }, delay);
    }
    
    /**
     * A vezérlő main metódusa.
     * Ha a grafikus felület nem érhető el, konzolra írja a szomorú tényt és a program végetér.
     * Ha a konfigurációban megadott tanúsítványfájlok nem léteznek, közli a hibát és kényszeríti a kijavítását úgy,
     * hogy feldobja a konfiguráció beállító ablakot és addig nem lehet elhagyni, míg nincs létező fájl beállítva.
     * Ezek után megnézi a program, hogy a publikus teszt tanúsítványok vannak-e használva és ha igen, figyelmezteti a felhasználót.
     * Ha a konfiguráció teljes egészében megegyezik az eredeti beállításokkal, a program közli, hol állítható át.
     * Végül a kliens program elkezdi futását.
     */
    public static void main(String[] args) throws InterruptedException {
        if (GraphicsEnvironment.isHeadless()) { // ha a grafikus felület nem érhető el
            System.err.println("A program futtatásához grafikus környezetre van szükség." + LS + "A program kilép.");
            System.exit(1); // hibakóddal lép ki
        }
        if (!CONFIG.isFileExists()) { // ha a tanúsítvány fájlok egyike nem létezik
            showSettingError((CONFIG.isDefault() ? "Az alapértelmezett konfiguráció nem használható, mert" : "A konfiguráció") + " nem létező fájlra hivatkozik." + LS + "A folytatás előtt a hibát helyre kell hozni.");
            showSettingDialog(true, 1); // kényszerített beállítás és tanúsítvány lapfül előtérbe hozása
        }
        if (CONFIG.isCertDefault()) { // figyelmeztetés
            showSettingWarning("Az alapértelmezett tanúsítvány használatával a kapcsolat nem megbízható!");
        }
        if (CONFIG.isDefault()) { // figyelmeztetés
            showSettingWarning("A konfiguráció beállítása a menüből érhető el. Most ide kattintva is megteheti.");
        }
        runClient(); // és végül a lényeg
    }
    
}
