package org.dyndns.fzoli.ui.systemtray;

/**
 *
 * @author zoli
 */
class AwtPopupMenu implements PopupMenu {

    final java.awt.PopupMenu menu;
    
    public AwtPopupMenu(java.awt.PopupMenu menu) {
        this.menu = menu;
    }

    @Override
    public void setVisible(boolean b) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addSeparator() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void addMenuItem(String text, Runnable r) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
