package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.PartialBatteryData;
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
    protected static abstract class ControllerPartialData<T extends Serializable> extends PartialData<ControllerData, T> {
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         */
        protected ControllerPartialData(T data) {
            super(data);
        }
        
    }
    
    /**
     * A ControllerData részadata, ami a GPS pozíció változását tartalmazza.
     */
    public static class PartialGpsData extends ControllerPartialData<Point3D> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a GPS koordináta
         */
        public PartialGpsData(Point3D data) {
            super(data);
        }

        /**
         * Alkalmazza a GPS koordinátát a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null) {
                ;
            }
        }
        
    }
    
    /**
     * A HostData részadata, ami az akkumulátorszint változását tartalmazza.
     * @author zoli
     */
    public static class PartialBatteryHostData extends PartialBatteryData<ControllerData> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az akkumulátorszint
         */
        public PartialBatteryHostData(Integer data) {
            super(data);
        }
        
    }
    
}
