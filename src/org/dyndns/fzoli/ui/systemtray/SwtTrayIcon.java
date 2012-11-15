package org.dyndns.fzoli.ui.systemtray;

import java.io.InputStream;
import org.dyndns.fzoli.ui.SwtDisplayProvider;
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
 * Az SWT TrayIcon adaptere.
 * @author zoli
 */
class SwtTrayIcon implements TrayIcon {
    
    private final TrayItem item;
    private final Shell shell;
    private final Display display;
    
    private final SystemTray st;
    
    private SwtPopupMenu menu;
    private Listener l;
    
    private boolean visible = true;
    
    public SwtTrayIcon(SystemTray st, Display display, Shell shell, final Tray tray) {
        this.st = st;
        this.shell = shell;
        this.display = display;
        this.item = SwtDisplayProvider.syncReturn(new SwtDisplayProvider.RunnableReturn<TrayItem>() {

            @Override
            protected TrayItem createReturn() {
                return new TrayItem(tray, SWT.NONE);
            }
            
        });
    }

    @Override
    public SystemTray getSystemTray() {
        return st;
    }
    
    @Override
    public PopupMenu createPopupMenu() {
        if (menu != null) menu.setVisible(false);
        menu = new SwtPopupMenu(display, shell, item);
        return menu;
    }
    
    @Override
    public void setOnClickListener(final Runnable r) {
        display.syncExec(new Runnable() {

            @Override
            public void run() {
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
            
        });
    }
    
    @Override
    public void displayMessage(String title, String msg, IconType icon) {
        displayMessage(title, msg, icon, null);
    }
    
    @Override
    public void displayMessage(final String title, final String msg, final IconType icon, final Runnable onClick) {
        display.syncExec(new Runnable() {

            @Override
            public void run() {
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
            
        });
    }
    
    @Override
    public void setImage(final InputStream in) {
        display.syncExec(new Runnable() {

            @Override
            public void run() {
                item.setImage(new Image(display, in));
            }
        });
    }
    
    @Override
    public void setToolTip(final String text) {
        display.syncExec(new Runnable() {

            @Override
            public void run() {
                item.setToolTipText(text);
            }
            
        });
    }
    
    @Override
    public void setVisible(final boolean b) {
        visible = b;
        display.syncExec(new Runnable() {

            @Override
            public void run() {
                item.setVisible(b);
            }
            
        });
    }

    @Override
    public boolean isVisible() {
        return visible;
    }
    
}
