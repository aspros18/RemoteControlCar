package org.dyndns.fzoli.ui.systemtray;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zoli
 */
class AwtSystemTrayAdapter implements SystemTrayAdapter {
    
    private SystemTray tray;

    private Map<TrayIconAdapter, TrayIcon> ICONS = new HashMap<TrayIconAdapter, TrayIcon>();
    
    public AwtSystemTrayAdapter() {
        if (SystemTray.isSupported()) tray = SystemTray.getSystemTray();
    }

    @Override
    public boolean isSupported() {
        return tray != null;
    }

    @Override
    public boolean add(TrayIconAdapter icon) {
        if (icon != null && isSupported()) {
            TrayIcon i = new TrayIcon(icon.getImage(), icon.getToolTip(), icon.getPopupMenu());
            ICONS.put(icon, i);
            try {
                tray.add(i);
                return true;
            }
            catch (AWTException ex) {
                ;
            }
        }
        return false;
    }

    @Override
    public void remove(TrayIconAdapter icon) {
        TrayIcon i = ICONS.get(icon);
        if (i != null) tray.remove(i);
    }
    
}
