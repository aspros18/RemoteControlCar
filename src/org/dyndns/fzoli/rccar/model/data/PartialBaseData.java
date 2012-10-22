package org.dyndns.fzoli.rccar.model.data;

import java.io.Serializable;

/**
 * Részdat, mely Host és Controller oldalon is használandó.
 * @author zoli
 */
public abstract class PartialBaseData<D extends BaseData, T extends Serializable> extends PartialData<D, T> {

    /**
     * Részadat inicializálása és beállítása.
     * @param data az adat
     */
    protected PartialBaseData(T data) {
        super(data);
    }
    
}
