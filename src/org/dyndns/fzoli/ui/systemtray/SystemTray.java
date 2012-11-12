package org.dyndns.fzoli.ui.systemtray;

import org.dyndns.fzoli.rccar.controller.resource.R;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;

/**
 *
 * @author zoli
 */
public final class SystemTray {

    private static SystemTray st;
    
    private final Display display = new Display();
    private final Shell shell = new Shell(display);
    private final Tray tray = display.getSystemTray();
    
    private SystemTray() {
    }
    
    public boolean isSupported() {
        return tray != null;
    }
    
    public TrayIcon addTrayIcon() {
        if (!isSupported()) return null;
        return new TrayIcon(display, shell, tray);
    }
    
    public void dispose() {
        if (isSupported()) shell.dispose();
    }
    
    public static SystemTray getSystemTray() {
        if (st == null) st = new SystemTray();
        return st;
    }
    
    public void start() {
        if (isSupported()) {
            try {
                while (!shell.isDisposed()) {
                    if (!display.readAndDispatch()) {
                        display.sleep();
                    }
                }
                display.dispose();
            }
            catch (Exception ex) {
                ;
            }
        }
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
                icon.displayMessage("Title", "Message", TrayIcon.IconType.INFO, new Runnable() {

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
