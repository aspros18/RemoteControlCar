package chrriis.dj.nativeswing.swtimpl.components.internal;

import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;

public interface INativeTray {
    
    public enum MenuItemType {
        NORMAL,
        RADIO,
        CHECK,
        SEPARATOR,
        DROP_DOWN
    }
    
    public enum MenuItemProperty {
        ENABLED,
        SELECTION
    }
    
    public boolean isSupported();
    
    public int createTrayItem(byte[] imageData, String tooltip);

    public void setTrayItemTooltip(int trayItemKey, String text);
    
    public void setTrayItemImage(int trayItemKey, byte[] imageData);
    
    public void setTrayItemVisible(int trayItemKey, boolean visible);
    
    public void showMessage(int trayItemKey, int msgKey, String title, String message, TrayMessageType type);
    
    public int createTrayMenu(Integer trayItemKey, boolean active);
    
    public void setTrayMenu(int menuKey, Integer itemKey);
    
    public void setTrayMenuActive(int menuKey, boolean active);
    
    public int createTraySubmenu(int dropDownMenuItemKey);
    
    public int createMenuItem(int menuKey, Integer index, String text, boolean enabled, boolean selected, MenuItemType type);
    
    public void setMenuItemImage(int menuItemKey, byte[] imageData);
    
    public void setMenuItemText(int menuItemKey, String text);
    
    public void setMenuItemProperty(int menuItemKey, MenuItemProperty property, boolean value);
    
    public void disposeTrayItem(int trayItemKey);
    
    public void disposeTrayMenu(int menuKey);
    
    public void disposeMenuItem(int menuItemKey);
    
    public void dispose();
    
}
