package org.dyndns.fzoli.ui.systemtray;

import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;

/**
 *
 * @author zoli
 */
public final class SystemTrayProvider {

    private static SystemTray st;
    
    private SystemTrayProvider() {
    }
    
    public static SystemTray getSystemTray() {
        if (st == null) st = new SwtSystemTray();
        return st;
    }
    
    public static void main(String[] args) {
        final SystemTray tray = getSystemTray();
        final TrayIcon icon = tray.addTrayIcon();
        icon.setImage(R.class.getResourceAsStream("icon.png"));
        icon.setToolTip("SWT tooltip");
        PopupMenu menu = icon.createPopupMenu();
        menu.addMenuItem("Nothing1", null);
        menu.addSeparator();
        menu.addMenuItem("Nothing2", null);
        icon.setOnClickListener(new Runnable() {

            @Override
            public void run() {
                icon.displayMessage("Title", "Message", IconType.INFO, new Runnable() {

                    @Override
                    public void run() {
                        tray.dispose();
                    }

                });
            }
            
        });
        tray.start();
    }
    
}
