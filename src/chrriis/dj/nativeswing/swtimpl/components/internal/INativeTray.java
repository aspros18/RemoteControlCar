package chrriis.dj.nativeswing.swtimpl.components.internal;

import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;
import chrriis.dj.nativeswing.swtimpl.components.core.TrayMenuData;

public interface INativeTray {

    public int createTrayItem(byte[] imageData, String tooltip);

    public void setTooltip(int itemKey, String text);
    
    public void setImage(int itemKey, byte[] imageData);
    
    public void setVisible(int itemKey, boolean visible);
    
    public int showMessage(int itemKey, String title, String message, TrayMessageType type);
    
    public void disposeTrayItem(int itemKey);
    
    public void disposeTrayMenu(int menuKey);
    
    public void dispose();
    
    public int createTrayMenu();
    
    public void setTrayMenu(TrayMenuData data);
    
}
