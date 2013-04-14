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

    private final JTrayBaseMenu PARENT;
    
    JMenuBaseItem(JTrayBaseMenu parent, int key) {
        super(key);
        PARENT = parent;
        getTrayContainer().getMenuItems().add(this);
    }
    
    JTrayBaseMenu getParent() {
        return PARENT;
    }
    
    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    boolean dispose(boolean outer) {
        if (!super.dispose()) return false;
        NATIVE_TRAY.disposeMenuItem(getKey());
        if (outer) getTrayContainer().getMenuItems().remove(this);
        return true;
    }
    
    @Override
    void checkState() {
        if (PARENT.isDisposed()) throw new IllegalStateException("Parent menu is disposed");
        if (isDisposed()) throw new IllegalStateException("Menu item is disposed");
    }
    
}
