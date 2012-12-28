package org.dyndns.fzoli.rccar.model.bridge;

import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * A hoszt és vezérlő tárolójában az a közös,
 * hogy mindkettőnek van név paramétere és az üzenetküldő hozza létre őket.
 * A név alapján lehet a tároló listában keresni.
 * @author zoli
 */
class Storage {

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
        this.NAME = messageProcess.getLocalCommonName();
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
