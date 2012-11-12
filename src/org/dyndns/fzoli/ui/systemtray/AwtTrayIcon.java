package org.dyndns.fzoli.ui.systemtray;

import java.awt.AWTException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author zoli
 */
class AwtTrayIcon implements TrayIcon {

    private final java.awt.TrayIcon icon;
    
    private final java.awt.SystemTray tray;
    
    private boolean visible = true;
    
    public AwtTrayIcon(java.awt.SystemTray tray, java.awt.TrayIcon icon) {
        this.icon = icon;
        this.tray = tray;
        icon.setImageAutoSize(true);
    }

    @Override
    public PopupMenu createPopupMenu() {
        java.awt.PopupMenu menu = new java.awt.PopupMenu();
        icon.setPopupMenu(menu);
        return new AwtPopupMenu(menu);
    }

    @Override
    public void setOnClickListener(final Runnable r) {
        if (r != null) {
            icon.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    r.run();
                }

            });
        }
    }

    @Override
    public void displayMessage(String title, String msg, IconType ic) {
        displayMessage(title, msg, ic, null);
    }

    @Override
    public void displayMessage(String title, String msg, IconType ic, Runnable onClick) {
        java.awt.TrayIcon.MessageType type = null;
        switch (ic) {
            case INFO:
                type = java.awt.TrayIcon.MessageType.INFO;
                break;
            case WARNING:
                type = java.awt.TrayIcon.MessageType.WARNING;
                break;
            case ERROR:
                type = java.awt.TrayIcon.MessageType.ERROR;
        }
        icon.displayMessage(title, msg, type);
    }

    @Override
    public void setImage(InputStream in) {
        try {
            icon.setImage(ImageIO.read(in));
        }
        catch (IOException ex) {
            ;
        }
    }

    @Override
    public void setToolTip(String text) {
        icon.setToolTip(text);
    }

    @Override
    public void setVisible(boolean b) {
        if (visible ^ b) {
            try {
                if (b) tray.add(icon);
                else tray.remove(icon);
                visible = !visible;
            }
            catch (AWTException ex) {
                ;
            }
        }
    }
    
}
