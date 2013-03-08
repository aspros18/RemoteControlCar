package chrriis.dj.nativeswing.swtimpl.components.internal;

import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;

public interface INativeTray {

    public int createTrayItem(byte[] imageData, String tooltip);

    public void setTrayItemTooltip(int itemKey, String text);
    
    public void setTrayItemImage(int itemKey, byte[] imageData);
    
    public void setTrayItemVisible(int itemKey, boolean visible);
    
    public void showMessage(int itemKey, int msgKey, String title, String message, TrayMessageType type);
    
    public int createTrayMenu(Integer itemKey, boolean active);
    
    public void setTrayMenu(int menuKey, Integer itemKey);
    
    public void setTrayMenuActive(int menuKey, boolean active);
    
    public void disposeTrayItem(int itemKey);
    
    public void disposeTrayMenu(int menuKey);
    
    public void dispose();
    
}
