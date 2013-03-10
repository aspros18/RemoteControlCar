package org.dyndns.fzoli.ui.systemtray;

import chrriis.dj.nativeswing.swtimpl.components.JTray;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;
import java.awt.image.BufferedImage;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;

/**
 * Az SWT TrayIcon adaptere.
 * @author zoli
 */
class SwtTrayIcon implements TrayIcon {
    
    private final SystemTray TRAY;
    
    private final JTrayItem ITEM = JTray.createTrayItem();
    
    private final TrayItemMouseListener LISTENER = new TrayItemMouseListener() {

        @Override
        public void onClick(TrayItemMouseEvent e) {
            if (callback != null) callback.run();
        }
        
    };
    
    private Runnable callback;
    
    private SwtPopupMenu menu = new SwtPopupMenu(ITEM);
    
    public SwtTrayIcon(SystemTray tray) {
        this.TRAY = tray;
        ITEM.addMouseListener(LISTENER);
    }

    @Override
    public SystemTray getSystemTray() {
        return TRAY;
    }
    
    @Override
    public PopupMenu createPopupMenu() {
        removePopupMenu();
        menu = new SwtPopupMenu(ITEM);
        return menu;
    }

    @Override
    public void removePopupMenu() {
        if (menu != null) {
            menu.dispose();
            menu = null;
        }
    }
    
    @Override
    public void setOnClickListener(final Runnable callback) {
        this.callback = callback;
    }
    
    @Override
    public void displayMessage(String title, String msg, IconType icon) {
        displayMessage(title, msg, icon, null);
    }
    
    @Override
    public void displayMessage(final String title, final String msg, final IconType icon, final Runnable onClick) {
        TrayMessageType type = TrayMessageType.INFO;
        if (icon != null) switch (icon) {
            case ERROR:
                type = TrayMessageType.ERROR;
                break;
            case WARNING:
                type = TrayMessageType.WARNING;
        }
        ITEM.showMessage(title, msg, type, onClick);
    }
    
    @Override
    public void setImage(BufferedImage img) {
        ITEM.setImage(img);
    }
    
    @Override
    public void setToolTip(final String text) {
        ITEM.setTooltip(text);
    }
    
    @Override
    public void setVisible(final boolean b) {
        ITEM.setVisible(b);
    }

    @Override
    public boolean isVisible() {
        return ITEM.isVisible();
    }
    
}
