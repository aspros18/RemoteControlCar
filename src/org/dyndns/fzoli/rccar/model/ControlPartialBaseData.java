package org.dyndns.fzoli.rccar.model;

/**
 * A BaseData vezérlő részadata, ami az autó irányításában játszik szerepet.    
 * @author zoli
 */
public class ControlPartialBaseData<D extends BaseData> extends PartialBaseData<D, Control> {

    /**
     * Részadat inicializálása és beállítása.
     * @param data a vezérlőjel
     */
    protected ControlPartialBaseData(Control data) {
        super(data);
    }

    /**
     * Alkalmazza az új vezérlőjelet a paraméterben megadott adaton.
     */
    @Override
    public void apply(D d) {
        if (d != null) d.setControl(data);
    }
    
}
