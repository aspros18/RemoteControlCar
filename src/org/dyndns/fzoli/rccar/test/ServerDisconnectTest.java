package org.dyndns.fzoli.rccar.test;

import java.io.File;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;
import org.dyndns.fzoli.socket.handler.SecureHandlerException;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.socket.process.impl.ServerDisconnectProcess;

/**
 * Teszt elindító kapcsolatmegszakadás detektálására szerver oldalon.
 * @author zoli
 */
public class ServerDisconnectTest implements ConnectionKeys {
    
    public static void main(String[] args) throws Exception {
        SSLServerSocket ss = SSLSocketUtil.createServerSocket(8443, new File("test-certs/ca.crt"), new File("test-certs/bridge.crt"), new File("test-certs/bridge.key"), new char[]{});
        while (!ss.isClosed()) {
            SSLSocket s = (SSLSocket) ss.accept();
            try {
                new Thread(new AbstractSecureServerHandler(s) {

                    @Override
                    protected AbstractSecureProcess selectProcess() { // szerver oldali teszt feldolgozó használata
                        switch (getConnectionId()) {
                            case KEY_CONN_DISCONNECT:
                                return new ServerDisconnectProcess(this, 1000, 250) {

                                    @Override
                                    public void onDisconnect(Exception ex) {
                                        System.out.println("CLIENT DISCONNECT");
                                        super.onDisconnect(ex);
                                    }

                                };
                            case KEY_CONN_DUMMY:
                                return new DummyProcess(this);
                        }
                        return null;
                    }
                    
                }).start(); // új szálban indítás
            }
            catch (SecureHandlerException ex) {
                ex.printStackTrace();
            }
        }
    }
    
}
