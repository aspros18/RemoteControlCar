package org.dyndns.fzoli.rccar.test;

import java.io.File;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.handler.SecureHandlerException;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.socket.process.impl.ServerDisconnectProcess;

/**
 * Teszt elindító kapcsolatmegszakadás detektálására szerver oldalon.
 * @author zoli
 */
public class ServerDisconnectTest {
    
    private static DummyProcess dummy;
    
    public static void main(String[] args) throws Exception {
        SSLServerSocket ss = SSLSocketUtil.createServerSocket(8443, new File("test-certs/ca.crt"), new File("test-certs/bridge.crt"), new File("test-certs/bridge.key"), new char[]{});
        while (!ss.isClosed()) {
            SSLSocket s = (SSLSocket) ss.accept();
            try {
                new Thread(new AbstractSecureServerHandler(s) {

                    @Override
                    protected AbstractSecureProcess selectProcess() { // szerver oldali teszt feldolgozó használata
                        switch (getConnectionId()) {
                            case 1:
                                return new DummyProcess(this);
                            default:
                                return new ServerDisconnectProcess(this) {

                                    @Override
                                    protected void onDisconnect() {
                                        System.out.println("CLIENT DISCONNECT");
                                        try {
                                            if (dummy != null) dummy.getSocket().close();
                                        }
                                        catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }

                                };
                        }
                    }
                    
                }).start(); // új szálban indítás
            }
            catch (SecureHandlerException ex) {
                System.err.println("Nem megbízható kapcsolódás a " + s.getInetAddress() + " címről.");
            }
        }
    }
    
}
