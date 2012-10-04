package org.dyndns.fzoli.rccar.test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.Handler;
import org.dyndns.fzoli.socket.handler.event.HandlerListener;

/**
 *
 * @author zoli
 */
public abstract class ClientConnectingTest {

    private final int deviceId;
    private final int[] connectionIds;

    private final HandlerListener listener = new HandlerListener() {

        @Override
        public void onProcessSelected(Handler handler) {
            synchronized(connectionIds) {
                for (int i = 1; i < connectionIds.length; i++) {
                    runHandler(connectionIds[i], false);
                }
            }
        }
        
    };
    
    public ClientConnectingTest(int deviceId, int[] connectionIds) {
        if (connectionIds == null || connectionIds.length < 1) throw new IllegalArgumentException("At least one Connection ID needs to be added");
        this.deviceId = deviceId;
        this.connectionIds = connectionIds;
    }
    
    protected abstract SSLSocket createConnection() throws GeneralSecurityException, IOException;
    
    protected abstract AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId);
    
    private void runHandler(int connectionId, boolean addListener) {
        try {
            AbstractSecureClientHandler handler = createHandler(createConnection(), deviceId, connectionId);
            if (addListener) handler.addHandlerListener(listener);
            new Thread(handler).start();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void connect() {
        runHandler(connectionIds[0], true);
    }
    
}
