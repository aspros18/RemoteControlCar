package org.dyndns.fzoli.rccar.model.bridge;

import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * A hoszt és vezérlő tárolójában az a közös,
 * hogy mindkettőnek van név paramétere és az üzenetküldő hozza létre őket.
 * A név alapján lehet a tároló listában keresni.
 * @author zoli
 */
abstract class Storage<T extends BaseData> {

    /**
     * A kliens üzenetküldője.
     */
    private MessageProcess messageProcess;
    
    /**
     * A tárolóhoz tartozó jogkezelt adatmódosító.
     */
    private final DataModifier DATA_MODIFIER = createDataModifier();
    
    /**
     * A kliens azonosítója.
     */
    private final String NAME;
    
    /**
     * Konstruktor.
     * @param a tároló üzenetküldője, a létrehozó
     */
    public Storage(MessageProcess messageProcess) {
        this.messageProcess = messageProcess;
        this.NAME = messageProcess.getLocalCommonName();
    }
    
    /**
     * A jogkezelt adatmódosító példányosítása.
     * A Storage objektum inicializálásakor hívódik meg egyszer,
     * további használatára semmi szükség.
     * @see #getDataModifier()
     */
    protected abstract DataModifier createDataModifier();
    
    /**
     * Az üzenetküldőt adja vissza.
     * Feladata, hogy üzenetet küldjön a kliensnek a setter metódusok hívásakor.
     */
    public abstract T getSender();
    
    /**
     * Az üzenetfogadót adja vissza.
     * Feladata, hogy a fogadott üzenetet dolgozza fel, amikor a setter(ek) meghívódnak.
     */
    public abstract T getReceiver();
    
    /**
     * A jogkezelt adatmódosítót adja vissza.
     */
    public DataModifier getDataModifier() {
        return DATA_MODIFIER;
    }
    
    /**
     * A tároló üzenetküldőjét adja meg.
     */
    public MessageProcess getMessageProcess() {
        return messageProcess;
    }

    /**
     * Beállítja az új üzenetküldőt.
     * Abban az esetben, ha új üzenetküldő jön létre, le kell váltani a régit.
     * Ez akkor történik meg, ha megszakad a kapcsolat és újra létrejön vagy
     * már tartozik egy tároló a klienshez.
     */
    public void setMessageProcess(MessageProcess messageProcess) {
        this.messageProcess = messageProcess;
    }
    
    /**
     * Megadja a kliens nevét.
     */
    public String getName() {
        return NAME;
    }
    
}
