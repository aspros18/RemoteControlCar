package org.dyndns.fzoli.rccar.model.host;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialData;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * Egy hosztra jellemző összes adat, ami a telefon szenzoraiból jön.
 * Amikor a telefon a szenzoroktól új adatot kap, megnézi, hogy a 'refresh time'
 * szerint kell-e üzenni a hídnak.
 * Ha kell üzenni, megnézi, hogy egy vagy több adat változott-e.
 * Ha több adat változott, a hídnak teljes modelt, egyébként részmodelt küld.
 * A híd a saját modeljét frissíti a kapott adat alapján az update metódussal.
 * Kivételt képez az akkumulátor-szint változás - mivel az ritka esemény -, mely
 * változása mindig részadatban érkezik és nem függ a 'refresh time'-tól.
 * @author zoli
 */
public class HostData extends Data<HostData, HostData.HostPartialData> {
    
    /**
     * A HostData részadata.
     * Egy HostPartialData objektumot átadva a HostData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    protected static abstract class HostPartialData<T extends Serializable> extends PartialData<HostData, T> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         */
        protected HostPartialData(T data) {
            super(data);
        }
        
    }
    
    /**
     * A HostData részadata, ami az akkumulátorszint változását tartalmazza.
     */
    public static class PartialBatteryData extends HostPartialData<Integer> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az akkumulátorszint
         */
        public PartialBatteryData(Integer data) {
            super(data);
        }

        /**
         * Alkalmazza az akkumulátorszintet a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(HostData d) {
            if (d != null) d.setBatteryLevel(data);
        }
        
    }
    
    /**
     * A HostData részadata, ami egy pont változását tartalmazza.
     */
    public static class PartialPointData extends HostPartialData<Point3D> {
        
        /**
         * A HostData Point3D változóinak megfeleltetett felsorolás.
         */
        public static enum PointType {
            GPS_POSITION,
            GRAVITATIONAL_FIELD,
            MAGNETIC_FIELD
        }
        
        /**
         * Megmondja, melyik adatról van szó.
         */
        public final PointType type;

        /**
         * Részadat inicializálása és beállítása.
         * @param data a 3D pontadatok
         * @param type melyik 3D pont
         */
        public PartialPointData(Point3D data, PointType type) {
            super(data);
            this.type = type;
        }

        /**
         * Alkalmazza a 3D pontot a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(HostData d) {
            if (d != null && type != null) {
                switch (type) {
                    case GPS_POSITION:
                        d.setGpsPosition(data);
                        break;
                    case GRAVITATIONAL_FIELD:
                        d.setGravitationalField(data);
                        break;
                    case MAGNETIC_FIELD:
                        d.setMagneticField(data);
                }
            }
        }
        
    }
    
    /**
     * GPS koordináta.
     */
    private Point3D gpsPosition;
    
    /**
     * Gravitációs mező.
     */
    private Point3D gravitationalField;
    
    /**
     * Mágneses mező.
     */
    private Point3D magneticField;

    /**
     * Akkumulátor szint százalékban.
     */
    private Integer batteryLevel;
    
    /**
     * Megadja a GPS koordinátát.
     */
    public Point3D getGpsPosition() {
        return gpsPosition;
    }

    /**
     * Megadja a gravitációs mező erősségét.
     */
    public Point3D getGravitationalField() {
        return gravitationalField;
    }

    /**
     * Megadja a mágneses mező erősségét.
     */
    public Point3D getMagneticField() {
        return magneticField;
    }

    /**
     * Megadja a host akkumulátorszintjét százalékban.
     */
    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * Beállítja a GPS koordinátát.
     */
    public void setGpsPosition(Point3D gpsPosition) {
        this.gpsPosition = gpsPosition;
    }

    /**
     * Beállítja a gravitációs mező erősségét.
     */
    public void setGravitationalField(Point3D gravitationalField) {
        this.gravitationalField = gravitationalField;
    }

    /**
     * Beállítja a mágneses mező erősségét.
     */
    public void setMagneticField(Point3D magneticField) {
        this.magneticField = magneticField;
    }

    /**
     * Beállítja az akkumulátorszintet.
     */
    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }
    
    /**
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(HostData d) {
        if (d != null) {
            setGpsPosition(d.getGpsPosition());
            setGravitationalField(d.getGravitationalField());
            setMagneticField(d.getMagneticField());
            setBatteryLevel(d.getBatteryLevel());
        }
    }
    
}
