package org.dyndns.fzoli.rccar.model;

import java.io.Serializable;

/**
 * Vezérlőjel, ami a jármű irányításáért felelős.
 * @author zoli
 */
public class Controll implements Serializable {
    
    /**
     * Irány.
     */
    public final int X, Y;

    /**
     * Vezérlőjel konstruktor.
     * @param x előre, hátra vagy semerre se mozogjon?
     * @param y balra, jobbra vagy egyenesen mozogjon?
     */
    public Controll(int x, int y) {
        X = x;
        Y = y;
    }
    
}
