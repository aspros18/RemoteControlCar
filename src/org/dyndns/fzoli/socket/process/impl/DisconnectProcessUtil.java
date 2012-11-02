package org.dyndns.fzoli.socket.process.impl;

import java.net.SocketException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.dyndns.fzoli.socket.Processes;
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
            if (timer == null) { // ha aktiválni kell, csak akkor aktiválódik, ha még nem aktív
                timer = new Timer(); // időzítő létrehozása, ami a 2. időtúllépést hívja meg
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() { // ha letelt az idő
                        callDisconnect(ex); // disconnect esemény hívása, ha még nem hívták meg
                    }
                    
                }, proc.getSecondTimeout());
            }
        }
        else {
            if (timer != null) { // ha deaktiválni kell, csak akkor deaktiválódik, ha még aktív
                timer.cancel(); // időzítő leállítása
                timer = null; // Garbage Collector végezheti a dolgát
            }
        }
    }
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a klienssel.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     */
    public void onDisconnect() {
        List<SecureProcess> procs = proc.getHandler().getSecureProcesses();
        Processes.closeProcesses(procs, proc.getDeviceId(), proc.getRemoteCommonName());
    }
    
}
