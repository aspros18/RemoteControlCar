package org.dyndns.fzoli.ui.systemtray;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Az alapértelmezett AWT PopupMenu adaptere.
 * @author zoli
 */
class AwtPopupMenu implements PopupMenu {

    private final java.awt.PopupMenu menu;
    private final java.awt.TrayIcon icon;
    
    private boolean visible = true;
    
    public AwtPopupMenu(java.awt.TrayIcon icon, java.awt.PopupMenu menu) {
        this.menu = menu;
        this.icon = icon;
    }

    @Override
    public void setVisible(boolean b) {
        if (visible ^ b) {
            if (b) icon.setPopupMenu(menu);
            else icon.setPopupMenu(null);
        }
    }

    @Override
    public void addSeparator() {
        menu.addSeparator();
    }

    @Override
    public void addMenuItem(String text, final Runnable r) {
        java.awt.MenuItem item = new MenuItem(text);
        if (r != null) {
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    r.run();
                }

            });
        }
        menu.add(item);
    }
    
}
