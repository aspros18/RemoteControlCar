package org.dyndns.fzoli.rccar.model;

import java.io.Serializable;

/**
 * A Data osztály részadata (részhalmaza).
 * Egy PartialData objektumot átadva a Data objektumnak, egyszerű frissítést lehet végrehajtani.
 * Generikus paraméterben adható meg, milyen típusú adatot tartalmaz az objektum, és
 * hogy az osztály melyik osztálynak a részhalmaza, melyen alkalmaznia kell a módosítást.
 */
public abstract class PartialData<D extends Data, T extends Serializable> implements Serializable {

    /**
     * Az adat.
     */
    public final T data;

    /**
     * Részadat inicializálása és beállítása.
     * @param data az adat
     */
    protected PartialData(T data) {
        this.data = data;
    }

    /**
     * A részadatot alkalmazza a paraméterben megadott adaton.
     * @param d a teljes adat, amin a módosítást alkalmazni kell
     */
    public abstract void apply(D d);
    
    /**
     * A részadatot alkalmazza a paraméterben megadott adaton és ha a teljes adat képes üzenetküldő generálására,
     * akkor létrehozza az üzenetküldőt a paraméterben megadottak alapján, és azzal fog megtörténni az adatmódosítás.
     * @param d a teljes adat, amin a módosítást alkalmazni kell
     * @param senderName az üzenetküldő tanúsítványának CN neve
     * @param senderDevice az üzenetküldő eszközazonosítója
     */
    public final void apply(D d, String senderName, Integer senderDevice) {
        d = Data.createUpdater(d, senderName, senderDevice);
        if (d == null) return;
        apply(d);
    }

}
