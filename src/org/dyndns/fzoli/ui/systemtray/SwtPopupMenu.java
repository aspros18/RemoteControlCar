package org.dyndns.fzoli.ui.systemtray;

import chrriis.dj.nativeswing.swtimpl.components.JMenuItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuSelectionItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayMenu;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemActionListener;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemSelectionListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayActionEvent;

/**
 * Az SWT PopupMenu adaptere.
 * @author zoli
 */
class SwtPopupMenu implements PopupMenu {
    
    private final JTrayMenu MENU;
    
    public SwtPopupMenu(JTrayItem item) {
        this.MENU = new JTrayMenu(item);
    }
    
    @Override
    public void dispose() {
        MENU.dispose();
    }
    
    @Override
    public void setVisible(final boolean b) {
        MENU.setActive(b);
    }

    @Override
    public boolean isVisible() {
        return MENU.isActive();
    }
    
    @Override
    public void addSeparator() {
        MENU.addMenuSeparator();
    }
    
    @Override
    public void addMenuItem(String text, final Runnable r) {
        JMenuItem mi = MENU.addMenuItem(text);
        if (r == null) return;
        mi.addActionListener(new MenuItemActionListener() {

            @Override
            public void onAction(TrayActionEvent<JMenuItem> e) {
                r.run();
            }
            
        });
    }

    @Override
    public void addCheckboxMenuItem(String text, boolean checked, final Runnable r) {
        JMenuSelectionItem mi = MENU.addMenuCheckItem(text, checked);
        if (r == null) return;
        mi.addActionListener(new MenuItemSelectionListener() {

            @Override
            public void onSelection(TrayActionEvent<JMenuSelectionItem> e) {
                r.run();
            }
            
        });
    }
    
}
