package org.dyndns.fzoli.rccar.test;

import java.io.File;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.process.AbstractSecureClientProcess;
import org.dyndns.fzoli.socket.process.SecureUtil;

/**
 * Teszt osztály kliens oldalra.
 * @author zoli
 */
public class TestClientProcess extends AbstractSecureClientProcess {

    public TestClientProcess(SSLSocket socket, int deviceId) {
        super(socket, deviceId);
    }

    @Override
    protected void process() {
        System.out.println("Device: " + getDeviceId());
        System.out.println("Connection: " + getConnectionId());
        System.out.println("Local name: " + getLocalCommonName());
        System.out.println("Remote name: " + getRemoteCommonName());
    }
    
    public static void main(String[] args) throws Exception {
        SSLSocket s = SecureUtil.createClientSocket("localhost", 8443, new File("test-certs/ca.crt"), new File("test-certs/controller.crt"), new File("test-certs/controller.key"), new char[]{});
        new Thread(new TestClientProcess(s, 20)).start();
    }
    
}
