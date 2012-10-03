package org.dyndns.fzoli.rccar.test;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.socket.process.AbstractSecureServerProcess;
import org.dyndns.fzoli.socket.process.SecureProcess;
import org.dyndns.fzoli.socket.process.SecureProcessException;
import org.dyndns.fzoli.socket.handler.SecureUtil;

/**
 * Teszt osztály szerver oldalra.
 * @author zoli
 */
public class TestServerProcess extends AbstractSecureServerProcess {

    private int count = 0;
    
    private static final int timeout = 1000, delay = 200;
    
    public TestServerProcess(SSLSocket socket, int connectionId) {
        super(socket, connectionId);
    }

    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a másik oldallal.
     * Ez a szerver oldali teszt.
     */
    @Override
    protected void process() {
        test(this); // alap információ megjelenítése
        try {
            InputStream in = getSocket().getInputStream();
            OutputStream out = getSocket().getOutputStream();
            getSocket().setSoTimeout(timeout);
            while(!getSocket().isClosed() && getSocket().isConnected()) {
                out.write(1);
                in.read();
                System.out.println("Ok " + ++count);
                Thread.sleep(timeout - delay);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * Alap információkat ír ki a konzolra.
     */
    public static void test(SecureProcess proc) {
        System.out.println("Device ID: " + proc.getDeviceId());
        System.out.println("Connection ID: " + proc.getConnectionId());
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
