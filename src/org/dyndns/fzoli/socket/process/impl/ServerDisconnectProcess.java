package org.dyndns.fzoli.socket.process.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Az osztály a klienssel kiépített kapcsolatot arra használja, hogy
 * másodpercenként ellenőrizze, hogy megszakadt-e a kapcsolat.
 * @author zoli
 */
public class ServerDisconnectProcess extends AbstractSecureProcess implements DisconnectProcess {
    
    /**
     * Konstruktorban beállított konstansok.
     */
    private final int timeout1, timeout2, waiting;
    
    /**
     * Kliens és szerver oldalon is megegyező metódusok gyűjteménye.
     */
    private final DisconnectProcessUtil UTIL = new DisconnectProcessUtil(this);
    
    /**
     * Szerver oldalra időtúllépés detektáló.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @param timeout1 az első időtúllépés ideje ezredmásodpercben
     * @param timeout2 a második időtúllépés ideje ezredmásodpercben
     * @param waiting két ellenőrzés között eltelt idő
     * @throws NullPointerException ha handler null
     */
    public ServerDisconnectProcess(SecureHandler handler, int timeout1, int timeout2, int waiting) {
        super(handler);
        this.timeout1 = timeout1;
        this.timeout2 = timeout2;
        this.waiting = waiting;
    }

    /**
     * Az első időtúllépés ideje.
     */
    @Override
    public int getFirstTimeout() {
        return timeout1;
    }

    /**
     * A második időtúllépés ideje.
     */
    @Override
    public int getSecondTimeout() {
        return timeout2;
    }

    /**
     * Két ellenőrzés között eltelt idő.
     */
    @Override
    public int getWaiting() {
        return waiting;
    }

    /**
     * Ez a metódus hívódik meg, amikor létrejön a kapcsolat a klienssel.
     */
    @Override
    public void onConnect() {
        ;
    }
    
    /**
     * A válaszkérés előtt hívódik meg.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    @Override
    public void beforeAnswer() throws Exception {
        ;
    }
    
    /**
     * Akkor hívódik meg, amikor a klienstől sikeresen válasz érkezett.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    @Override
    public void afterAnswer() throws Exception {
        ;
    }
    
    /**
     * Időtúllépés esetén hívódik meg.
     * Az első időtúllépés történt meg, ami még nem végzetes.
     * Ha a metódus kivételt dob, az {@code onDisconnect} metódus hívódik meg.
     * @param ex a hibát okozó kivétel
     * @throws Exception az {@code onDisconnect} metódusnak átadott kivétel
     */
    @Override
    public void onTimeout(final Exception ex) throws Exception {
        ;
    }
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a klienssel.
     * A második időtúllépés történt meg, ami végzetes hiba.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     * @param ex a hibát okozó kivétel
     */
    @Override
    public void onDisconnect(Exception ex) {
        UTIL.onDisconnect();
    }
    
    /**
     * Aktiválja vagy inaktiválja az időzítőt, ami meghívja a második időtúllépést.
     */
    private void setTimeoutActive(boolean b, Exception ex) throws SocketException {
        UTIL.setTimeoutActive(b, ex);
    }
    
    private void callDisconnect(Exception ex) {
        UTIL.callDisconnect(ex);
    }
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a klienssel.
     */
    @Override
    public void run() {
        onConnect();
        try {
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            getSocket().setSoTimeout(getFirstTimeout());
            while(true) {
                try {
                    out.write(1);
                    out.flush();
                    beforeAnswer();
                    in.read();
                    setTimeoutActive(false, null);
                    afterAnswer();
                }
                catch (SocketTimeoutException ex) {
                    setTimeoutActive(true, ex);
                    onTimeout(ex);
                }
                Thread.sleep(getWaiting());
            }
        }
        catch (Exception ex) {
            callDisconnect(ex);
        }
    }
    
}
