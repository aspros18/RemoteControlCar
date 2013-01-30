package org.dyndns.fzoli.rccar.model.host;

import java.io.Serializable;
import java.util.Date;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.ControlPartialBaseData;
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
     * A HostData vezérlő részadata, ami egy boolean érték változását tartalmazza.
     */
    public static class BooleanPartialHostData extends PartialHostData<Boolean> {

        /**
         * A HostData Boolean változóinak megfeleltetett felsorolás.
         */
        public static enum BooleanType {
            STREAMING,
            VEHICLE_CONNECTED,
            UP_2_DATE
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
                        break;
                    case UP_2_DATE:
                        d.setUp2Date(data);
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
         * Ezzel a verzióval 847 bájt egy objektum mérete szerializálva.
         * Ha csak egy 3D pont lenne tárolva, 611 bájt helyigénye lenne,
         * de két pont esetén már 1222 bájt helyre lenne szükség, ellenben
         * az aktuális megoldással csak 888 bájtot igényel két koordináta.
         * Mivel nagyon ritka az az eset, hogy csak egy szenzoradat módosul,
         * ezért ez a megoldás kevesebb adatforgalmat generál, mint az előző.
         * @param data a 3D pontadatok
         */
        public PointPartialHostData(PointData ... data) {
            super(data);
        }
        
        /**
         * Alkalmazza a 3D pontot/pontokat a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(HostData d) {
            if (d != null && data != null && data.length > 0) {
                d.pointChanging = true;
                for (int i = 0; i < data.length; i++) {
                    if (i == data.length - 1) d.pointChanging = false;
                    PointData pd = data[i];
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
     * A HostData vezérlő részadata, ami az autó irányításában játszik szerepet.
     */
    public static class ControlPartialHostData extends ControlPartialBaseData<HostData> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a vezérlőjel
         */
        public ControlPartialHostData(Control data) {
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
     * Az utolsó módosulás előtti szenzor-adat.
     */
    private Point3D previousGpsPosition, previousMagneticField, previousGravitationalField;
    
    /**
     * Az utolsó előtti GPS pozíció módosulásának dátuma.
     */
    private Date previousGpsChangeDate;
    
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
    private Boolean vehicleConnected = false;
    
    /**
     * Megmondja, hogy folyamatban van-e a Point3D adatok beállítása.
     */
    private boolean pointChanging = false;
    
    /**
     * Az északtól való eltéréshez hozzáadódó érték.
     */
    private Integer additionalDegree;
    
    /**
     * Konstruktor Híd oldalra.
     * Kezdetben nem ismert minden adat,
     * az adat frissítésére a hoszt kapcsolódásakor kerül sor.
     */
    public HostData() {
        super();
        init();
    }
    
    /**
     * Konstruktor hoszt oldalra.
     * Az állandó értékek konstruktorból állítódnak be.
     */
    public HostData(boolean fullX, boolean fullY, int additionalDegree) {
        super();
        setFullX(fullX);
        setFullY(fullY);
        setAdditionalDegree(additionalDegree);
        init();
    }
    
    /**
     * Beállítja a kezdeti paramétereket.
     * Kezdetben a jármű áll.
     */
    private void init() {
        setControl(new Control(0, 0));
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
     * Megadja, hogy a GPS adat naprakész-e.
     * Ha nincs megadva, akkor hamis értékkel tér vissza.
     */
    @Override
    public Boolean isUp2Date() {
        Boolean b = super.isUp2Date();
        return b == null ? false : b;
    }

    /**
     * Megmondja, hogy folyamatban van-e a Point3D adatok beállítása.
     * A szerver oldalán hasznos metódus, mert segítségével elkerülhető
     * a fölösleges adatfeldolgozás, amikoris több Point3D kerül kiküldésre.
     * @return true, ha éppen valamelyik Point3D adat állítódik be és miután beállítódott,
     * még legalább egy Point3D adat beállítás meg fog történni; egyébként false.
     */
    protected boolean isPointChanging() {
        return pointChanging;
    }

    /**
     * Megadja az északtól való eltéréshez hozzáadódó értéket.
     */
    public Integer getAdditionalDegree() {
        return additionalDegree;
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
    public Point3D getPreviousGpsPosition() {
        return previousGpsPosition;
    }

    /**
     * Megadja az utolsó módosítás előtti gravitációs mező erősségét.
     */
    public Point3D getPreviousGravitationalField() {
        return previousGravitationalField;
    }
    
    /**
     * Megadja az utolsó módosítás előtti mágneses mező erősségét.
     */
    public Point3D getPreviousMagneticField() {
        return previousMagneticField;
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
    public Date getPreviousGpsChangeDate() {
        return previousGpsChangeDate;
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
    @Override
    public void setControl(Control controll) {
        if (controll == null) controll = new Control(0, 0);
        super.setControl(controll);
    }

    /**
     * Beállítja az északtól való eltéréshez hozzáadódó értéket.
     */
    public void setAdditionalDegree(Integer additionalDegree) {
        this.additionalDegree = additionalDegree;
    }
    
    /**
     * Beállítja a GPS koordinátát.
     * Még mielőtt megváltozna az adat, az előző adat eltárolódik.
     */
    public void setGpsPosition(Point3D gpsPosition) {
        previousGpsChangeDate = gpsChangeDate;
        gpsChangeDate = new Date();
        this.previousGpsPosition = getGpsPosition();
        this.gpsPosition = gpsPosition;
    }

    /**
     * Beállítja a gravitációs mező erősségét.
     */
    public void setGravitationalField(Point3D gravitationalField) {
        this.previousGravitationalField = getGravitationalField();
        this.gravitationalField = gravitationalField;
    }

    /**
     * Beállítja a mágneses mező erősségét.
     */
    public void setMagneticField(Point3D magneticField) {
        this.previousMagneticField = getMagneticField();
        this.magneticField = magneticField;
    }
    
    /**
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(HostData d) {
        if (d != null) {
            setStreaming(d.isStreaming());
            setVehicleConnected(d.isVehicleConnected());
            setGpsPosition(d.getGpsPosition());
            setGravitationalField(d.getGravitationalField());
            setMagneticField(d.getMagneticField());
            setAdditionalDegree(d.getAdditionalDegree());
            super.update(d);
        }
    }

    /**
     * Kinullázza azokat a szenzoradatokat, melyek azonnal megtudhatóak, tehát nem igényelnek online kapcsolatot (minden, ami nem GPS).
     * A többi adatot megtartja, hátha elvész a kapcsolat és csak az utolsó adat lesz elérhető.
     */
    @Override
    public void clear() {
        super.clear();
        previousMagneticField = magneticField = null;
        previousGravitationalField = gravitationalField = null;
    }
    
}
