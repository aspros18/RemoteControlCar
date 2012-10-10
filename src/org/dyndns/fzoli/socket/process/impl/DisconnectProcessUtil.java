package org.dyndns.fzoli.socket.process.impl;

import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Segédosztály a DisconnectProcess interfész implementálásához.
 * @author zoli
 */
class DisconnectProcessUtil {

    /**
     * Időzítő a második időtúllépés hívására.
     */
    private Timer timer;
    
    /**
     * Megadja, hogy meg lett-e hívva már az {@code onDisconnect} metódus.
     */
    private boolean disconnected = false;
    
    /**
     * Az az objektum, mely meg van segítve.
     */
    private final DisconnectProcess proc;
    
    public DisconnectProcessUtil(DisconnectProcess proc) {
        this.proc = proc;
    }

    /**
     * Ha még nem lett meghívva, meghívódik az {@code onDisconnect} metódus.
     */
    public void callDisconnect(Exception ex) {
        if (!disconnected) proc.onDisconnect(ex);
        disconnected = true;
    }
    
    /**
     * Aktiválja vagy inaktiválja az időzítőt, ami meghívja a második végzetes időtúllépést.
     */
    public void setTimeoutActive(boolean b, final Exception ex) throws SocketException {
        if (b) {
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        callDisconnect(ex);
                    }
                    
                }, proc.getSecondTimeout());
            }
        }
        else {
            if (timer != null) {
                timer.cancel();
                timer = null;
            }
        }
    }
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a klienssel.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     */
    public void onDisconnect() {
        List<SecureProcess> procs = proc.getHandler().getSecureProcesses();
        for (SecureProcess prc : procs) {
            try {
                if (prc.getRemoteCommonName().equals(proc.getRemoteCommonName())) {
                    prc.getSocket().close();
                }
            }
            catch (Exception ex) {
                ;
            }
        }
    }
    
}
