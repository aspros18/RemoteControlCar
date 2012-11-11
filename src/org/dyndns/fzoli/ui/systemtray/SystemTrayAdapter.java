package org.dyndns.fzoli.ui.systemtray;

/**
 *
 * @author zoli
 */
public interface SystemTrayAdapter {
    
    public boolean isSupported();
    
    public boolean add(TrayIconAdapter icon);
    
    public void remove(TrayIconAdapter icon);
    
}
