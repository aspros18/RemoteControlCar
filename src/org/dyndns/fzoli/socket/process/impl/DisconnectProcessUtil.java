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
    public static void onDisconnect(DisconnectProcess sp) { //TODO: bugos a szerver oldalon valamiért
        List<SecureProcess> procs = sp.getHandler().getSecureProcesses();
        System.out.println(procs);
        for (SecureProcess proc : procs) {
            try {
                System.out.println(proc.getRemoteCommonName() + " ; " + sp.getRemoteCommonName());
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
