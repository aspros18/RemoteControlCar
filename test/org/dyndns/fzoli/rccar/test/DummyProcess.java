package org.dyndns.fzoli.rccar.test;

import org.dyndns.fzoli.rccar.SplashScreenLoader;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * Azt tesztelem, hogy mi történik, ha olvasás közben bezárom a socketet másik szálból.
 * Eredmény: java.net.SocketException: Socket closed
 * @author zoli
 */
public class DummyProcess extends AbstractSecureProcess {
    
    public DummyProcess(SecureHandler handler) {
        super(handler);
    }
    
    @Override
    public void run() {
        try {
            SplashScreenLoader.closeSplashScreen();
            System.out.println("Device id: " + getDeviceId());
            System.out.println("Connection id: " + getConnectionId());
            System.out.println("Dummy " + getSocket().getInputStream().read());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
