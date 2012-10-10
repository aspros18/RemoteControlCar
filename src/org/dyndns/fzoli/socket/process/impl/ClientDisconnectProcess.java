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

    private static final int timeout = 3000, waiting = 250;
    
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
//                Date d1 = new Date();
                in.read();
//                Date d2 = new Date();
                out.write(1);
                out.flush();
//                System.out.println(d2.getTime() - d1.getTime() + " ms");
                Thread.sleep(waiting);
            }
        }
        catch (Exception ex) {
            onDisconnect();
        }
    }
    
}
