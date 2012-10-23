package org.dyndns.fzoli.rccar.model.host;

import java.io.Serializable;
import java.util.Date;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
import org.dyndns.fzoli.rccar.model.Controll;
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
    private static abstract class PartialHostData<T extends Serializable> extends PartialBaseData<HostData, T> {

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
    public static class ControllPartialHostData extends PartialHostData<Controll> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a vezérlőjel
         */
        public ControllPartialHostData(Controll data) {
            super(data);
        }

        /**
         * Alkalmazza az új vezérlőjelet a paraméterben megadott adaton.
         */
        @Override
        public void apply(HostData d) {
            if (d != null) {
                d.setControll(data);
            }
        }
        
    }
    
    /**
     * A HostData vezérlő részadata, ami megmondja, hogy kell-e streamelni.
     */
    public static class StreamingPartialHostData extends PartialHostData<Boolean> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a vezérlőjel
         */
        public StreamingPartialHostData(Boolean data) {
            super(data);
        }

        /**
         * Alkalmazza az új vezérlőjelet a paraméterben megadott adaton.
         */
        @Override
        public void apply(HostData d) {
            if (d != null) {
                d.setStreaming(data);
            }
        }
        
    }
    
    /**
     * A HostData részadata, ami egy pont változását tartalmazza.
     */
    public static class PointPartialHostData extends PartialHostData<Point3D> {
        
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
        public PointPartialHostData(Point3D data, PointType type) {
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
     * Vezérlőjel.
     * Alapértelmezetten a jármű áll.
     */
    private Controll controll = new Controll(0, 0);

    /**
     * Megadja, hogy folyamatban van-e a streamelés.
     */
    public Boolean isStreaming() {
        return streaming;
    }

    /**
     * Az autó vezérlőjelét adja vissza.
     */
    public Controll getControll() {
        return controll;
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
     * Beállítja, van-e streamelés.
     * @param streaming ha null, (az alapértelmezett) false állítódik be
     */
    public void setStreaming(Boolean streaming) {
        if (streaming == null) streaming = false;
        this.streaming = streaming;
    }
    
    /**
     * Beállítja az autó vezérlőjelét.
     * @param controll ha null, (az alapértelmezett) 0;0 állítódik be
     */
    public void setControll(Controll controll) {
        if (controll == null) controll = new Controll(0, 0);
        this.controll = controll;
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
            setControll(d.getControll());
            setStreaming(d.isStreaming());
            setGpsPosition(d.getGpsPosition());
            setGravitationalField(d.getGravitationalField());
            setMagneticField(d.getMagneticField());
            super.update(d);
        }
    }
    
}
