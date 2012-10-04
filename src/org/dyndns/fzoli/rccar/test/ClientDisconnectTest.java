package org.dyndns.fzoli.rccar.test;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * Teszt elindító kapcsolatmegszakadás detektálására kliens oldalon.
 * @author zoli
 */
public class ClientDisconnectTest {
    
    /**
     * A kliens oldali teszt kapcsolatkezelő.
     */
    private static class TestClientHandler extends AbstractSecureClientHandler {

        public TestClientHandler(SSLSocket socket, int deviceId, int connectionId) {
            super(socket, deviceId, connectionId);
        }

        @Override
        protected AbstractSecureProcess selectProcess() {
            switch (getConnectionId()) {
                case 0:
                    return new ClientDisconnectProcess(this) {
                        
                        @Override
                        protected void onDisconnect() {
                            System.out.println("SERVER DISCONNECT");
                            super.onDisconnect();
                        }
                        
                    };
                default:
                    return new DummyProcess(this);
            }
        }
        
    }
    
    /**
     * Kapcsolódik a szerverhez teszt célból.
     */
    private static SSLSocket createSocket() throws GeneralSecurityException, IOException {
        return SSLSocketUtil.createClientSocket("192.168.20.5", 8443, new File("test-certs/ca.crt"), new File("test-certs/controller.crt"), new File("test-certs/controller.key"), new char[]{});
    }
    
    public static void main(String[] args) throws Exception {
        // az első kapcsolódás a szerverhez új szálban
        new Thread(new TestClientHandler(createSocket(), 5, 0) { // eszközazonosító: 5; kapcsolatazonosító: 0

            @Override
            protected void onProcessSelected() { // ha sikerült az első kapcsolódás
                try {
                    for (int i = 1; i <= 3; i++) { // további három tesztkapcsolat kialakítása; remélhetőleg sikeres lesz, mivel az első kapcsolódás sikerült
                        new Thread(new TestClientHandler(createSocket(), 5, i) {}).start(); // új szálban indítás; eszközazonosító: 5; kapcsolatazonosítók: 1 - 3
                    }
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
        }).start();
    }
    
}
