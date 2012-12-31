package org.dyndns.fzoli.rccar.model;

import java.io.Serializable;

/**
 * Az adatmodellek alapja.
 * Mindegyik adatmodellnek vannak részhalmazai.
 * Az adatmodell képes frissíteni magát önmagával vagy a részhalmazaival.
 * A generikus paraméterek segítségével megadható, hogy a frissítés
 * mely konkrét típusokkal mehessen végbe.
 * @author zoli
 */
public abstract class Data<D extends Data, PD extends PartialData> implements Serializable {

    /**
     * Nem példányosítható kintről, csak örökölhető.
     */
    protected Data() {
    }
    
    /**
     * Adatmodell létrehozása egy másik adatmodell objektum adataival.
     * @param data a másik adatmodell
     */
    protected Data(D data) {
        if (data != null) update(data);
    }
    
    /**
     * Két objektumról állapítja meg, hogy egyenlőek-e.
     * @return True, ha a két objektum egyenlő vagy mindkét paraméter null, egyébként false.
     */
    protected boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null ^ o2 == null) return false;
        return o1.equals(o2);
    }
    
    /**
     * Egy adat frissítése részhalmaz segítségével.
     */
    public void update(PD pd) {
        if (pd != null) pd.apply(this);
    }
    
    /**
     * Egy adat frissítése részhalmaz segítségével és üzenetküldés, ha támogatott.
     */
    public void update(PD pd, String senderName, Integer senderDevice) {
        if (pd != null) pd.apply(this, senderName, senderDevice);
    }
    
    /**
     * Az összes adat frissítése adatmodell segítségével.
     */
    public abstract void update(D d);
    
    /**
     * Az összes adat frissítése adatmodell segítségével és üzenetküldés, ha támogatott.
     */
    public void update(D d, String senderName, Integer senderDevice) {
        d = createUpdater(d, senderName, senderDevice);
        if (d == null) return;
        update(d);
    }
    
    /**
     * Kinullázza az adatokat, így felszabadulhat a memória.
     */
    public abstract void clear();
    
    /**
     * Ha a teljes adat képes üzenetküldő generálására, akkor létrehozza az üzenetküldőt a paraméterben megadottak alapján,
     * egyébként a paraméterben megadott teljes adattal tér vissza módosítatlanul.
     * @param d a teljes adat, amin a módosítást alkalmazni kell
     * @param senderName az üzenetküldő tanúsítványának CN neve
     * @param senderDevice az üzenetküldő eszközazonosítója
     * @return az adatmódosító, amin hívhatóak a setter metódusok
     */
    public static <D extends Data> D createUpdater(D d, String senderName, Integer senderDevice) {
        if (d == null) return null;
        D dat = d;
        if (d instanceof DataSender) {
            dat = ((DataSender<D>) d).createSender(senderName, senderDevice);
        }
        return dat;
    }
    
}
