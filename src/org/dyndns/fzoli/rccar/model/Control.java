package org.dyndns.fzoli.rccar.model;

import java.io.Serializable;

/**
 * Vezérlőjel, ami a jármű irányításáért felelős.
 * @author zoli
 */
public class Control implements Serializable {
    
    /**
     * Irány százalékban.
     */
    private int mX;

    /**
     * Sebesség százalékban.
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

    /**
     * Irány százalékban.
     */
    public int getX() {
        return mX;
    }

    /**
     * Sebesség százalékban.
     */
    public int getY() {
        return mY;
    }

    /**
     * Irány megadása százalékban.
     */
    public void setX(int x) {
        mX = x;
    }

    /**
     * Sebesség százalékban.
     */
    public void setY(int y) {
        mY = y;
    }
    
}
