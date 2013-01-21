package org.dyndns.fzoli.rccar.model;

/**
 * Host és Controller oldalon egyaránt használt paraméterek.
 * @author zoli
 */
public class BaseData<D extends BaseData, PD extends PartialBaseData> extends Data<D, PD> {

    /**
     * Megadja, hogy pontosan szabályozható-e az irány.
     * Állandó adat, nincs részadata.
     */
    private Boolean fullX;
    
    /**
     * Megadja, hogy pontosan szabályozható-e a sebesség.
     * Állandó adat, nincs részadata.
     */
    private Boolean fullY;
    
    /**
     * Megadja, hogy a GPS adat naprakész-e.
     */
    private Boolean up2date;
    
    /**
     * Akkumulátor szint százalékban.
     */
    private Integer batteryLevel;

    /**
     * A jármű vezérlőjele.
     */
    private Control control;
    
    protected BaseData() {
        super();
    }

    protected BaseData(D data) {
        super(data);
    }
    
    /**
     * Megadja a host akkumulátorszintjét százalékban.
     */
    public Integer getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * A jármű vezérlőjelét adja vissza.
     */
    public Control getControl() {
        return control;
    }
    
    /**
     * Megadja, hogy pontosan szabályozható-e a az irány.
     * True esetén csak 0 vagy 100 lehet az érték.
     */
    public Boolean isFullX() {
        return fullX;
    }

    /**
     * Megadja, hogy pontosan szabályozható-e a a sebesség.
     * True esetén csak 0 vagy 100 lehet az érték.
     */
    public Boolean isFullY() {
        return fullY;
    }
    
    /**
     * Megadja, hogy a GPS adat naprakész-e.
     */
    public Boolean isUp2Date() {
        return up2date;
    }
    
    /**
     * Beállítja az akkumulátorszintet.
     */
    public void setBatteryLevel(Integer batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    /**
     * A jármű vezérlőjelét állítja be.
     */
    public void setControl(Control control) {
        this.control = control;
    }

    /**
     * Beállítja, hogy pontosan szabályozható-e a az irány.
     */
    public void setFullX(Boolean fullX) {
        this.fullX = fullX;
    }

    /**
     * Beállítja, hogy pontosan szabályozható-e a a sebesség.
     */
    public void setFullY(Boolean fullY) {
        this.fullY = fullY;
    }
    
    /**
     * Beállítja, hogy a GPS adat naprakész-e.
     */
    public void setUp2Date(Boolean up2date) {
        this.up2date = up2date;
    }
    
    /**
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(D d) {
        if (d != null) {
            setBatteryLevel(d.getBatteryLevel());
            setControl(d.getControl());
            setFullX(d.isFullX());
            setFullY(d.isFullY());
            setUp2Date(d.isUp2Date());
        }
    }

    /**
     * Kinullázza az adatokat, így felszabadulhat a memória.
     */
    @Override
    public void clear() {
        up2date = null;
        batteryLevel = null;
        control = null;
    }
    
}
