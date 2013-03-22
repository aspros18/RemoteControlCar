package org.dyndns.fzoli.ui.systemtray;

import chrriis.dj.nativeswing.swtimpl.components.JMenuItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuSelectionItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayMenu;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemActionListener;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemSelectionListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayActionEvent;
import java.awt.image.BufferedImage;

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
    public MenuItem addMenuItem(String text, final Runnable r) {
        return addMenuItem(text, null, r);
    }
    
    @Override
    public MenuItem addMenuItem(String text, BufferedImage img, final Runnable r) {
        JMenuItem mi = MENU.addMenuItem(text);
        if (img != null) mi.setImage(img);
        if (r != null) {
            mi.addActionListener(new MenuItemActionListener() {

                @Override
                public void onAction(TrayActionEvent<JMenuItem> e) {
                    r.run();
                }

            });
        }
        return new SwtMenuItem(mi);
    }

    @Override
    public void addCheckboxMenuItem(String text, boolean checked, final Runnable r) {
        JMenuSelectionItem mi = MENU.addMenuCheckItem(text, checked);
        if (r != null) {
            mi.addActionListener(new MenuItemSelectionListener() {

                @Override
                public void onSelectionChanged(TrayActionEvent<JMenuSelectionItem> e) {
                    r.run();
                }

            });
        }
    }
    
}
