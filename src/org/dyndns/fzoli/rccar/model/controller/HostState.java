package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * Egy jármű pillanatnyi állapotát reprezentálja:
 * @author zoli
 */
public class HostState implements Serializable {

    /**
     * GPS koordináta.
     */
    public final Point3D LOCATION;
    
    /**
     * Pillanatnyi sebesség.
     */
    public final Integer SPEED;

    /**
     * Északtól való eltérés fokban megadva.
     */
    public final Integer BEARING;
    
    /**
     * Konstruktor.
     * @param location GPS koordináta
     * @param speed pillanatnyi sebesség
     * @param bearing északtól való eltérés fokban megadva
     */
    public HostState(Point3D location, Integer speed, Integer bearing) {
        LOCATION = location;
        SPEED = speed;
        BEARING = bearing;
    }
    
}
