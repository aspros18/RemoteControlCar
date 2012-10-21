package org.dyndns.fzoli.rccar.model.pure;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * Egy hosztra jellemző összes adat, ami a telefon szenzoraiból jön.
 * @author zoli
 */
public class HostData implements Serializable {
    
    /**
     * A HostData változóinak megfeleltetett felsorolás.
     */
    public static enum DataType {
        GPS_POSITION,
        GRAVITATIONAL_FIELD,
        MAGNETIC_FIELD
    }
    
    /**
     * A HostData részadata.
     * Egy PartialData objektumot átadva a HostData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    public static class PartialData {
        
        /**
         * Az adat.
         */
        public final Point3D data;
        
        /**
         * Megmondja, melyik adatról van szó.
         */
        public final DataType type;

        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         * @param type adattípus
         */
        public PartialData(Point3D data, DataType type) {
            this.data = data;
            this.type = type;
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
     * Frissíti az adatokat a részadat alapján.
     * @param d a részadat
     */
    public void update(PartialData d) {
        if (d != null && d.type != null) {
            switch (d.type) {
                case GPS_POSITION:
                    setGpsPosition(d.data);
                    break;
                case GRAVITATIONAL_FIELD:
                    setGravitationalField(d.data);
                    break;
                case MAGNETIC_FIELD:
                    setMagneticField(d.data);
            }
        }
    }
    
}
