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
     * Az összes adat frissítése adatmodell segítségével.
     */
    public abstract void update(D d);
    
    /**
     * Kinullázza az adatokat, így felszabadulhat a memória.
     */
    public abstract void clear();
    
}
