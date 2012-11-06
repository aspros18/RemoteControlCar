package org.dyndns.fzoli.rccar.model;

/**
 * Host és Controller oldalon egyaránt használt paraméterek.
 * @author zoli
 */
public class BaseData<D extends BaseData, PD extends PartialBaseData> extends Data<D, PD> {

    /**
     * Akkumulátor szint százalékban.
     */
    private Integer batteryLevel;
    
    /**
     * Megadja a host akkumulátorszintjét százalékban.
     */
    public Integer getBatteryLevel() {
        return batteryLevel;
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
            setBatteryLevel(d.getBatteryLevel());
        }
    }

    /**
     * Kinullázza az adatokat, így felszabadulhat a memória.
     */
    @Override
    public void clear() {
        batteryLevel = null;
    }
    
}
