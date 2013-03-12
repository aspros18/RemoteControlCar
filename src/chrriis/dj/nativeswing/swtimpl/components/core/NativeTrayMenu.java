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
 * Methods of tray menu.
 * @author Zolt&aacute;n Farkas
 */
class NativeTrayMenu extends NativeTrayBaseMenu {
    
    private Integer trayItemKey;
    
    public NativeTrayMenu(Menu menu, int key, boolean active) {
        super(menu, key, active);
    }

    /**
     * Returns the tray item's key.
     * @return key of the menu's parent or null if it has no parent yet
     */
    @Override
    public Integer getParentKey() {
        return trayItemKey;
    }
    
    /**
     * Sets the specified tray item.
     * @param nativeItem the tray item or null
     */
    public void setTrayItem(NativeTrayItem nativeItem) {
        if (nativeItem != null) {
            NativeTrayMenu replaced = nativeItem.getNativeTrayMenu();
            if (replaced != null) replaced.setParentKey(null);
            nativeItem.setNativeTrayMenu(this);
            setParentKey(nativeItem.getKey());
        }
        else {
            setParentKey(null);
        }
    }
    
    /**
     * Sets the specified tray item's key.
     * The previous menu of the tray item is visible,
     * it will be hidden and the new one will be showed.
     * @param trayItemKey the key of the tray item or null
     */
    private void setParentKey(Integer trayItemKey) {
        this.trayItemKey = trayItemKey;
        boolean visible = getMenu().isVisible();
        getMenu().setVisible(false);
        if (visible && trayItemKey != null) getMenu().setVisible(true);
    }
    
}
