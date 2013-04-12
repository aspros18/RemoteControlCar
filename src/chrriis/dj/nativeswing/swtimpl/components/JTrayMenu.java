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
 * A popup menu component that is deployed from a tray icon.
 * @author Zolt&aacute;n Farkas
 */
public class JTrayMenu extends JTrayBaseMenu {
    
    /**
     * The key of the current {@link JTrayItem}.
     */
    private Integer trayItemKey;
    
    /**
     * The current {@link JTrayItem}.
     */
    private JTrayItem trayItem;
    
    /**
     * Stores whether the tray menu is active.
     */
    private boolean active;
    
    /**
     * Constructs a tray menu.
     * After a {@link JTrayMenu} has been created, it can be set to
     * a {@link JTrayItem} by calling {@link JTrayMenu#setTrayItem(JTrayItem)}.
     */
    public JTrayMenu() {
        this(null);
    }

    /**
     * Constructs a tray menu.
     * @param trayItem a tray item which will be associated with this menu
     */
    public JTrayMenu(JTrayItem trayItem) {
        this(trayItem, true);
    }
    
    /**
     * Constructs a tray menu.
     * @param trayItem a tray item which will be associated with this menu
     * @param active sets the menu to active or inactive
     */
    public JTrayMenu(JTrayItem trayItem, boolean active) {
        super(createTrayMenu(trayItem, active));
        this.active = active;
        applyTrayItem(trayItem);
        getTrayContainer().getTrayMenus().add(this);
    }

    /**
     * Helper method to create a native tray menu safely.
     * It is used as a parameter of the parent class' constructor.
     */
    private static int createTrayMenu(JTrayItem trayItem, boolean active) {
        JTray.checkState();
        return NATIVE_TRAY.createTrayMenu(trayItem == null ? null : trayItem.getKey(), active);
    }
    
    /**
     * Applies the new tray item on this menu.
     * It's called when the native side changes
     * and it's necessary to refresh the variables.
     */
    private JTrayMenu applyTrayItem(JTrayItem trayItem) {
        if (trayItem == null) {
            this.trayItem = null;
            this.trayItemKey = null;
            return null;
        }
        if (trayItem.isDisposed()) {
            throw new IllegalStateException("The selected tray item is disposed");
        }
        JTrayMenu oldMenu = trayItem.getTrayMenu();
        if (oldMenu != null && this != oldMenu) {
            oldMenu.trayItem = null;
            oldMenu.trayItemKey = null;
        }
        this.trayItem = trayItem;
        this.trayItemKey = trayItem.getKey();
        return oldMenu;
    }
    
    /**
     * Returns the current tray item which is associated with this menu.
     * @return the tray item or null if the menu doesn't belong to any tray item
     */
    public JTrayItem getTrayItem() {
        return trayItem;
    }
    
    /**
     * Sets the tray item which will be associated with this menu.
     * If there's a previous tray item, it will be unassociated.
     */
    public void setTrayItem(JTrayItem trayItem) {
        checkState();
        JTrayMenu oldMenu = applyTrayItem(trayItem);
        boolean changed;
        if (trayItem == null) changed = this.trayItem != null;
        else changed = this != oldMenu;
        if (changed) NATIVE_TRAY.setTrayMenu(getKey(), trayItemKey);
    }
    
    /**
     * Returns whether the tray menu is active.
     * If the menu is inactive it won't appear when the user
     * clicks on the tray item by right mouse button.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the tray menu to active or inactive.
     * If the menu is inactive it won't appear when the user
     * clicks on the tray item by right mouse button.
     * @param active if <code>true</code>, the menu becomes active;
     * otherwise it becomes inactive
     */
    public void setActive(boolean active) {
        checkState();
        NATIVE_TRAY.setTrayMenuActive(getKey(), active);
        this.active = active;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    /**
     * {@inheritDoc}
     * This method is used by {@link JTrayContainer} when it is disposed.
     * When the tray container is disposing it disposes and removes every
     * tray object so it's unnecessary to indicate the tray menu removing.
     * @param outer if false, tray container doesn't remove the menu object
     */
    boolean dispose(boolean outer) {
        if (!super.isDisposed()) return false;
        if (outer) getTrayContainer().getTrayMenus().remove(this);
        return true;
    }
    
}
