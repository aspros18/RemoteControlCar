package org.dyndns.fzoli.rccar.test;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.ClientConnectionHelper;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * Teszt elindító kapcsolatmegszakadás detektálására kliens oldalon.
 * @author zoli
 */
public class ClientDisconnectTest implements ConnectionKeys {
    
    /**
     * A kliens oldali teszt kapcsolatkezelő.
     */
    private static class TestClientHandler extends AbstractSecureClientHandler {

        public TestClientHandler(SSLSocket socket, int deviceId, int connectionId) {
            super(socket, deviceId, connectionId);
        }

        @Override
        protected void init() {
            super.init();
            // throw new HandlerException("salala"); // távoli hiba előidézése
        }

        @Override
        protected AbstractSecureProcess selectProcess() {
            switch (getConnectionId()) {
                case KEY_CONN_DISCONNECT:
                    return new ClientDisconnectProcess(this) {
                        
                        @Override
                        protected void onDisconnect() {
                            System.out.println("SERVER DISCONNECT");
                            super.onDisconnect();
                        }
                        
                    };
                case KEY_CONN_DUMMY:
                    return new DummyProcess(this);
            }
            return null;
        }
        
    }
    
    public static void main(String[] args) throws Exception {
        final String url = args.length == 1 ? args[0] : "192.168.20.5";
        new ClientConnectionHelper(5, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_DUMMY, KEY_CONN_DUMMY}) {

            @Override
            protected SSLSocket createConnection() throws GeneralSecurityException, IOException {
                return SSLSocketUtil.createClientSocket(url, 8443, new File("test-certs/ca.crt"), new File("test-certs/controller.crt"), new File("test-certs/controller.key"), new char[]{});
            }

            @Override
            protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
                return new TestClientHandler(socket, deviceId, connectionId);
            }
            
        }.connect();
    }
    
}
