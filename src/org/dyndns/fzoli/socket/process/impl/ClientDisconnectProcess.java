package org.dyndns.fzoli.socket.process.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Az osztály a szerverrel kiépített kapcsolatot arra használja, hogy
 * másodpercenként ellenőrizze, hogy megszakadt-e a kapcsolat.
 * @author zoli
 */
public class ClientDisconnectProcess extends AbstractSecureProcess implements DisconnectProcess {

    private static final int timeout = 10000, waiting = 250;
    
    public ClientDisconnectProcess(SecureHandler handler) {
        super(handler);
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
     */
    protected void onDisconnect() {
        DisconnectProcessUtil.onDisconnect(this);
    }
    
    private long max = 0, sum = 0, count = 0;
    
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
                Date d1 = new Date();
                in.read();
                Date d2 = new Date();
                out.write(1);
                out.flush();
                long ping = d2.getTime() - d1.getTime();
                max = Math.max(max, ping);
                sum += ping;
                count++;
                System.out.println("Server write: " + ping + " ms (max. " + max + " ms avg. " + (sum / (double) count) + " ms)");
                Thread.sleep(waiting);
            }
        }
        catch (Exception ex) {
            onDisconnect();
        }
    }
    
}
