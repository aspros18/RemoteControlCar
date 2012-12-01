package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.bridge.ConnectionAlert;
import org.dyndns.fzoli.rccar.test.DisconnectProcessTester;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ServerDisconnectProcess;

/**
 * A híd oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
public class BridgeDisconnectProcess extends ServerDisconnectProcess implements ConnectionKeys {
    
    private final DisconnectProcessTester TESTER = new DisconnectProcessTester();
    
    public BridgeDisconnectProcess(SecureHandler handler) {
        super(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY); // 1 és 10 mp időtúllépés, 250 ms sleep
    }

    @Override
    protected void onTimeout(Exception ex) throws Exception {
        super.onTimeout(ex);
        TESTER.onTimeout();
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
     * Ha a kapcsolat létrejött, jelzi, ha kérik.
     */
    @Override
    protected void onConnect() {
        super.onConnect();
        log(true);
    }
    
    /**
     * Ha a kapcsolat végetért, jelzi, ha kérik.
     */
    @Override
    protected void onDisconnect(Exception ex) {
        log(false);
        super.onDisconnect(ex);
    }
    
    /**
     * Jelez a felhasználónak, kapcsolódást illetve lekapcsolódást, ha kérik.
     */
    private void log(boolean connect) {
        ConnectionAlert.log(getRemoteCommonName() + ' ' + (connect ? "kapcsolódott a hídhoz" : "lekapcsolódott a hídról"));
    }
    
}
