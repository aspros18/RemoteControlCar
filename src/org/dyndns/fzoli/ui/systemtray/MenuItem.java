package org.dyndns.fzoli.ui.systemtray;

/**
 * Menüelem.
 * @author zoli
 */
public interface MenuItem {
    
    /**
     * Beállítja a menüelemet aktívra vagy inaktívra.
     * @param enabled true esetén aktív, egyébként inaktív
     */
    public void setEnabled(boolean enabled);
    
}
