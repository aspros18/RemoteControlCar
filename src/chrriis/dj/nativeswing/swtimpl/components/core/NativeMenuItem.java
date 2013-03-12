/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.MenuItem;

/**
 * Methods of the menu item.
 * @author Zolt&aacute;n Farkas
 */
public class NativeMenuItem implements NativeTrayObject {
    
    private final int KEY;

    private final MenuItem ITEM;
    
    public NativeMenuItem(int key, MenuItem item) {
        KEY = key;
        ITEM = item;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getKey() {
        return KEY;
    }

    public MenuItem getMenuItem() {
        return ITEM;
    }
    
}
