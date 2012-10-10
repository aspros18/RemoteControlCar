package org.dyndns.fzoli.socket.process.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Az osztály a klienssel kiépített kapcsolatot arra használja, hogy
 * másodpercenként ellenőrizze, hogy megszakadt-e a kapcsolat.
 * @author zoli
 */
public class ServerDisconnectProcess extends AbstractSecureProcess implements DisconnectProcess {
    
    private static final int timeout = 3000, waiting = 250;
    
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
                System.out.println("Server Ping: " + (d2.getTime() - d1.getTime()) + " ms");
                Thread.sleep(waiting);
            }
        }
        catch (Exception ex) {
            onDisconnect();
        }
    }
    
}
