package org.dyndns.fzoli.rccar.test;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.process.AbstractSecureClientProcess;
import org.dyndns.fzoli.socket.handler.SecureUtil;

/**
 * Teszt osztály kliens oldalra.
 * @author zoli
 */
public class TestClientProcess extends AbstractSecureClientProcess {

    private static final int timeout = 1000;
    
    private int count = 0;
    
    public TestClientProcess(SSLSocket socket, int deviceId) {
        super(socket, deviceId);
    }

    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a másik oldallal.
     * Ez a kliens oldali teszt.
     */
    @Override
    protected void process() {
        TestServerProcess.test(this); // alap információ megjelenítése
        try {
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            getSocket().setSoTimeout(timeout);
            while(!getSocket().isClosed() && getSocket().isConnected()) {
                in.read();
                System.out.println("Ok " + ++count);
                out.write(1);
                Thread.sleep(1);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        SSLSocket s = SecureUtil.createClientSocket("192.168.20.5", 8443, new File("test-certs/ca.crt"), new File("test-certs/controller.crt"), new File("test-certs/controller.key"), new char[]{});
        new Thread(new TestClientProcess(s, 5)).start(); // az eszközazonosító: 5
    }
    
}
