/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

/**
 * A drop down menu item.
 * It has a label text, an enabled state and a submenu; it supports images too.
 * On the other hand it has no selection state and it can not be selected
 * because it just opens its submenu when the user select it.
 * @author Zolt&aacute;n Farkas
 */
public class JMenuDropDownItem extends JMenuCommonItem<Object> {

    /**
     * The submenu of this drop down menu item.
     */
    private final JTraySubmenu SUBMENU;

    /**
     * Constructs a drop down menu item and its submenu.
     * @param parent the menu that is the parent of this tray object
     * @param key the unique key of the tray object
     * @param text the initial label text
     * @param enabled the initial enabled state
     */
    JMenuDropDownItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
        SUBMENU = new JTraySubmenu(this);
    }

    /**
     * Returns the submenu of this drop down menu item.
     */
    public JTraySubmenu getSubmenu() {
        return SUBMENU;
    }

    /**
     * {@inheritDoc}
     * If the submenu of this menu item is changed to inactive,
     * the submenu won't be displayed and this menu item will be disabled too so
     * this method changes only the state of the submenu.
     */
    @Override
    protected void applyEnabled(boolean enabled) {
        NATIVE_TRAY.setTrayMenuActive(SUBMENU.getKey(), enabled);
    }

    /**
     * {@inheritDoc}
     * It disposes the submenu of this menu item too.
     */
    @Override
    public boolean dispose() {
        if (!super.dispose()) return false;
        SUBMENU.dispose();
        return true;
    }

}
