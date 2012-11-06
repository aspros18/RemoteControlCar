package org.dyndns.fzoli.rccar.bridge;

import static org.dyndns.fzoli.rccar.bridge.Main.VAL_CONN_LOG;
import static org.dyndns.fzoli.ui.SystemTrayIcon.showMessage;

/**
 * Sockettel kapcsolatos figyelmeztetésjelző osztály.
 * @author zoli
 */
public class ConnectionAlert {
    
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
     * Jelez a felhasználónak, kapcsolódást illetve lekapcsolódást, ha kérik.
     */
    public static void log(String text) {
        if (show) showMessage(VAL_CONN_LOG, text);
    }
    
}
