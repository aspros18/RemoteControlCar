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
        TestServerProcess.test(this); // alap információ megjelenítése
        try {
            System.out.println("Read: " + getSocket().getInputStream().read()); // első olvasás belefér az időbe
            getSocket().getInputStream().read(); // második olvasás már időtúllépés
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        SSLSocket s = SecureUtil.createClientSocket("192.168.20.5", 8443, new File("test-certs/ca.crt"), new File("test-certs/controller.crt"), new File("test-certs/controller.key"), new char[]{});
        s.setSoTimeout(6000); // 6 mp időtúllépés az input stream olvasására
        new Thread(new TestClientProcess(s, 5)).start(); // az eszközazonosító: 5
    }
    
}
