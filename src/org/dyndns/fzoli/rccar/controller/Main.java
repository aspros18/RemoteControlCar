package org.dyndns.fzoli.rccar.controller;

import javax.swing.JFrame;
import org.dyndns.fzoli.rccar.SystemTrayIcon;
import static org.dyndns.fzoli.rccar.UIUtil.setSystemLookAndFeel;
import org.dyndns.fzoli.rccar.UncaughtExceptionHandler;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.showSplashScreenText;
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
     * Még mielőtt lefutna a main metódus, a nyitóképernyő szövege megjelenik és a rendszer LAF, a kivételkezelő és a rendszerikon beállítódik.
     */
    static {
        showSplashScreenText();
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
     * Szimulál 5 másodpercnyi töltést, aztán megjelenít egy üres ablakot, ezzel a nyitóképernyőt eltüntetve.
     */
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(5000);
        new JFrame() {
            {
                setSize(200, 100);
                setTitle("Teszt vége");
                setLocationRelativeTo(this);
                setDefaultCloseOperation(EXIT_ON_CLOSE);
            }
        }.setVisible(true);
    }
    
}
