package org.dyndns.fzoli.ui.systemtray;

/**
 *
 * @author zoli
 */
public interface SystemTray {
    
    public boolean isSupported();
    
    public TrayIcon addTrayIcon();
    
    public void dispose();
    
    public void start();
    
}
