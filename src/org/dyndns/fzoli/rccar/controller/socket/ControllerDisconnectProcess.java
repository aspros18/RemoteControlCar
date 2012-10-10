package org.dyndns.fzoli.rccar.controller.socket;

import org.dyndns.fzoli.rccar.controller.ConnectionProgressFrame.Status;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.rccar.test.DisconnectProcessTester;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * A vezérlő oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
public class ControllerDisconnectProcess extends ClientDisconnectProcess {
    
    private final DisconnectProcessTester TESTER = new DisconnectProcessTester();
    
    public ControllerDisconnectProcess(SecureHandler handler) {
        super(handler, 1000, 10000, 250); // 1 és 10 mp időtúllépés, 250 ms sleep
    }

    @Override
    public void onTimeout(Exception ex) throws Exception {
        super.onTimeout(ex);
        TESTER.onTimeout();
    }

    @Override
    public void beforeAnswer() throws Exception {
        super.beforeAnswer();
        TESTER.beforeAnswer();
    }
    
    @Override
    public void afterAnswer() throws Exception {
        super.afterAnswer();
        TESTER.afterAnswer();
    }

    /**
     * Ha a kapcsolt megszakadt.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik
     * és megjeleníti a kapcsolódás hiba ablakot.
     */
    @Override
    public void onDisconnect(Exception ex) {
        super.onDisconnect(ex);
        showConnectionStatus(Status.DISCONNECTED);
    }
    
}
