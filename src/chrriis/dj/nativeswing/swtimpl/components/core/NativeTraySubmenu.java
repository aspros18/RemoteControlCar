/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

/**
 * Class that contains the additional methods of the submenu.
 * @author Zolt&aacute;n Farkas
 */
class NativeTraySubmenu extends NativeTrayBaseMenu {

    private final NativeMenuItem PARENT;
    
    /**
     * Initializer.
     * @param menu the SWT menu
     * @param parent the drop down menu item
     * @param key the key of the SWT menu
     */
    public NativeTraySubmenu(Menu menu, NativeMenuItem parent, int key) {
        super(menu, key, true);
        PARENT = parent;
    }

    /**
     * Returns the menu item's key.
     * @return key of the menu's parent
     */
    @Override
    public Integer getParentKey() {
        return PARENT.getKey();
    }

    /**
     * Returns whether the submenu is active or inactive.
     * @return true if the submenu is active; otherwise false
     */
    @Override
    public boolean isActive() {
        return super.isActive() && PARENT.getMenuItem().isEnabled();
    }

    /**
     * Sets the submenu to active or inactive.
     * The submenu's tray item will be enabled or disabled.
     * @param active true to enable the tray item; otherwise false
     */
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        PARENT.getMenuItem().setEnabled(active);
    }

}
