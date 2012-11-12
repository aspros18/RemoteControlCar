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
        if (st == null) {
            if (isSwtTrayAvailable()) st = new SwtSystemTray();
            else st = new AwtSystemTray();
        }
        return st;
    }
    
    /**
     * Megadja, hogy használható-e az SWT rendszerikon.
     */
    private static boolean isSwtTrayAvailable() {
        try {
            Class.forName("org.eclipse.swt.widgets.Tray", false, SystemTrayProvider.class.getClassLoader());
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static void main(String[] args) {
        final SystemTray tray = getSystemTray();
        final TrayIcon icon = tray.addTrayIcon();
        icon.setImage(R.class.getResourceAsStream("icon.png"));
        icon.setToolTip("Tooltip");
        PopupMenu menu = icon.createPopupMenu();
        menu.addMenuItem("Test1", new Runnable() {

            @Override
            public void run() {
                System.out.println("Test1");
            }
            
        });
        menu.addSeparator();
        menu.addMenuItem("Test2", null);
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
