package org.dyndns.fzoli.rccar.controller;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.setDefaultSplashMessage;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.socket.ConnectionHelper;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame.Status;
import org.dyndns.fzoli.rccar.controller.view.config.ConfigEditorFrame;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.ui.UncaughtExceptionHandler;
import org.dyndns.fzoli.ui.OptionPane;
import org.dyndns.fzoli.ui.UIUtil;
import static org.dyndns.fzoli.ui.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.ui.systemtray.SystemTrayIcon;
import static org.dyndns.fzoli.ui.systemtray.SystemTrayIcon.showMessage;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;

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
     * Callback, ami megjeleníti a kapcsolatbeállító ablakot.
     */
    private static final Runnable CALLBACK_SETTING = new Runnable() {

        @Override
        public void run() {
            // kapcsolatbeállító ablak megjelenítése
            showSettingDialog(false, null);
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
                int opt = OptionPane.showYesNoDialog(R.getIconImage(), "Biztos, hogy kilép a programból?", "Megerősítés");
                // ha igen, akkor a program kilép
                if (opt == 0) {
                    exiting = true;
                    System.exit(0);
                }
            }
            else { // ha nincs kiépített kapcsolat, a program kilép
                exiting = true;
                System.exit(0);
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
     * A konfigurációt tartalmazó objektum.
     */
    private static final Config CONFIG = Config.getInstance();
    
    /**
     * A híd szerverrel építi ki a kapcsolatot.
     */
    private static final ConnectionHelper CONN = new ConnectionHelper(CONFIG);
    
    /**
     * Ha az értéke true, akkor a program leállítás alatt van.
     * Ez esetben a kapcsolódáskezelő ablak nem jelenik meg,
     * amikor megszakad a híddal a kapcsolat.
     */
    private static boolean exiting = false;
    
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
     * Segédváltozó kapcsolódás kérés detektálására.
     */
    private static boolean connecting = false;
    
    /**
     * Még mielőtt lefutna a main metódus,
     * a nyitóképernyő szövege megjelenik és a rendszer LAF,
     * és a kivételkezelő valamint a rendszerikon beállítódik.
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
        UncaughtExceptionHandler.apply(R.getIconImage());
    }
    
    /**
     * Beállítja a rendszerikont.
     */
    private static void setSystemTrayIcon() {
        // az ikon beállítása
        SystemTrayIcon.setIcon("Mobile-RC", R.getIconImageStream());
        
        // kapcsolatbeállítás opció hozzáadása
        SystemTrayIcon.addMenuItem("Kapcsolatbeállítás", CALLBACK_SETTING);
        
        SystemTrayIcon.addMenuItem("Újrakapcsolódás", new Runnable() {

            @Override
            public void run() {
                reconnect();
            }
            
        });
        
        //szeparátor hozzáadása
        SystemTrayIcon.addMenuSeparator();
        
        // kilépés opció hozzáadása
        SystemTrayIcon.addMenuItem("Kilépés", CALLBACK_EXIT);
    }
    
    /**
     * A beállításkezelő ablakot jeleníti meg.
     * Ha a helyes konfiguráció kényszerített, az ablak bezárása után figyelmeztetés jelenhet meg a beállításokkal kapcsolatban.
     * @param force kényszerítve legyen-e a felhasználó helyes konfiguráció megadására
     * @param tabIndex a megjelenő lapfül
     */
    public static void showSettingDialog(boolean force, Integer tabIndex) {
        if (!CONN.isConnected()) CONN.disconnect();
        PROGRESS_FRAME.setVisible(false);
        CONFIG_EDITOR.setTabIndex(tabIndex);
        CONFIG_EDITOR.setForce(force);
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
        showMessage(VAL_WARNING, text, IconType.WARNING, CALLBACK_SETTING);
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
     * Beállítja a kapcsolatjelző ablakon a látható ikont és szöveget, majd elrejti a többi ablakot.
     * Ha nincs megadva státusz, akkor az ablak eltűnik, egyébként a megadott státusz jelenik meg.
     * Ha éppen kapcsolódás van, csak a kapcsolódás státusz állítható be.
     * @param status a kapcsolat státusza
     */
    public static void showConnectionStatus(Status status) {
        if (!exiting && connecting && status != Status.CONNECTING) return;
        PROGRESS_FRAME.setStatus(status);
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
        CONTROLLER_WINDOWS.refresh();
        CONTROLLER_WINDOWS.setVisible(true);
        SELECTION_FRAME.setVisible(false);
    }
    
    /**
     * Ha van kiépítve kapcsolat, bontja azt és új kapcsolatot alakít ki.
     */
    public static void reconnect() {
        if (CONN.isConnected()) {
            CONN.disconnect();
            runClient(true, false);
        }
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
     * Figyelmeztetést jelenít meg a konfigurációval kapcsolatban, ha az alapértelmezett tanúsítvány van használatban.
     * @param help true esetén megjelenik, hogy hol érhetőek el a beállítások, ha alapértelmezett a konfig; false esetén meg figyelmeztetés
     */
    private static void configAlert(boolean help) {
        if (help) {
            if (CONFIG.isDefault()) {
                showSettingWarning("A konfiguráció beállítása a menüből érhető el. Most ide kattintva is megteheti.");
            }
        }
        else {
            if (CONFIG.isCertDefault()) {
                showSettingWarning("Az alapértelmezett tanúsítvány használatával a kapcsolat nem megbízható!");
            }
        }
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
        final File dir = new File("./");
        if (!dir.canWrite() || !dir.canRead()) {
            showSettingError("A program futtatásához olvasási és írási jogra van szükség." + LS + "A program kilép.");
            System.exit(1);
        }
        NativeInterface.open(); // a natív böngésző támogatás igényli
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // előinicializálom az ablakokat, míg a nyitóképernyő fent van,
                // hogy később ne menjen el ezzel a hasznos idő
                PROGRESS_FRAME = new ConnectionProgressFrame();
                CONFIG_EDITOR = new ConfigEditorFrame(CONFIG);
                CONFIG_EDITOR.addWindowListener(WL_CFG);
                SELECTION_FRAME = new HostSelectionFrame(AL_EXIT);
                CONTROLLER_WINDOWS = new ControllerWindows();
                if (!CONFIG.isCorrect()) { // ha a tanúsítvány fájlok egyike nem létezik
                    showSettingError((CONFIG.isDefault() ? "Az alapértelmezett konfiguráció nem használható, mert" : "A konfiguráció") + " nem létező fájlra hivatkozik." + LS + "A folytatás előtt a hibát helyre kell hozni.");
                    showSettingDialog(true, 1); // kényszerített beállítás és tanúsítvány lapfül előtérbe hozása
                }
                else {
                    configAlert(true); // súgó figyelmeztetés, ha kell
                    runClient(false); // és végül a lényeg
                }
            }

        });
        NativeInterface.runEventPump(); // a natív böngésző támogatás igényli
    }
    
}
