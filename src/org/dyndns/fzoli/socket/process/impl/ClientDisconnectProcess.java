package org.dyndns.fzoli.socket.process.impl;

import java.io.InputStream;
import java.io.OutputStream;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Az osztály a szerverrel kiépített kapcsolatot arra használja, hogy
 * másodpercenként ellenőrizze, hogy megszakadt-e a kapcsolat.
 * @author zoli
 */
public class ClientDisconnectProcess extends AbstractSecureProcess implements DisconnectProcess {

    /**
     * Konstruktorban beállított konstansok.
     */
    private final int timeout, waiting;
    
    /**
     * Kliens oldalra időtúllépés detektáló.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @param timeout az időtúllépés ideje ezredmásodpercben
     * @param waiting két ellenőrzés között eltelt idő
     * @throws NullPointerException ha handler null
     */
    public ClientDisconnectProcess(SecureHandler handler, int timeout, int waiting) {
        super(handler);
        this.timeout = timeout;
        this.waiting = waiting;
    }

    /**
     * Ez a metódus hívódik meg, amikor létrejön a kapcsolat a szerverrel.
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
     * Akkor hívódik meg, amikor a szervertől sikeresen válasz érkezett.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    @Override
    public void afterAnswer() throws Exception {
        ;
    }
    
    /**
     * Időtúllépés esetén hívódik meg.
     * A metódus ha kivételt dob, az {@code onDisconnect} metódus hívódik meg.
     * A metódus az elkapott kivételt dobja, így alapértelmezésként az első megszakadás
     * esetén már lefut az {@code onDisconnect}
     * @param ex a hibát okozó kivétel
     * @throws Exception az {@code onDisconnect} metódusnak átadott kivétel
     */
    @Override
    public void onTimeout(Exception ex) throws Exception {
        throw ex;
    }
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a szerverrel.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     * @param ex a hibát okozó kivétel
     */
    @Override
    public void onDisconnect(Exception ex) {
        DisconnectProcessUtil.onDisconnect(this);
    }
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a szerverrel.
     */
    @Override
    public void run() {
        onConnect();
        try {
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            getSocket().setSoTimeout(timeout);
            while(true) {
                try {
                    beforeAnswer();
                    in.read();
                    afterAnswer();
                    out.write(1);
                    out.flush();
                }
                catch (Exception ex) {
                    onTimeout(ex);
                }
                Thread.sleep(waiting);
            }
        }
        catch (Exception ex) {
            onDisconnect(ex);
        }
    }
    
}
