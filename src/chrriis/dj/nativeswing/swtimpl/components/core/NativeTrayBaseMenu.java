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
 * Common methods of tray menu and submenu.
 * @author Zolt&aacute;n Farkas
 */
abstract class NativeTrayBaseMenu implements NativeTrayObject {
    
    private final int KEY;
    
    private final Menu MENU;
    
    private boolean active;
    
    /**
     * Initializer.
     * @param menu the SWT menu
     * @param key the key of the SWT menu
     * @param active true if the menu can appear; false otherwise
     */
    public NativeTrayBaseMenu(Menu menu, int key, boolean active) {
        this.KEY = key;
        this.MENU = menu;
        this.active = active;
    }
    
    @Override
    public int getKey() {
        return KEY;
    }
    
    public abstract Integer getParentKey();
    
    public Menu getMenu() {
        return MENU;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        if (!active) getMenu().setVisible(false);
    }
    
}
