package org.dyndns.fzoli.rccar.model;

import java.io.Serializable;

/**
 * 3D pont.
 * @author zoli
 */
public class Point3D implements Serializable {

    /**
     * A három irány értékei.
     */
    public final int x, y, z;

    /**
     * Inicializálás és a három paraméter megadása.
     * @param x x irány értéke
     * @param y y irány értéke
     * @param z z irány értéke
     */
    public Point3D(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

}
