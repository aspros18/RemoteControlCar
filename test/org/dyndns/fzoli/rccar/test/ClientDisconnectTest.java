package org.dyndns.fzoli.rccar.test;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.ClientConnectionHelper;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

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
                    return new ClientTestDisconnectProcess(this, 1000, 10000, 250);
                case KEY_CONN_DUMMY:
                    return new DummyProcess(this);
            }
            return null;
        }
        
    }
    
    /**
     * Linuxon tesztelve helyben futó szerverrel, de Wi-Fi LAN IP címmel.
     */
    private static String getIP() throws SocketException {
        for (final Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
            final NetworkInterface cur = interfaces.nextElement();
            if (cur.isLoopback() || !cur.getName().equals("wlan0")) continue;
            for (final InterfaceAddress addr : cur.getInterfaceAddresses()) {
                final InetAddress inet_addr = addr.getAddress();
                if (!(inet_addr instanceof Inet4Address)) continue;
                return inet_addr.getHostAddress();
            }
        }
        return null;
    }
    
    public static void main(String[] args) throws Exception {
        final String url = getIP();
        new ClientConnectionHelper(5, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_MESSAGE}) {

            @Override
            protected SSLSocket createConnection() throws GeneralSecurityException, IOException {
                return SSLSocketUtil.createClientSocket(url, 8443, new File("test-certs/ca.crt"), new File("test-certs/controller.crt"), new File("test-certs/controller.key"), new char[]{}, null);
            }

            @Override
            protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
                return new TestClientHandler(socket, deviceId, connectionId);
            }
            
        }.connect();
    }
    
}
