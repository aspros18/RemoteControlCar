package org.dyndns.fzoli.ui.systemtray;

/**
 *
 * @author zoli
 */
public interface PopupMenu extends Visibility {
    
    public void addSeparator();
    
    public void addMenuItem(String text, final Runnable r);
    
}
