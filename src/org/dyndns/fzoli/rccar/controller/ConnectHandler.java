package org.dyndns.fzoli.rccar.controller;

import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.test.DummyProcess;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.SecureProcess;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * A vezérlő kapcsolatkezelője.
 * @author zoli
 */
public class ConnectHandler extends AbstractSecureClientHandler {

    /**
     * Szép konstruktor.
     */
    public ConnectHandler(SSLSocket socket, int deviceId, int connectionId) {
        super(socket, deviceId, connectionId);
    }

    /**
     * A kapcsolatazonosító alapján eldől, melyik feldolgozót kell indítani.
     * Egyelőre csak teszt.
     */
    @Override
    protected SecureProcess selectProcess() {
        switch (getConnectionId()) {
            case 0:
                return new ClientDisconnectProcess(this);
            default:
                return new DummyProcess(this);
        }
    }
    
}
