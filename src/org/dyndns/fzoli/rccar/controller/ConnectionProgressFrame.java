package org.dyndns.fzoli.rccar.controller;

import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.AbstractConnectionProgressFrame;

/**
 * A vezérlő kapcsolódásjelző- és kezelő ablaka.
 * @author zoli
 */
public class ConnectionProgressFrame extends AbstractConnectionProgressFrame {

    /**
     * Beállítja a kis autó ikont és az indikátor animációt.
     */
    public ConnectionProgressFrame() {
        super(R.getIndicatorImageIcon());
        setIconImage(R.getIconImage());
    }

    /**
     * Akkor hívódik meg, amikor az Újra gombot kiválasztják.
     */
    @Override
    protected void onAgain() {
        setProgress(true); //teszt
    }

    /**
     * Akkor hívódik meg, amikor az Beállítások gombot kiválasztják.
     */
    @Override
    protected void onSettings() {
        setProgress(false); //teszt
        Main.showSettingDialog(false, null);
    }
    
}
