package org.dyndns.fzoli.rccar.controller;

import java.awt.TrayIcon;
import org.dyndns.fzoli.rccar.SystemTrayIcon;
import static org.dyndns.fzoli.rccar.SystemTrayIcon.showMessage;
import static org.dyndns.fzoli.rccar.UIUtil.alert;
import static org.dyndns.fzoli.rccar.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.rccar.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.setDefaultSplashMessage;
import org.dyndns.fzoli.rccar.controller.resource.R;

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
     * A vezérlő main metódusa.
     * Nyitóképernyő tesztelés.
     * Szimulál 5 másodpercnyi töltést, aztán végetér a program.
     */
    public static void main(String[] args) throws InterruptedException {
        if (!CONFIG.isFileExists()) {
            alert(VAL_ERROR, "A konfiguráció nem létező fájlra hivatkozik." + LS + "A folytatás előtt a hibát helyre kell hozni.", System.err);
            //TODO: modális ablakként megjeleníteni a beállításokat; ha bezárják, a program lépjen ki
        }
        if (CONFIG.isCertDefault()) {
            showMessage(VAL_WARNING, "Az alapértelmezett tanúsítvány használatával a kapcsolat nem megbízható!", TrayIcon.MessageType.WARNING);
        }
        Thread.sleep(5000);
        System.exit(0);
    }
    
}
