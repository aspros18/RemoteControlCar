package org.dyndns.fzoli.rccar.test.cpp;

import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.test.ClientTestDisconnectProcess;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.BufferedStreamDeviceHandler;
import org.dyndns.fzoli.socket.handler.DeviceHandler;
import org.dyndns.fzoli.socket.handler.exception.HandlerException;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * ObjectStream osztályt felváltó readLine() alapú megoldás.
 * C++ teszt része.
 * @author zoli
 */
public class RCHandler extends AbstractSecureClientHandler {

    public RCHandler(SSLSocket socket, int connectionId) {
        super(socket, ConnectionKeys.KEY_DEV_CONTROLLER, connectionId);
    }

    @Override
    protected void init() {
        super.init();
//        throw new HandlerException("Remote error");
    }

    @Override
    protected SecureProcess selectProcess() {
        switch (getConnectionId()) {
            case 5:
                return new RCTestProcess(this);
            default:
                return new ClientTestDisconnectProcess(this, 1000, 10000, 250);
        }
    }
    
    @Override
    protected DeviceHandler createDeviceHandler(InputStream in, OutputStream out) {
        return new BufferedStreamDeviceHandler(in, out);
    }
    
}
