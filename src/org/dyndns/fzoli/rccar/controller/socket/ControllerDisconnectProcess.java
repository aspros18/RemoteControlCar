package org.dyndns.fzoli.rccar.controller.socket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.controller.ControllerModels;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame.Status;
import org.dyndns.fzoli.rccar.test.DisconnectProcessTester;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

/**
 * A vezérlő oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
public class ControllerDisconnectProcess extends ClientDisconnectProcess implements ConnectionKeys {
    
    private final DisconnectProcessTester TESTER = new DisconnectProcessTester();
    
    public ControllerDisconnectProcess(SecureHandler handler) {
        super(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY); // 1 és 10 mp időtúllépés, 250 ms sleep
    }

    private void setTimeout(boolean b) {
        ControllerModels.getData().setUnderTimeout(b);
    }

    @Override
    protected void onConnect() {
        setTimeout(false);
        super.onConnect();
    }
    
    @Override
    protected void onTimeout(Exception ex) throws Exception {
        setTimeout(true);
        super.onTimeout(ex);
        TESTER.onTimeout();
    }

    @Override
    protected void afterTimeout() throws Exception {
        setTimeout(false);
        super.afterTimeout();
    }

    @Override
    protected void beforeAnswer() throws Exception {
        super.beforeAnswer();
        TESTER.beforeAnswer();
    }
    
    @Override
    protected void afterAnswer() throws Exception {
        super.afterAnswer();
        TESTER.afterAnswer();
    }

    /**
     * Ha a kapcsolt megszakadt.
     * Az összes aktív kapcsolatfeldolgozót leállítja, mely ugyan ahhoz az eszközhöz tartozik
     * és megjeleníti a kapcsolódás hiba ablakot.
     */
    @Override
    protected void onDisconnect(Exception ex) {
        super.onDisconnect(ex);
        showConnectionStatus(Status.DISCONNECTED);
    }
    
}
