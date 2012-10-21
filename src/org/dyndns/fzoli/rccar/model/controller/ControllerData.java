package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialData;

/**
 * A híd a vezérlőnek ezen osztály objektumait küldi, amikor adatot közöl.
 * @author zoli
 */
public class ControllerData extends Data<ControllerData, ControllerData.ControllerPartialData> {
    
    /**
     * A ControllerData részadata.
     * Egy ControllerPartialData objektumot átadva a ControllerData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    protected static abstract class ControllerPartialData<T extends Serializable> extends PartialData<ControllerData, T> {
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         */
        protected ControllerPartialData(T data) {
            super(data);
        }
        
    }
    
    /**
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(ControllerData d) {
        ;
    }
    
}
