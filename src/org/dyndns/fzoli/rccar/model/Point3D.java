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
    public final double X, Y, Z;

    /**
     * Inicializálás és a három paraméter megadása.
     * @param x x irány értéke
     * @param y y irány értéke
     * @param z z irány értéke
     */
    public Point3D(double x, double y, double z) {
        X = x;
        Y = y;
        Z = z;
    }

    /**
     * A három érték tömbben.
     */
    public float[] toArray() {
        return new float[] { (float) X, (float) Y, (float) Z };
    }
    
}
