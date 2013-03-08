package chrriis.dj.nativeswing.swtimpl.components.internal;

import chrriis.dj.nativeswing.swtimpl.components.MenuItemType;
import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;

public interface INativeTray {

    public int createTrayItem(byte[] imageData, String tooltip);

    public void setTrayItemTooltip(int trayItemKey, String text);
    
    public void setTrayItemImage(int trayItemKey, byte[] imageData);
    
    public void setTrayItemVisible(int trayItemKey, boolean visible);
    
    public void showMessage(int trayItemKey, int msgKey, String title, String message, TrayMessageType type);
    
    public int createTrayMenu(Integer trayItemKey, boolean active);
    
    public void setTrayMenu(int menuKey, Integer itemKey);
    
    public void setTrayMenuActive(int menuKey, boolean active);
    
    public int createMenuItem(int menuKey, String text, boolean enabled, boolean selected, MenuItemType type);
    
//    public void setMenuItem(int menuItemKey, Integer menuKey);
    
    public void setMenuItemText(int menuItemKey, String text);
    
    public void setMenuItemEnabled(int menuItemKey, boolean enabled);
    
    public void setMenuItemSelected(int menuItemKey, boolean selected);
    
    public void disposeTrayItem(int trayItemKey);
    
    public void disposeTrayMenu(int menuKey);
    
    public void disposeMenuItem(int menuItemKey);
    
    public void dispose();
    
}
