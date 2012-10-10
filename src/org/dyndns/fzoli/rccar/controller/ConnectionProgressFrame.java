package org.dyndns.fzoli.rccar.controller;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.socket.ConnectionHelper;
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
     * A kapcsolódást segító objektum.
     * Kell rá a referencia, hogy kérésre lehessen kapcsolódást indítani.
     */
    private final ConnectionHelper CONN;
    
    /**
     * Az ablakon ezek a panelek jelenhetnek meg.
     */
    private static final IconTextPanel[] PANELS = {
        new ConnProgPanel(R.getIndicatorIcon(), "Kapcsolódás folyamatban..."),
        new ConnProgPanel(R.getErrorIcon(), "Nem sikerült kapcsolódni a szerverhez!"),
        new ConnProgPanel(R.getWarningIcon(), "Megszakadt a kapcsolat a szerverrel!"),
        new ConnProgPanel(R.getWarningIcon(), "A szerver elutasította a kérést!")
    };
    
    /**
     * Beállítja a kis autó ikont és az indikátor animációt.
     */
    public ConnectionProgressFrame(ConnectionHelper conn) {
        super(PANELS);
        setIconImage(R.getIconImage());
        CONN = conn;
    }

    /**
     * Akkor hívódik meg, amikor az Újra gombot kiválasztják.
     */
    @Override
    protected void onAgain() {
        setProgress(true);
        CONN.connect();
    }

    /**
     * Akkor hívódik meg, amikor az Beállítások gombot kiválasztják.
     */
    @Override
    protected void onSettings() {
        Main.showSettingDialog(false, null);
    }
    
    /**
     * Beállítja a megjelenő panelt és az Újra gombot.
     * Folyamatjelzés közben az Újra gomb használata nem engedélyezett.
     * @param b true esetén a folyamatjelző, false esetén a hibaüzenet jelenik meg
     */
    public void setProgress(boolean b) {
        setAgainButtonEnabled(!b);
        setIconTextPanel(b ? 0 : 1);
    }
    
    /**
     * Beállítja a megjelenő panelt és az Újra gombot engedélyezi.
     * @param b true esetén a kapcsolat elveszett szöveg, false esetén a hibaüzenet jelenik meg
     */
    public void setDisconnect(boolean b) {
        setAgainButtonEnabled(true);
        setIconTextPanel(b ? 2 : 1);
    }
    
    /**
     * Beállítja a megjelenő panelt és az Újra gombot engedélyezi.
     * @param b true esetén a kapcsolat elutasítva szöveg, false esetén a hibaüzenet jelenik meg
     */
    public void setRefused(boolean b) {
        setAgainButtonEnabled(true);
        setIconTextPanel(b ? 3 : 1);
    }
    
}
