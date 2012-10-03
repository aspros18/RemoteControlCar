package org.dyndns.fzoli.rccar.bridge.socket;

import static org.dyndns.fzoli.rccar.SystemTrayIcon.showMessage;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ServerDisconnectProcess;

/**
 * A híd oldalán vizsgálja, hogy él-e még a kapcsolat a klienssel.
 * TODO: egyelőre teszt
 * @author zoli
 */
public class BridgeDisconnectProcess extends ServerDisconnectProcess {

    /**
     * Alapértelmezetten a kapcsolódás és lekapcsolódás nincs jelezve.
     */
    private static boolean show = false;
    
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
    
    /**
     * Megadja a menüelem aktuális opcióját.
     */
    public static String getLogOption() {
        return "Kapcsolatjelzés " + (isLogEnabled() ? "ki" : "be");
    }
    
    public BridgeDisconnectProcess(SecureHandler handler) {
        super(handler);
    }
    
    /**
     * Ha a kapcsolat létrejött, jelzi, ha kérik.
     */
    @Override
    protected void onConnect() {
        log(true);
    }
    
    /**
     * Ha a kapcsolat végetért, jelzi, ha kérik.
     */
    @Override
    protected void onDisconnect() {
        log(false);
    }
    
    /**
     * Jelez a felhasználónak, kapcsolódást illetve lekapcsolódást, ha kérik.
     */
    private void log(boolean connect) {
        if (show) showMessage("Kapcsolat", getRemoteCommonName() + ' ' + (connect ? "kapcsolódott a hídhoz" : "lekapcsolódott a hídról"));
    }
    
}
