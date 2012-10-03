package org.dyndns.fzoli.rccar.bridge.socket;

import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * A híd kapcsolatkezelője.
 * TODO: egyelőre teszt
 * @author zoli
 */
public class BridgeHandler extends AbstractSecureServerHandler {

    public BridgeHandler(SSLSocket socket) {
        super(socket);
    }

    @Override
    protected SecureProcess selectProcess() {
        return new BridgeDisconnectProcess(this);
    }
    
}
