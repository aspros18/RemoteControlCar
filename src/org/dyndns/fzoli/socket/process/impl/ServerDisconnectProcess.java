package org.dyndns.fzoli.socket.process.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.handler.SecureHandlerException;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Az osztály a klienssel kiépített kapcsolatot arra használja, hogy
 * másodpercenként ellenőrizze, hogy megszakadt-e a kapcsolat.
 * @author zoli
 */
public abstract class ServerDisconnectProcess extends AbstractSecureProcess {
    
    private static final int timeout = 1000, delay = 200;
    
    private static final int waiting = timeout - delay;
    
    public ServerDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Ez a metódus hívódik meg, amikor a kapcsolat megszakad a klienssel.
     */
    protected abstract void onDisconnect();
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a másik oldallal.
     */
    @Override
    public void run() {
        try {
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            getSocket().setSoTimeout(timeout);
            while(!getSocket().isClosed() && getSocket().isConnected()) {
                out.write(1);
                in.read();
                Thread.sleep(waiting);
            }
        }
        catch (Exception ex) {
            onDisconnect();
        }
    }
    
}
