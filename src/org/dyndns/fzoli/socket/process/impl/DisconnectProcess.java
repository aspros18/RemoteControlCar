package org.dyndns.fzoli.socket.process.impl;

import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Az implementált osztályok a másik oldallal kiépített kapcsolatot arra használják, hogy
 * másodpercenként ellenőrzik, hogy megszakadt-e a kapcsolat a másik oldallal.
 * @author zoli
 */
public interface DisconnectProcess extends SecureProcess {
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a távoli géppel.
     */
    @Override
    public void run();
    
}
