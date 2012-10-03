package org.dyndns.fzoli.rccar.test;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.handler.SecureUtil;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Teszt osztály kliens oldalra.
 * @author zoli
 */
public class TestClientProcess extends AbstractSecureProcess {

    private static final int timeout = 1000;
    
    private int count = 0;
    
    public TestClientProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a másik oldallal.
     * Ez a kliens oldali teszt.
     */
    @Override
    public void run() {
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
        new Thread(new AbstractSecureClientHandler(s, 5) { // az eszközazonosító: 5

            @Override
            protected SecureProcess selectProcess() { // kliens oldali teszt feldolgozó használata
                return new TestClientProcess(this);
            }
            
        }).start(); // új szálban indítás
    }
    
}
