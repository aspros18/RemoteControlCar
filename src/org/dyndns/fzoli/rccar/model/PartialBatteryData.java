package org.dyndns.fzoli.rccar.model;

/**
 * A BaseData részadata, ami az akkumulátorszint változását tartalmazza.
 * @author zoli
 */
public abstract class PartialBatteryData<D extends BaseData> extends BasePartialData<D, Integer> {

    /**
     * Részadat inicializálása és beállítása.
     * @param data az akkumulátorszint
     */
    protected PartialBatteryData(Integer data) {
        super(data);
    }

    /**
     * Alkalmazza az akkumulátorszintet a paraméterben megadott adaton.
     * @param d a teljes adat, amin a módosítást alkalmazni kell
     */
    @Override
    public void apply(D d) {
        if (d != null) d.setBatteryLevel(data);
    }
    
}
