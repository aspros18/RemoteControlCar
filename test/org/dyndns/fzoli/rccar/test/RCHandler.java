package org.dyndns.fzoli.rccar.test;

import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.BufferedStreamDeviceHandler;
import org.dyndns.fzoli.socket.handler.DeviceHandler;

/**
 * ObjectStream osztályt felváltó readLine() alapú megoldás.
 * C++ teszt része.
 * @author zoli
 */
public abstract class RCHandler extends AbstractSecureClientHandler {

    public RCHandler(SSLSocket socket, int connectionId) {
        super(socket, ConnectionKeys.KEY_DEV_CONTROLLER, connectionId);
    }

    @Override
    protected DeviceHandler createDeviceHandler(InputStream in, OutputStream out) {
        return new BufferedStreamDeviceHandler(in, out);
    }
    
}
