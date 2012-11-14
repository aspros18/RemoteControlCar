package org.dyndns.fzoli.ui.systemtray;

import org.dyndns.fzoli.ui.SwtDisplayProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
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
    
    private final Display display;
    
    private final Listener l;
    
    private boolean visible = false;
    
    public SwtPopupMenu(Display display, final Shell shell, TrayItem item) {
        this.item = item;
        this.display = display;
        this.menu = SwtDisplayProvider.syncReturn(new SwtDisplayProvider.RunnableReturn<Menu>() {

            @Override
            protected Menu createReturn() {
                return new Menu(shell, SWT.POP_UP);
            }
            
        });
        this.l = new Listener() {

            @Override
            public void handleEvent(Event event) {
                menu.setVisible(true);
            }

        };
        setVisible(true);
    }
    
    @Override
    public void setVisible(final boolean b) {
        if (visible ^ b) {
            visible = b;
            display.syncExec(new Runnable() {

                @Override
                public void run() {
                    if (!b) {
                        item.removeListener(SWT.MenuDetect, l);
                        menu.setVisible(false);
                    }
                    else {
                        item.addListener(SWT.MenuDetect, l);
                    }
                }
                
            });
        }
    }
    
    @Override
    public void addSeparator() {
        display.syncExec(new Runnable() {

            @Override
            public void run() {
                new MenuItem(menu, SWT.SEPARATOR);
            }
            
        });
    }
    
    private void addMenuItem(final String text, final Runnable r, final int type, final boolean checked) {
        display.syncExec(new Runnable() {

            @Override
            public void run() {
                final MenuItem mi = new MenuItem(menu, type);
                mi.setText(text);
                mi.setSelection(checked);
                if (r != null) {
                    mi.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            r.run();
                        }

                    });
                }
            }
            
        });
    }
    
    @Override
    public void addMenuItem(String text, final Runnable r) {
        addMenuItem(text, r, SWT.PUSH, false);
    }

    @Override
    public void addCheckboxMenuItem(String text, boolean checked, Runnable r) {
        addMenuItem(text, r, SWT.CHECK, checked);
    }
    
}
