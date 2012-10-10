package org.dyndns.fzoli.socket.process.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    protected void onConnect() {
        ;
    }
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a szerverrel.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     * @param ex a hibát okozó kivétel
     */
    protected void onDisconnect(Exception ex) {
        DisconnectProcessUtil.onDisconnect(this);
    }
    
    /**
     * Időtúllépés esetén hívódik meg.
     * A metódus ha kivételt dob, az {@code onDisconnect} metódus hívódik meg.
     * A metódus az elkapott kivételt dobja, így alapértelmezésként az első megszakadás
     * esetén már lefut az {@code onDisconnect}
     * @param ex a hibát okozó kivétel
     * @throws Exception az {@code onDisconnect} metódusnak átadott kivétel
     */
    protected void onTimeout(Exception ex) throws Exception {
        throw ex;
    }
    
    /**
     * Akkor hívódik meg, amikor a szervertől sikeresen válasz érkezett.
     * @throws Exception az {@code onTimeout} metódusnak átadott kivétel
     */
    protected void onAnswer() throws Exception {
        ;
    }
    
//    private long max = 0, sum = 0, count = 0;
//    private final Date startDate = new Date();
//    private final SimpleDateFormat dateFormat = new SimpleDateFormat(" [m:s] ");
    
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
//                    Date d1 = new Date();
                    in.read();
//                    Date d2 = new Date();
                    out.write(1);
                    out.flush();
                    onAnswer();
//                    long ping = d2.getTime() - d1.getTime();
//                    max = Math.max(max, ping);
//                    sum += ping;
//                    count++;
//                    System.out.println('C' + dateFormat.format(new Date(d2.getTime() - startDate.getTime())) + ping + " ms (max. " + max + " ms; avg. " + (sum / count) + " ms)");
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
