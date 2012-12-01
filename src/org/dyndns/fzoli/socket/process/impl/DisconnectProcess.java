package org.dyndns.fzoli.socket.process.impl;

import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Az utód osztályok a másik oldallal kiépített kapcsolatot arra használják, hogy
 * másodpercenként ellenőrzik, hogy megszakadt-e a kapcsolat a másik oldallal.
 * @author zoli
 */
abstract class DisconnectProcess extends AbstractSecureProcess {
    
    /**
     * Konstruktorban beállított konstansok.
     */
    private final int timeout1, timeout2, waiting;

    /**
     * Időzítő a második időtúllépés hívására.
     */
    private Timer timer;
    
    /**
     * Megadja, hogy meg lett-e hívva már az {@code onDisconnect} metódus.
     */
    private boolean disconnected = false;
    
    /**
     * Időtúllépés detektáló konstruktora.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @param timeout1 az első időtúllépés ideje ezredmásodpercben (nem végzetes korlát)
     * @param timeout2 a második időtúllépés ideje ezredmásodpercben (végzetes korlát)
     * @param waiting két ellenőrzés között eltelt idő
     * @throws NullPointerException ha handler null
     */
    public DisconnectProcess(SecureHandler handler, int timeout1, int timeout2, int waiting) {
        super(handler);
        this.timeout1 = timeout1;
        this.timeout2 = timeout2;
        this.waiting = waiting;
    }
    
    /**
     * Az első időtúllépés ideje.
     */
    public int getFirstTimeout() {
        return timeout1;
    }
    
    /**
     * A második időtúllépés ideje.
     */
    public int getSecondTimeout() {
        return timeout2;
    }
    
    /**
     * Két ellenőrzés között eltelt idő.
     */
    public int getWaiting() {
        return waiting;
    }
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a távoli géppel.
     */
    @Override
    public abstract void run();
    
    /**
     * Ez a metódus hívódik meg, amikor létrejön a kapcsolat.
     */
    protected void onConnect() {
        ;
    }
    
    /**
     * A válaszkérés előtt hívódik meg.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    protected void beforeAnswer() throws Exception {
        ;
    }
    
    /**
     * Akkor hívódik meg, amikor sikeresen válasz érkezett a távoli géptől.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    protected void afterAnswer() throws Exception {
        ;
    }
    
    /**
     * Időtúllépés esetén hívódik meg.
     * Az első időtúllépés történt meg, ami még nem végzetes.
     * A metódus ha kivételt dob, az {@code onDisconnect} metódus hívódik meg.
     * A metódus az elkapott kivételt dobja, így alapértelmezésként az első megszakadás
     * esetén már lefut az {@code onDisconnect}
     * @param ex a hibát okozó kivétel
     * @throws Exception az {@code onDisconnect} metódusnak átadott kivétel
     */
    protected void onTimeout(final Exception ex) throws Exception {
        ;
    }
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat.
     * A második időtúllépés történt meg, ami végzetes hiba.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     * @param ex a hibát okozó kivétel
     */
    protected void onDisconnect(Exception ex) {
        getHandler().closeProcesses();
    }
    
    /**
     * Aktiválja vagy inaktiválja az időzítőt, ami meghívja a második végzetes időtúllépést.
     */
    protected void setTimeoutActive(boolean b, final Exception ex) throws SocketException {
        if (b) {
            if (timer == null) { // ha aktiválni kell, csak akkor aktiválódik, ha még nem aktív
                timer = new Timer(); // időzítő létrehozása, ami a 2. időtúllépést hívja meg
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() { // ha letelt az idő
                        callDisconnect(ex); // disconnect esemény hívása, ha még nem hívták meg
                    }
                    
                }, getSecondTimeout());
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
     * Ha még nem lett meghívva, meghívódik az {@code onDisconnect} metódus.
     */
    protected void callDisconnect(Exception ex) {
        if (!disconnected) onDisconnect(ex);
        disconnected = true;
    }
    
}
