/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components.internal;

import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;

/**
 * Native system tray support.
 * @author Zolt&aacute;n Farkas
 */
public interface INativeTray {
    
    /**
     * Menu item type.
     * @author Zolt&aacute;n Farkas
     */
    public enum MenuItemType {
        NORMAL,
        RADIO,
        CHECK,
        SEPARATOR,
        DROP_DOWN
    }
    
    /**
     * Menu item boolean property.
     * @author Zolt&aacute;n Farkas
     */
    public enum MenuItemProperty {
        ENABLED,
        SELECTION
    }
    
    /**
     * Returns whether the system tray is supported on the current platform.
     * @return true if the native system tray is available, otherwise false
     */
    public boolean isSupported();
    
    /**
     * Creates a native system tray item.
     * @param imageData bytes of the image to be used
     * @param tooltip the string to be used as tooltip text; if the
     * value is <code>null</code> no tooltip is shown
     * @return the key of the tray item
     */
    public int createTrayItem(byte[] imageData, String tooltip);

    /**
     * Sets the tooltip string for the specified system tray item.
     * @param trayItemKey the key of the tray item
     * @param text the string for the tooltip; if the
     * value is <code>null</code> no tooltip is shown
     */
    public void setTrayItemTooltip(int trayItemKey, String text);
    
    /**
     * Sets the image for the specified system tray item.
     * @param trayItemKey the key of the tray item
     * @param imageData the non-null bytes of the image to be used
     */
    public void setTrayItemImage(int trayItemKey, byte[] imageData);
    
    /**
     * Makes the system tray item visible or invisible.
     * @param trayItemKey the key of the tray item
     * @param visible true to make the tray item visible;
     * false to make it invisible
     */
    public void setTrayItemVisible(int trayItemKey, boolean visible);
    
    /**
     * Displays a popup message near the system tray item.
     * The message will disappear after a time or if the user clicks on it.
     * Clicking on the message may trigger an event.
     * @param trayItemKey the key of the tray item
     * @param msgKey the key of the event callback
     * @param title the non-null text displayed above the message, usually in bold
     * @param message the non-null text displayed for the particular message
     * @param type an enum indicating the message type
     */
    public void showMessage(int trayItemKey, int msgKey, String title, String message, TrayMessageType type);
    
    /**
     * Creates a native system tray menu.
     * @param trayItemKey the key of the tray item or <code>null</code>
     * @param active true to make the tray menu active; false to make it inactive
     * @return the key of the tray menu
     */
    public int createTrayMenu(Integer trayItemKey, boolean active);
    
    /**
     * Sets the specified tray menu's parent.
     * @param menuKey the key of the tray menu
     * @param itemKey the key of the tray item or null
     */
    public void setTrayMenu(int menuKey, Integer itemKey);
    
    /**
     * Sets tray menu active or inactive.
     * @param menuKey the key of the tray menu
     * @param active true to make the tray menu active; false to make it inactive
     */
    public void setTrayMenuActive(int menuKey, boolean active);
    
    /**
     * Creates a native submenu to the specified drop down menu item.
     * @param dropDownMenuItemKey the key of the drop down menu item
     * @return the key of the submenu
     */
    public int createTraySubmenu(int dropDownMenuItemKey);
    
    /**
     * Creates a native menu item to the specified tray menu.
     * @param menuKey the key of the tray menu
     * @param index the position of the menu item on the tray menu
     * or <code>null</code> to use the last position
     * @param text the label of the menu item
     * @param enabled true to make the menu item enabled; false to make it disabled
     * @param selected true to make the menu item selected; false to make it unselected
     * @param type an enum indicating the menu item type
     */
    public int createMenuItem(int menuKey, Integer index, String text, boolean enabled, boolean selected, MenuItemType type);
    
    /**
     * Sets the image for the specified menu item.
     * @param menuItemKey the key of the tray menu
     * @param imageData the non-null bytes of the image to be used
     */
    public void setMenuItemImage(int menuItemKey, byte[] imageData);
    
    /**
     * Sets the text for the specified menu item to the specified label.
     * @param menuItemKey the key of the menu item
     * @param text the new label of the menu item
     */
    public void setMenuItemText(int menuItemKey, String text);
    
    /**
     * Sets a <code>boolean</code> property for the specified menu item.
     * @param menuItemKey the key of the menu item
     * @param property an enum indicating the <code>boolean</code> property
     * @param value value of the property
     */
    public void setMenuItemProperty(int menuItemKey, MenuItemProperty property, boolean value);
    
    /**
     * Disposes the specified tray item and releases the resources held by it.
     * @param trayItemKey the key of the tray item
     */
    public void disposeTrayItem(int trayItemKey);
    
    /**
     * Disposes the specified tray menu and releases the resources held by it.
     * @param menuKey the key of the tray menu
     */
    public void disposeTrayMenu(int menuKey);
    
    /**
     * Disposes the specified menu item and releases the resources held by it.
     * @param menuItemKey the key of the menu item
     */
    public void disposeMenuItem(int menuItemKey);
    
    /**
     * Disposes every native components and releases the resources held by it.
     * The <code>Tray</code> won't be able to create any component after that call.
     */
    public void dispose();
    
}
