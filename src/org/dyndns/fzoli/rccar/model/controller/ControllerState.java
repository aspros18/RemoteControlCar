package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;

/**
 * Egy vezérlő kliens állapotát adja meg.
 * @author zoli
 */
public class ControllerState implements Serializable {

    /**
     * A vezérlő neve.
     */
    private final String NAME;

    /**
     * Vezérli-e a járművet.
     */
    private boolean controlling;
    
    /**
     * Akarja-e vezérelni a járművet.
     */
    private boolean wantControl;
    
    /**
     * Konstruktor a kezdeti paraméterek megadásával.
     * @param name a vezérlő neve
     * @param controlling vezérli-e a járművet
     * @param wantControl akarja-e vezérelni a járművet
     */
    public ControllerState(String name, boolean controlling, boolean wantControl) {
        this.NAME = name;
        this.controlling = controlling;
        this.wantControl = wantControl;
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
     * Megadja, akarja-e vezérelni a járművet a vezérlő.
     */
    public boolean isWantControl() {
        return wantControl;
    }

    /**
     * Beállítja, vezérli-e a járművet a vezérlő.
     */
    public void setControlling(boolean controlling) {
        this.controlling = controlling;
    }

    /**
     * Beállítja, akarja-e vezérelni a járművet a vezérlő.
     */
    public void setWantControl(boolean wantControl) {
        this.wantControl = wantControl;
    }
    
    /**
     * Frissíti az objektumot egy másik objektum adataira
     * és jelzi a változást az adatmodelnek, ha az nem null.
     * @param cs az újabb adatok
     * @param d az adatmodel
     */
    public void apply(ControllerState cs, ControllerData d) {
        ControllerState old = new ControllerState(getName(), isControlling(), isWantControl());
        setControlling(cs.isControlling());
        setWantControl(cs.isWantControl());
        if (d != null) d.onControllerStateChanged(this, old);
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
