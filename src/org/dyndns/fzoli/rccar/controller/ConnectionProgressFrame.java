package org.dyndns.fzoli.rccar.controller;

import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.socket.ConnectionHelper;
import org.dyndns.fzoli.ui.AbstractConnectionProgressFrame;

/**
 * A vezérlő kapcsolódásjelző- és kezelő ablaka.
 * @author zoli
 */
public class ConnectionProgressFrame extends AbstractConnectionProgressFrame {

    private final ConnectionHelper CONN;
    
    /**
     * Beállítja a kis autó ikont és az indikátor animációt.
     */
    public ConnectionProgressFrame(ConnectionHelper conn) {
        super(R.getIndicatorImageIcon());
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
    
}
