package org.dyndns.fzoli.ui.systemtray;

import java.io.InputStream;

/**
 *
 * @author zoli
 */
class AwtTrayIcon implements TrayIcon {

    @Override
    public PopupMenu createPopupMenu() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setOnClickListener(Runnable r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void displayMessage(String title, String msg, IconType icon) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void displayMessage(String title, String msg, IconType icon, Runnable onClick) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setImage(InputStream in) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setToolTip(String text) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
