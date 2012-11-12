package org.dyndns.fzoli.ui.systemtray;

import java.io.InputStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 *
 * @author zoli
 */
public class TrayIcon {

    public enum IconType {
        INFO, WARNING, ERROR
    }
    
    private final TrayItem item;
    private final Shell shell;
    private final Display display;
    private final Tray tray;
    
    private Listener l, l2;
    
    TrayIcon(Display display, Shell shell, Tray tray) {
        this.shell = shell;
        this.tray = tray;
        this.display = display;
        this.item = new TrayItem(tray, SWT.NONE);
    }
    
    public PopupMenu createPopupMenu() {
        if (l != null) item.removeListener(SWT.MenuDetect, l);
        final PopupMenu menu = new PopupMenu(shell);
        item.addListener(SWT.MenuDetect, new Listener() {
            
            @Override
            public void handleEvent(Event event) {
                menu.setVisible(true);
            }
            
        });
        return menu;
    }
    
    public void setOnClickListener(final Runnable r) {
        if (l2 != null) {
            item.removeListener(SWT.DefaultSelection, l2);
        }
        if (r != null) {
            l2 = new Listener() {

                @Override
                public void handleEvent(Event event) {
                    r.run();
                }

            };
            item.addListener(SWT.DefaultSelection, l2);
        }
    }
    
    public void displayMessage(String title, String msg, IconType icon) {
        displayMessage(title, msg, icon, null);
    }
    
    public void displayMessage(String title, String msg, IconType icon, final Runnable onClick) {
        int i = SWT.ICON_INFORMATION;
        switch (icon) {
            case WARNING:
                i = SWT.ICON_WARNING;
                break;
            case ERROR:
                i = SWT.ICON_ERROR;
                break;
        }
        final ToolTip tip = new ToolTip(shell, SWT.BALLOON | i);
        tip.setText(title);
        tip.setMessage(msg);
        if (onClick != null) {
            tip.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent se) {
                    onClick.run();
                }

            });
        }
        item.setToolTip(tip);
        tip.setVisible(true);
    }
    
    public void setImage(InputStream in) {
        item.setImage(new Image(display, in));
    }
    
    public void setToolTip(String text) {
        item.setToolTipText(text);
    }
    
    public void setVisible(boolean b) {
        item.setVisible(b);
    }
    
}
