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
 * Defines common behaviors for tray menu items.
 * @author Zolt&aacute;n Farkas
 */
class JMenuBaseItem extends JTrayObject {

    /**
     * The menu that is the parent of this tray object.
     */
    private final JTrayBaseMenu PARENT;
    
    /**
     * Constructs a menu item.
     * @param parent the menu that is the parent of this tray object
     * @param key the unique key of the tray object
     */
    JMenuBaseItem(JTrayBaseMenu parent, int key) {
        super(key);
        PARENT = parent;
        getTrayContainer().getMenuItems().add(this);
    }
    
    /**
     * Returns the menu that is the parent of this tray object.
     */
    JTrayBaseMenu getParent() {
        return PARENT;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    /**
     * This method is used by {@link JTrayContainer} when it is disposed.
     * When the tray container is disposing it disposes and removes every
     * tray object so it's unnecessary to indicate the menu item removing.
     * @param outer if false, tray container doesn't remove the item object
     */
    boolean dispose(boolean outer) {
        if (!super.dispose()) return false;
        NATIVE_TRAY.disposeMenuItem(getKey());
        if (outer) getTrayContainer().getMenuItems().remove(this);
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void checkState() {
        if (PARENT.isDisposed()) throw new IllegalStateException("Parent menu is disposed");
        if (isDisposed()) throw new IllegalStateException("Menu item is disposed");
    }
    
}
