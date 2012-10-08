package org.dyndns.fzoli.rccar.controller;

import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.AbstractConnectionProgressFrame;

/**
 * A vezérlő kapcsolódásjelző- és kezelő ablaka.
 * @author zoli
 */
public class ConnectionProgressFrame extends AbstractConnectionProgressFrame {

    public ConnectionProgressFrame() {
        super(R.getIndicatorImageIcon());
        setIconImage(R.getIconImage());
    }

    @Override
    protected void onAgain() {
        setProgress(true); //teszt
    }

    @Override
    protected void onSettings() {
        setProgress(false); //teszt
    }
    
}
