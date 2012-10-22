package org.dyndns.fzoli.rccar.model.data;

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
     * Egy adat frissítése részhalmaz segítségével.
     */
    public void update(PD pd) {
        if (pd != null) pd.apply(this);
    }
    
    /**
     * Az összes adat frissítése adatmodell segítségével.
     */
    public abstract void update(D d);
    
}
