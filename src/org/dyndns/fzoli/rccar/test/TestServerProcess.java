package org.dyndns.fzoli.rccar.test;

import java.io.File;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.process.AbstractSecureServerProcess;
import org.dyndns.fzoli.socket.process.SecureProcess;
import org.dyndns.fzoli.socket.process.SecureProcessException;
import org.dyndns.fzoli.socket.process.SecureUtil;

/**
 * Teszt osztály szerver oldalra.
 * @author zoli
 */
public class TestServerProcess extends AbstractSecureServerProcess {

    public TestServerProcess(SSLSocket socket, int connectionId) {
        super(socket, connectionId);
    }

    @Override
    protected void process() {
        test(this);
    }
    
    /**
     * Azt tesztelem, hogy működik-e az elgondolásom.
     */
    public static void test(SecureProcess proc) {
        System.out.println("Device: " + proc.getDeviceId());
        System.out.println("Connection: " + proc.getConnectionId());
        System.out.println("Local name: " + proc.getLocalCommonName());
        System.out.println("Remote name: " + proc.getRemoteCommonName());
        System.out.println();
    }
    
    public static void main(String[] args) throws Exception {
        SSLServerSocket ss = SecureUtil.createServerSocket(8443, new File("test-certs/ca.crt"), new File("test-certs/bridge.crt"), new File("test-certs/bridge.key"), new char[]{});
        while (!ss.isClosed()) {
            SSLSocket s = (SSLSocket) ss.accept();
            try {
                new Thread(new TestServerProcess(s, 10)).start();
            }
            catch (SecureProcessException ex) {
                System.err.println("Nem megbízható kapcsolódás a " + s.getInetAddress() + " címről.");
            }
        }
    }
    
}
