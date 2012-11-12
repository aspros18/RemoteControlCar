package org.dyndns.fzoli.ui.systemtray;

import java.io.InputStream;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;
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
class SwtTrayIcon implements TrayIcon {
    
    private final TrayItem item;
    private final Shell shell;
    private final Display display;
    private final Tray tray;
    
    private SwtPopupMenu menu;
    private Listener l;
    
    public SwtTrayIcon(Display display, Shell shell, Tray tray) {
        this.shell = shell;
        this.tray = tray;
        this.display = display;
        this.item = new TrayItem(tray, SWT.NONE);
    }
    
    @Override
    public PopupMenu createPopupMenu() {
        if (menu != null) menu.setVisible(false);
        menu = new SwtPopupMenu(shell, item);
        return menu;
    }
    
    @Override
    public void setOnClickListener(final Runnable r) {
        if (l != null) {
            item.removeListener(SWT.DefaultSelection, l);
        }
        if (r != null) {
            l = new Listener() {

                @Override
                public void handleEvent(Event event) {
                    r.run();
                }

            };
            item.addListener(SWT.DefaultSelection, l);
        }
    }
    
    @Override
    public void displayMessage(String title, String msg, IconType icon) {
        displayMessage(title, msg, icon, null);
    }
    
    @Override
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
    
    @Override
    public void setImage(InputStream in) {
        item.setImage(new Image(display, in));
    }
    
    @Override
    public void setToolTip(String text) {
        item.setToolTipText(text);
    }
    
    @Override
    public void setVisible(boolean b) {
        item.setVisible(b);
    }
    
}
