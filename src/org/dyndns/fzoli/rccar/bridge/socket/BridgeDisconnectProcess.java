package org.dyndns.fzoli.rccar.bridge.socket;

import static org.dyndns.fzoli.rccar.bridge.Main.VAL_CONN_LOG;
import org.dyndns.fzoli.rccar.test.DisconnectProcessTester;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ServerDisconnectProcess;
import static org.dyndns.fzoli.ui.SystemTrayIcon.showMessage;

/**
 * A híd oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * @author zoli
 */
public class BridgeDisconnectProcess extends ServerDisconnectProcess {

    /**
     * Alapértelmezetten a kapcsolódás és lekapcsolódás nincs jelezve.
     */
    private static boolean show = false;
    
    private final DisconnectProcessTester TESTER = new DisconnectProcessTester();
    
    public BridgeDisconnectProcess(SecureHandler handler) {
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
     * Ha a kapcsolat létrejött, jelzi, ha kérik.
     */
    @Override
    public void onConnect() {
        super.onConnect();
        log(true);
    }
    
    /**
     * Ha a kapcsolat végetért, jelzi, ha kérik.
     */
    @Override
    public void onDisconnect(Exception ex) {
        log(false);
        super.onDisconnect(ex);
    }
    
    /**
     * Jelez a felhasználónak, kapcsolódást illetve lekapcsolódást, ha kérik.
     */
    private void log(boolean connect) {
        if (show) showMessage(VAL_CONN_LOG, getRemoteCommonName() + ' ' + (connect ? "kapcsolódott a hídhoz" : "lekapcsolódott a hídról"));
    }
    
    /**
     * Megadja, hogy be van-e kapcsolva az üzenetjelzés.
     */
    public static boolean isLogEnabled() {
        return show;
    }
    
    /**
     * Bekapcsolja vagy kikapcsolja az üzenetjelzést.
     */
    public static void setLogEnabled(boolean enabled) {
        show = enabled;
    }
    
}
