package org.dyndns.fzoli.rccar.controller;

import java.awt.GraphicsEnvironment;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.dyndns.fzoli.rccar.SystemTrayIcon;
import static org.dyndns.fzoli.rccar.SystemTrayIcon.showMessage;
import static org.dyndns.fzoli.rccar.UIUtil.alert;
import static org.dyndns.fzoli.rccar.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.rccar.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.setDefaultSplashMessage;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.socket.ConnectionHelper;

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
     * TODO
     */
    private static void setSystemTrayIcon() {
        // az ikon beállítása
        SystemTrayIcon.setIcon("Mobile-RC", R.getIconImage());
        // a rendszerikon megjelenítése
        SystemTrayIcon.setVisible(true);
    }
    
    /**
     * TODO: Egyelőre semmit nem csinál, de majd a beállításkezelő ablakot fogja megjeleníteni.
     * - Ha a kapcsolódás folyamatban van, megszakad.
     * - Ha modális az ablak és a beállítások változatlanok, a program végetér bezárásakor.
     * - Ha nem modális az ablak, akkor újra kezdődik a kapcsolódás az ablak bezárásakor függetlenül a változástól.
     * - Ha már van kiépítve kapcsolat, és megváltozik a konfiguráció, jelenjen meg Igen/Nem/Mégse ablak:
     *   a) Igen: ablak bezárása, adatok elmentése, kapcsolat bezárása és új konfiggal kapcsolódás
     *   b) Nem: ablak bezárása, adatok elmentése
     *   c) Mégsem: nem tesz semmit, az ablak nem záródik be
     * @param model modális legyen-e az ablak
     */
    private static void showSettingDialog(boolean modal) {
        ; //TODO
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
        System.out.println(CONFIG);
        if (!CONFIG.isFileExists()) {
            alert(VAL_ERROR, (CONFIG.isDefault() ? "Az alapértelmezett konfiguráció nem használható, mert" : "A konfiguráció") + " nem létező fájlra hivatkozik." + LS + "A folytatás előtt a hibát helyre kell hozni.", System.err);
            showSettingDialog(true);
        }
        if (CONFIG.isDefault() || CONFIG.isCertDefault()) {
            if (CONFIG.isDefault()) {
                showMessage(VAL_WARNING, "A konfiguráció beállítása a menüből érhető el. Most ide kattintva is megteheti.", TrayIcon.MessageType.WARNING, new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showSettingDialog(false);
                    }
                
                });
            }
            else if (CONFIG.isCertDefault()) {
                showMessage(VAL_WARNING, "Az alapértelmezett tanúsítvány használatával a kapcsolat nem megbízható!", TrayIcon.MessageType.WARNING);
            }
        }
        //CONFIG.setPassword(new char[] {'a','a','a','a','a','a','a','a'});
        //CONN.connect();
        Thread.sleep(5000);
        System.exit(0);
    }
    
}
