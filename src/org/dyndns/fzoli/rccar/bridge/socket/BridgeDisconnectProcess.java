package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.bridge.ConnectionAlert;
import static org.dyndns.fzoli.rccar.bridge.Main.getString;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ServerDisconnectProcess;

/**
 * A híd oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
abstract class BridgeDisconnectProcess extends ServerDisconnectProcess implements ConnectionKeys {
    
    public BridgeDisconnectProcess(SecureHandler handler) {
        super(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY); // 1 és 10 mp időtúllépés, 250 ms sleep
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
        char c = getDeviceId() == KEY_DEV_CONTROLLER ? 'a' : 'b';
        String s1 = getString("log_conn1" + c);
        String s2 = getString("log_conn2" + c);
        boolean e1 = s1.trim().isEmpty();
        boolean e2 = s2.trim().isEmpty();
        ConnectionAlert.log(s1 + (e1 ? "" : " ") + getRemoteCommonName() + (e2 ? "" : " ") + s2 + ' ' + getString(connect ? "log_conn3" : "log_conn4"));
    }
    
}
