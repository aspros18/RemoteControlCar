package org.dyndns.fzoli.ui.systemtray;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

/**
 *
 * @author zoli
 */
public class PopupMenu {

    private final Menu menu;
    
    PopupMenu(Shell shell) {
        menu = new Menu(shell, SWT.POP_UP);
    }
    
    public void setVisible(boolean b) {
        menu.setVisible(b);
    }
    
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
