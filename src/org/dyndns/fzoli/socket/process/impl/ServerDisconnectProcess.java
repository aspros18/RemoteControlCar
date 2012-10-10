package org.dyndns.fzoli.socket.process.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Az osztály a klienssel kiépített kapcsolatot arra használja, hogy
 * másodpercenként ellenőrizze, hogy megszakadt-e a kapcsolat.
 * @author zoli
 */
public class ServerDisconnectProcess extends AbstractSecureProcess implements DisconnectProcess {
    
    private static final int timeout = 10000, waiting = 250;
    
    public ServerDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Ez a metódus hívódik meg, amikor létrejön a kapcsolat a klienssel.
     */
    protected void onConnect() {
        ;
    }
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a klienssel.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik.
     */
    protected void onDisconnect() {
        DisconnectProcessUtil.onDisconnect(this);
    }
    
    private long max = 0, sum = 0, count = 0;
    private final Date startDate = new Date();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(" [m:s] ");
    
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
            getSocket().setSoTimeout(timeout);
            while(true) {
                out.write(1);
                out.flush();
                Date d1 = new Date();
                in.read();
                Date d2 = new Date();
                long ping = d2.getTime() - d1.getTime();
                max = Math.max(max, ping);
                sum += ping;
                count++;
                System.out.println('S' + dateFormat.format(new Date(d2.getTime() - startDate.getTime())) + ping + " ms (max. " + max + " ms; avg. " + (sum / count) + " ms)");
                Thread.sleep(waiting);
            }
        }
        catch (Exception ex) {
            onDisconnect();
        }
    }
    
}
