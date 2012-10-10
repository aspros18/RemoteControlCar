package org.dyndns.fzoli.rccar.controller;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import static org.dyndns.fzoli.rccar.controller.Main.runClient;
import static org.dyndns.fzoli.rccar.controller.Main.showSettingDialog;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.AbstractConnectionProgressFrame;
import org.dyndns.fzoli.ui.IconTextPanel;

/**
 * A vezérlő kapcsolódásjelző- és kezelő ablaka.
 * @author zoli
 */
public class ConnectionProgressFrame extends AbstractConnectionProgressFrame {

    /**
     * Az ablakon megjelenő panelek belőle származnak.
     */
    private static class ConnProgPanel extends IconTextPanel {

        public ConnProgPanel(Icon icon, String text) {
            super(icon, text);
            setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // alsó és felső margó 5 pixel
        }
        
    }
    
    /**
     * A kapcsolatok állapotai.
     */
    public static enum Status {
        CONNECTING,
        CONNECTION_ERROR,
        DISCONNECTED,
        REFUSED,
        UNKNOWN_HOST,
        CONNECTION_TIMEOUT
    };
    
    /**
     * Az ablakon ezek a panelek jelenhetnek meg.
     */
    private static final IconTextPanel[] PANELS = {
        new ConnProgPanel(R.getIndicatorIcon(), "Kapcsolódás folyamatban..."),
        new ConnProgPanel(R.getErrorIcon(), "Nem sikerült kapcsolódni a szerverhez!"),
        new ConnProgPanel(R.getWarningIcon(), "Megszakadt a kapcsolat a szerverrel!"),
        new ConnProgPanel(R.getWarningIcon(), "A szerver elutasította a kérést!"),
        new ConnProgPanel(R.getErrorIcon(), "A szerver hosztneve nem érhető el!"),
        new ConnProgPanel(R.getErrorIcon(), "Időtúllépés a kapcsolódás közben!")
    };
    
    /**
     * Beállítja a kis autó ikont és az indikátor animációt.
     */
    public ConnectionProgressFrame() {
        super(PANELS);
        setIconImage(R.getIconImage());
    }

    /**
     * Beállítja a megjelenő panelt és az Újra gombot.
     * Az Újra gomb tiltva lesz {@code Status.CONNECTING} státusz esetén.
     * Ha nincs megadva státusz, az ablak elrejtődik.
     * @param status a kapcsolat egyik állapota
     */
    public void setStatus(Status status) {
        if (status != null) {
            setAgainButtonEnabled(status != Status.CONNECTING);
            setIconTextPanel(status.ordinal());
        }
        setVisible(status != null);
    }
    
    /**
     * Akkor hívódik meg, amikor az Újra gombot kiválasztják.
     */
    @Override
    protected void onAgain() {
        runClient(true);
    }

    /**
     * Akkor hívódik meg, amikor az Beállítások gombot kiválasztják.
     */
    @Override
    protected void onSettings() {
        showSettingDialog(false, null);
    }
    
}
