package org.dyndns.fzoli.rccar.model.host;

import java.io.Serializable;
import java.util.Date;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
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
public class HostData extends BaseData<HostData, PartialBaseData<HostData, ?>> {
    
    /**
     * A HostData részadata.
     * Egy HostPartialData objektumot átadva a HostData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    public static abstract class PartialHostData<T extends Serializable> extends PartialBaseData<HostData, T> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         */
        protected PartialHostData(T data) {
            super(data);
        }
        
    }
    
    /**
     * A HostData vezérlő részadata, ami az autó irányításában játszik szerepet.
     */
    public static class ControlPartialHostData extends PartialHostData<Control> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a vezérlőjel
         */
        public ControlPartialHostData(Control data) {
            super(data);
        }

        /**
         * Alkalmazza az új vezérlőjelet a paraméterben megadott adaton.
         */
        @Override
        public void apply(HostData d) {
            if (d != null) {
                d.setControl(data);
            }
        }
        
    }
    
    /**
     * A HostData vezérlő részadata, ami egy boolean érték változását tartalmazza.
     */
    public static class BooleanPartialHostData extends PartialHostData<Boolean> {

        /**
         * A HostData Boolean változóinak megfeleltetett felsorolás.
         */
        public static enum BooleanType {
            STREAMING,
            VEHICLE_CONNECTED
        }
        
        /**
         * Megmondja, melyik adatról van szó.
         */
        private final BooleanType type;
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data a vezérlőjel
         * @param type melyik változó
         */
        public BooleanPartialHostData(Boolean data, BooleanType type) {
            super(data);
            this.type = type;
        }

        /**
         * Alkalmazza az új értéket a paraméterben megadott adaton.
         */
        @Override
        public void apply(HostData d) {
            if (d != null && type != null) {
                switch (type) {
                    case STREAMING:
                        d.setStreaming(data);
                        break;
                    case VEHICLE_CONNECTED:
                        d.setVehicleConnected(data);
                }
            }
        }
        
    }
    
    /**
     * A HostData részadata, ami egy vagy több pont változását tartalmazza.
     */
    public static class PointPartialHostData extends PartialHostData<PointPartialHostData.PointData[]> {
        
        /**
         * A HostData Point3D változóinak megfeleltetett felsorolás.
         */
        public static enum PointType {
            GPS_POSITION,
            GRAVITATIONAL_FIELD,
            MAGNETIC_FIELD
        }
        
        /**
         * Egy 3D pont három koordinátáját és típusát tartalmazó bab.
         */
        public static class PointData implements Serializable {
            
            /**
             * A 3D pont.
             */
            private final Point3D point;
            
            /**
             * A 3D pont típusa.
             */
            private final PointType type;

            /**
             * Konstruktor.
             */
            public PointData(Point3D point, PointType type) {
                this.point = point;
                this.type = type;
            }
            
        }

        /**
         * Részadat inicializálása és beállítása.
         * @param data a 3D pontadatok
         */
        public PointPartialHostData(PointData... data) {
            super(data);
        }

        /**
         * Alkalmazza a 3D pontot/pontokat a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(HostData d) {
            if (d != null && data != null) {
                for (PointData pd : data) {
                    if (pd.type != null) switch (pd.type) {
                        case GPS_POSITION:
                            d.setGpsPosition(pd.point);
                            break;
                        case GRAVITATIONAL_FIELD:
                            d.setGravitationalField(pd.point);
                            break;
                        case MAGNETIC_FIELD:
                            d.setMagneticField(pd.point);
                    }
                }
            }
        }
        
    }
    
    /**
     * A HostData részadata, ami az akkumulátorszint változását tartalmazza.
     */
    public static class BatteryPartialHostData extends BatteryPartialBaseData<HostData> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az akkumulátorszint
         */
        public BatteryPartialHostData(Integer data) {
            super(data);
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
     * Az utolsó módosulás előtti GPS pozíció.
     */
    private Point3D lastGpsPosition;
    
    /**
     * Az utolsó előtti GPS pozíció módosulásának dátuma.
     */
    private Date lastGpsChangeDate;
    
    /**
     * Az utolsó GPS pozíció módosulásának dátuma.
     */
    private Date gpsChangeDate;
    
    /**
     * Folyamatban van-e az MJPEG streamelés.
     * Kezdetben nincs streamelés.
     */
    private Boolean streaming = false;
    
    /**
     * Megmondja, hogy a telefonhoz csatlakoztatva van-e a jármű.
     */
    private Boolean vehicleConnected;
    
    /**
     * Vezérlőjel.
     * Alapértelmezetten a jármű áll.
     */
    private Control control = new Control(0, 0);
    
    /**
     * Megadja, hogy pontosan szabályozható-e a az irány.
     */
    private boolean fullX = false;
    
    /**
     * Megadja, hogy pontosan szabályozható-e a a sebesség.
     */
    private boolean fullY = false;

    /**
     * Megadja, hogy pontosan szabályozható-e a az irány.
     * True esetén csak 0 vagy 100 lehet az érték.
     */
    public boolean isFullX() {
        return fullX;
    }

    /**
     * Megadja, hogy pontosan szabályozható-e a a sebesség.
     * True esetén csak 0 vagy 100 lehet az érték.
     */
    public boolean isFullY() {
        return fullY;
    }
    
    /**
     * Megadja, hogy folyamatban van-e a streamelés.
     */
    public Boolean isStreaming() {
        return streaming;
    }

    /**
     * Megmondja, hogy a telefonhoz csatlakoztatva van-e a jármű.
     */
    public Boolean isVehicleConnected() {
        return vehicleConnected;
    }

    /**
     * Az autó vezérlőjelét adja vissza.
     */
    public Control getControl() {
        return control;
    }

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
     * Megadja az utolsó módosítás előtti GPS pozíciót.
     */
    public Point3D getLastGpsPosition() {
        return lastGpsPosition;
    }

    /**
     * Megadja az utolsó GPS pozíció módosulásának dátumát.
     */
    public Date getGpsChangeDate() {
        return gpsChangeDate;
    }

    /**
     * Megadja az utolsó előtti GPS pozíció módosulásának dátumát.
     */
    public Date getLastGpsChangeDate() {
        return lastGpsChangeDate;
    }

    /**
     * Beállítja, hogy pontosan szabályozható-e a az irány.
     */
    public void setFullX(boolean fullX) {
        this.fullX = fullX;
    }

    /**
     * Beállítja, hogy pontosan szabályozható-e a a sebesség.
     */
    public void setFullY(boolean fullY) {
        this.fullY = fullY;
    }

    /**
     * Beállítja, van-e streamelés.
     * @param streaming ha null, (az alapértelmezett) false állítódik be
     */
    public void setStreaming(Boolean streaming) {
        if (streaming == null) streaming = false;
        this.streaming = streaming;
    }

    /**
     * Beállítja, hogy a telefonhoz csatlakoztatva van-e a jármű.
     */
    public void setVehicleConnected(Boolean vehicleConnected) {
        this.vehicleConnected = vehicleConnected;
    }
    
    /**
     * Beállítja az autó vezérlőjelét.
     * @param controll ha null, (az alapértelmezett) 0;0 állítódik be
     */
    public void setControl(Control controll) {
        if (controll == null) controll = new Control(0, 0);
        this.control = controll;
    }
    
    /**
     * Beállítja a GPS koordinátát.
     * Még mielőtt megváltozna az adat, az előző adat eltárolódik.
     */
    public void setGpsPosition(Point3D gpsPosition) {
        lastGpsChangeDate = gpsChangeDate;
        gpsChangeDate = new Date();
        this.lastGpsPosition = getGpsPosition();
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
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(HostData d) {
        if (d != null) {
            setFullX(d.isFullX());
            setFullY(d.isFullY());
            setControl(d.getControl());
            setStreaming(d.isStreaming());
            setVehicleConnected(d.isVehicleConnected());
            setGpsPosition(d.getGpsPosition());
            setGravitationalField(d.getGravitationalField());
            setMagneticField(d.getMagneticField());
            super.update(d);
        }
    }
    
}
