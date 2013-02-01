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
     * A kliens azonosítója.
     */
    private final String NAME;
    
    /**
     * Konstruktor.
     * @param a tároló üzenetküldője, a létrehozó
     */
    public Storage(MessageProcess messageProcess) {
        this.messageProcess = messageProcess;
        this.NAME = messageProcess.getRemoteCommonName();
    }
    
    /**
     * Az üzenetküldőt adja vissza.
     * Feladata, hogy üzenetet küldjön a kliensnek a setter metódusok hívásakor.
     * Kizárólag a kliens részére való üzenetküldésre és adatbeállításra.
     * Jogosultságkezeléshez és broadcast üzenet küldéséhez a {@link #getReceiver()}
     * metódus használható, ami képes a bejövő üzenetek feldolgozására is.
     */
    public abstract T getSender();
    
    /**
     * Az üzenetfogadót adja vissza.
     * Feladata, hogy a fogadott üzenetet jogkezelten dolgozza fel a setter metódusok által és az adatmódosulásról mindenkit értesítsen, akit érint.
     * Az implementációban a broadcast üzenet küldésre használható lenne a {@link #getSender()} objektum, de erőforrás pazarló lenne minden üzenetküldésre
     * új üzenet objektumot példányosítani, ezért ajánlott újra implementálni az üzenet létrehozást, majd ciklussal elküldeni azt az egy üzenetet több helyre.
     */
    public abstract T getReceiver();
    
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
