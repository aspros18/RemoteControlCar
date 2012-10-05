package org.dyndns.fzoli.rccar.controller;

import javax.swing.JFrame;
import static org.dyndns.fzoli.rccar.controller.SplashScreenLoader.showSplashScreenText;

/**
 * A vezérlő indító osztálya.
 * @author zoli
 */
public class Main {
    
    /**
     * Még mielőtt lefutna a main metódus, a nyitóképernyő szövege megjelenik.
     */
    static {
        showSplashScreenText();
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
