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
     * Konstruktor.
     * Egyelőre a vezérlőnek csak neve van.
     */
    public ControllerState(String name) {
        this.NAME = name;
    }

    /**
     * A vezérlő neve.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Frissíti az objektumot egy másik objektum adataira.
     */
    public void apply(ControllerState cs) {
        ;
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
