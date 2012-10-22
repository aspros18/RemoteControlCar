package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * A híd a vezérlőnek ezen osztály objektumait küldi, amikor adatot közöl.
 * Tartalmazza a kiválasztott autó nevét,
 * az autóhoz tartozó chatüzeneteket egy listában,
 * a felhasználó admin prioritását,
 * az autó gps helyzetét, pillanatnyi sebességét és északtól való eltérését.
 * @author zoli
 */
public class ControllerData extends BaseData<ControllerData, PartialBaseData<ControllerData, ?>> {
    
    //TODO: a modell változása megfeleltethető vezérlőutasításnak is. pl.: autó előre, autó állj, új chat üzenet, autó kiválasztása
    //      bővíteni a modelleket ez alapján (pl. ChatMessage és ChatMessageInfo átírása PartialBaseData alapúra)
    
    /**
     * A ControllerData részadata.
     * Egy ControllerPartialData objektumot átadva a ControllerData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    private static abstract class PartialControllerData<T extends Serializable> extends PartialBaseData<ControllerData, T> {
        
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
    public static class IntegerPartialControllerData extends PartialBaseData<ControllerData, Integer> {

        /**
         * A ControllerData Integer változóinak megfeleltetett felsorolás.
         */
        public static enum IntegerType {
            BATTERY_LEVEL,
            SPEED,
            WAY
        }
        
        /**
         * Megmondja, melyik adatról van szó.
         */
        public final IntegerType type;
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data az akkumulátorszint
         */
        public IntegerPartialControllerData(Integer data, IntegerType type) {
            super(data);
            this.type = type;
        }

        /**
         * Alkalmazza a új részadatot a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null && type != null) {
                switch (type) {
                    case BATTERY_LEVEL:
                        d.setBatteryLevel(data);
                        break;
                    case SPEED:
                        d.setSpeed(data);
                        break;
                    case WAY:
                        d.setWay(data);
                }
            }
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

    /**
     * Az északtól fokban megadott eltérést adja meg.
     */
    public Integer getWay() {
        return way;
    }

    /**
     * A pillanatnyi sebességet km/h-ban adja vissza.
     */
    public Integer getSpeed() {
        return speed;
    }
    
    /**
     * Beállítja az északtól való eltérést.
     * @param way fokban megadott eltérés
     */
    public void setWay(Integer way) {
        this.way = way;
    }

    /**
     * Beállítja a pillanatnyi sebességet.
     * @param speed km/h-ban megadott érték
     */
    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    /**
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(ControllerData d) {
        if (d != null) {
            setSpeed(d.getSpeed());
            setWay(d.getWay());
            super.update(d);
        }
    }
    
}
