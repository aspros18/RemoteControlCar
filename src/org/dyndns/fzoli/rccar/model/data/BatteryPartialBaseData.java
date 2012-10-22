package org.dyndns.fzoli.rccar.model.data;

/**
 * A BaseData részadata, ami az akkumulátorszint változását tartalmazza.
 * @author zoli
 */
public abstract class BatteryPartialBaseData<D extends BaseData> extends PartialBaseData<D, Integer> {

    /**
     * Részadat inicializálása és beállítása.
     * @param data az akkumulátorszint
     */
    protected BatteryPartialBaseData(Integer data) {
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
