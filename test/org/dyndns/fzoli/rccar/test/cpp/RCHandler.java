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
public class RCHandler extends AbstractSecureClientHandler implements ConnectionKeys {

    public RCHandler(SSLSocket socket, int connectionId) {
        super(socket, KEY_DEV_PURE_CONTROLLER, connectionId);
    }

    @Override
    protected void init() {
        super.init();
//        throw new HandlerException("Remote error");
    }

    @Override
    protected SecureProcess selectProcess() {
        switch (getConnectionId()) {
            case KEY_CONN_DUMMY:
                return new RCTestProcess(this);
            case KEY_CONN_DISCONNECT:
                return new ClientTestDisconnectProcess(this, 1000, 10000, 250);
            case KEY_CONN_MESSAGE:
                return new RCMessageProcess(this);
            default:
                return null;
        }
    }

    @Override
    protected void onProcessNull() {
        System.out.println("null process");
        super.onProcessNull();
    }
    
    @Override
    protected DeviceHandler createDeviceHandler(InputStream in, OutputStream out) {
        return new BufferedStreamDeviceHandler(in, out);
    }
    
}
