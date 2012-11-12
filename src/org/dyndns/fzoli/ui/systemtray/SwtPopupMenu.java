package org.dyndns.fzoli.ui.systemtray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

/**
 * Az SWT PopupMenu adaptere.
 * @author zoli
 */
class SwtPopupMenu implements PopupMenu {

    private final Menu menu;
    
    private final TrayItem item;
    
    private final Listener l;
    
    private boolean visible = false;
    
    public SwtPopupMenu(Shell shell, TrayItem item) {
        this.item = item;
        this.menu = new Menu(shell, SWT.POP_UP);
        this.l = new Listener() {

            @Override
            public void handleEvent(Event event) {
                menu.setVisible(true);
            }

        };
        setVisible(true);
    }
    
    @Override
    public void setVisible(boolean b) {
        if (visible ^ b) {
            visible = b;
            if (!b) {
                item.removeListener(SWT.MenuDetect, l);
                menu.setVisible(false);
            }
            else {
                item.addListener(SWT.MenuDetect, l);
            }
        }
    }
    
    @Override
    public void addSeparator() {
        new MenuItem(menu, SWT.SEPARATOR);
    }
    
    @Override
    public void addMenuItem(String text, final Runnable r) {
        final MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(text);
        if (r != null) {
            mi.addListener(SWT.Selection, new Listener() {

                @Override
                public void handleEvent(Event event) {
                    r.run();
                }

            });
        }
    }
    
}
