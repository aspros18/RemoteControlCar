package org.dyndns.fzoli.rccar.bridge;

import org.apache.log4j.Logger;
import static org.dyndns.fzoli.rccar.bridge.Main.VAL_CONN_LOG;
import static org.dyndns.fzoli.ui.systemtray.SystemTrayIcon.showMessage;

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
     * Logger ahhoz, hogy naplózni lehessen a kommunikációval kapcsolatos eseményeket.
     */
    private static final Logger logger = Logger.getLogger(ConnectionAlert.class);
    
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
     * Jelez a felhasználónak, kapcsolódást illetve lekapcsolódást, ha kérik,
     * valamint elvégzi a naplózást is.
     */
    public static void log(String text) {
        logger.info(text);
        if (show) showMessage(VAL_CONN_LOG, text);
    }
    
}
