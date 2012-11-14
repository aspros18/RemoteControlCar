package org.dyndns.fzoli.rccar.test;

import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.systemtray.PopupMenu;
import org.dyndns.fzoli.ui.systemtray.SystemTray;
import org.dyndns.fzoli.ui.systemtray.SystemTrayProvider;
import org.dyndns.fzoli.ui.systemtray.TrayIcon;

/**
 * Rendszerikon tesztelése.
 * @author zoli
 */
public class SystemTrayTest {
    
    /**
     * Teszt.
     */
    public static void main(String[] args) {
        final SystemTray tray = SystemTrayProvider.getSystemTray();
        final TrayIcon icon = tray.addTrayIcon();
        icon.setImage(R.class.getResourceAsStream("icon.png"));
        icon.setToolTip("Tooltip");
        final PopupMenu menu = icon.createPopupMenu();
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
                System.out.println("display message");
                icon.displayMessage("Title", "Message", TrayIcon.IconType.INFO, new Runnable() {

                    @Override
                    public void run() {
                        System.out.println("dispose");
                        tray.dispose();
                    }

                });
            }
            
        });
    }
    
}
