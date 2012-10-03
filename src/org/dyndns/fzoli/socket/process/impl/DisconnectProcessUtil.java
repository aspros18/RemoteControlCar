package org.dyndns.fzoli.socket.process.impl;

import java.util.List;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Segédosztály a DisconnectProcess interfész implementálásához.
 * @author zoli
 */
class DisconnectProcessUtil {
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a klienssel.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     */
    public static void onDisconnect(DisconnectProcess sp) {
        List<SecureProcess> procs = sp.getHandler().getSecureProcesses();
        for (SecureProcess proc : procs) {
            try {
                if (proc.getRemoteCommonName().equals(sp.getRemoteCommonName())) {
                    proc.getSocket().close();
                }
            }
            catch (Exception ex) {
                ;
            }
        }
    }
    
}
