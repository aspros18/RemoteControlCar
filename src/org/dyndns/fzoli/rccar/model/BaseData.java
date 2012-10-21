package org.dyndns.fzoli.rccar.model;

/**
 * Host és Controller oldalon egyaránt használt paraméterek.
 * @author zoli
 */
public abstract class BaseData<D extends BaseData, PD extends PartialData> extends Data<D, PD> {

    /**
     * GPS koordináta.
     */
    private Point3D gpsPosition;
    
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
    public void update(D d) {
        if (d != null) {
            setGpsPosition(d.getGpsPosition());
            setBatteryLevel(d.getBatteryLevel());
        }
    }
    
}
