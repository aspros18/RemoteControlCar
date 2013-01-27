package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;

/**
 * Egy vezérlő kliens állapotát adja meg.
 * @author zoli
 */
public class ControllerState implements Serializable, Cloneable {

    /**
     * A vezérlő neve.
     */
    private final String NAME;

    /**
     * Vezérli-e a járművet.
     */
    private boolean controlling;
    
    /**
     * Konstruktor a kezdeti paraméterek megadásával.
     * @param name a vezérlő neve
     * @param controlling vezérli-e a járművet
     */
    public ControllerState(String name, boolean controlling) {
        this.NAME = name;
        this.controlling = controlling;
    }
    
    /**
     * A vezérlő neve.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Megadja, vezérli-e a járművet a vezérlő.
     */
    public boolean isControlling() {
        return controlling;
    }

    /**
     * Beállítja, vezérli-e a járművet a vezérlő.
     */
    public void setControlling(boolean controlling) {
        this.controlling = controlling;
    }
    
    /**
     * Frissíti az objektumot egy másik objektum adataira
     * és jelzi a változást az adatmodelnek, ha az nem null.
     * @param cs az újabb adatok
     * @param d az adatmodel
     */
    public void apply(ControllerState cs, ControllerData d) {
        ControllerState old;
        try {
            old = d == null ? null : cs.clone();
        }
        catch (CloneNotSupportedException ex) {
            return;
        }
        setControlling(cs.isControlling());
        if (d != null) d.onControllerStateChanged(this, old);
    }

    /**
     * Az eredeti paraméterekkel megegyező objektumot hoz létre,
     * hogy össze lehessen később hasonlítani a régi paramétereket az új paraméterekkel.
     */
    @Override
    protected ControllerState clone() throws CloneNotSupportedException {
        return new ControllerState(NAME, controlling);
    }
    
    /**
     * A névvel tér vissza.
     * Fontos, hogy a névvel térjen vissza, mert a Lista rendezés ezt a metódust veszi alapul,
     * valamint a Listából való törléskor kasztolás nélkül csak Object típusú referencia érhető el.
     */
    @Override
    public final String toString() {
        return getName();
    }

}
