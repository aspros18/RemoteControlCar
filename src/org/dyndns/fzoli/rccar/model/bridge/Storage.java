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
     * A tároló üzenetküldője.
     */
    private final MessageProcess MESSAGE_PROCESS;
    
    /**
     * Konstruktor.
     * @param a tároló üzenetküldője, a létrehozó
     */
    public Storage(MessageProcess messageProcess) {
        MESSAGE_PROCESS = messageProcess;
    }
    
    /**
     * A tároló üzenetküldőjét adja meg.
     */
    public MessageProcess getMessageProcess() {
        return MESSAGE_PROCESS;
    }
    
    /**
     * Megadja a kliens nevét.
     */
    public String getName() {
        return getMessageProcess().getLocalCommonName();
    }
    
}
