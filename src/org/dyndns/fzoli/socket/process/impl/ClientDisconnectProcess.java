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
public abstract class ClientDisconnectProcess extends AbstractSecureProcess {

    private static final int timeout = 1000;
    
    public ClientDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Ez a metódus hívódik meg, amikor létrejön a kapcsolat a szerverrel.
     */
    protected void onConnect() {}
    
    /**
     * Ez a metódus hívódik meg, amikor megszakad a kapcsolat a szerverrel.
     */
    protected abstract void onDisconnect();
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a másik oldallal.
     */
    @Override
    public void run() {
        onConnect();
        try {
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            getSocket().setSoTimeout(timeout);
            while(!getSocket().isClosed() && getSocket().isConnected()) {
                in.read();
                out.write(1);
                Thread.sleep(1);
            }
        }
        catch (Exception ex) {
            onDisconnect();
        }
        onDisconnect();
    }
    
}
