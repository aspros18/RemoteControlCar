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
        super(handler, 10000, 250); // 10 mp időtúllépés, 250 ms sleep
    }

    @Override
    public void beforeAnswer() throws Exception {
        TESTER.beforeAnswer();
    }
    
    @Override
    public void afterAnswer() throws Exception {
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
