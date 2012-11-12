package org.dyndns.fzoli.ui.systemtray;

/**
 *
 * @author zoli
 */
public interface PopupMenu {
    
    public void setVisible(boolean b);
    
    public void addSeparator();
    
    public void addMenuItem(String text, final Runnable r);
    
}
