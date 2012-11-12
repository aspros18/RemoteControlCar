package org.dyndns.fzoli.ui.systemtray;

import java.io.InputStream;

/**
 *
 * @author zoli
 */
public interface TrayIcon {
    
    public enum IconType {
        INFO, WARNING, ERROR
    }
    
    public PopupMenu createPopupMenu();
    
    public void setOnClickListener(final Runnable r);
    
    public void displayMessage(String title, String msg, IconType icon);
    
    public void displayMessage(String title, String msg, IconType icon, final Runnable onClick);
    
    public void setImage(InputStream in);
    
    public void setToolTip(String text);
    
    public void setVisible(boolean b);
    
}
