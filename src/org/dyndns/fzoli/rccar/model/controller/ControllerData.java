package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * A híd a vezérlőnek ezen osztály objektumait küldi, amikor adatot közöl.
 * @author zoli
 */
public class ControllerData extends BaseData<ControllerData, PartialData<ControllerData, ?>> {
    
    /**
     * A ControllerData részadata.
     * Egy ControllerPartialData objektumot átadva a ControllerData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    protected static abstract class PartialControllerData<T extends Serializable> extends PartialData<ControllerData, T> {
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         */
        protected PartialControllerData(T data) {
            super(data);
        }
        
    }
    
    /**
     * A ControllerData részadata, ami a GPS pozíció változását tartalmazza.
     */
    public static class GpsPartialControllerData extends PartialControllerData<Point3D> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a GPS koordináta
         */
        public GpsPartialControllerData(Point3D data) {
            super(data);
        }

        /**
         * Alkalmazza a GPS koordinátát a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null) {
                d.setGpsPosition(data);
            }
        }
        
    }
    
    /**
     * A HostData részadata, ami az akkumulátorszint változását tartalmazza.
     * @author zoli
     */
    public static class BatteryPartialControllerData extends BatteryPartialBaseData<ControllerData> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az akkumulátorszint
         */
        public BatteryPartialControllerData(Integer data) {
            super(data);
        }
        
    }
    
    /**
     * Északtól fokban való eltérés.
     */
    private Integer way;
    
    /**
     * Pillanatnyi sebesség km/h-ban.
     */
    private Integer speed;
    
}
