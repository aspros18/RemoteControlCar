package org.dyndns.fzoli.rccar.bridge.socket;

import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.test.DummyProcess;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

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
    protected AbstractSecureProcess selectProcess() {
        switch (getConnectionId()) {
            case 1:
                return new DummyProcess(this);
            default:
                return new BridgeDisconnectProcess(this);
        }
    }
    
}
