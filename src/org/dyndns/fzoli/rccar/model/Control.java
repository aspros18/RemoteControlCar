package org.dyndns.fzoli.rccar.model;

import java.io.Serializable;

/**
 * Vezérlőjel, ami a jármű irányításáért felelős.
 * @author zoli
 */
public class Control implements Serializable {
    
    /**
     * Irány.
     */
    private int mX;

    /**
     * Sebesség.
     */
    private int mY;
    
    /**
     * Vezérlőjel konstruktor.
     * @param x előre, hátra vagy semerre se mozogjon?
     * @param y balra, jobbra vagy egyenesen mozogjon?
     */
    public Control(int x, int y) {
        mX = x;
        mY = y;
    }

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public void setX(int x) {
        mX = x;
    }

    public void setY(int y) {
        mY = y;
    }
    
}
