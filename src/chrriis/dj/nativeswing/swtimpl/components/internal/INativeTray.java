package chrriis.dj.nativeswing.swtimpl.components.internal;

import chrriis.dj.nativeswing.swtimpl.components.core.TrayMenuData;

public interface INativeTray {

    public int createTrayItem(byte[] imageData, String tooltip);

    public void setTooltip(int key, String text);
    
    public void setImage(int key, byte[] imageData);
    
    public void setVisible(int key, boolean visible);
    
    public void disposeTrayItem(int key);
    
    public void disposeTrayMenu(int key);
    
    public void dispose();
    
    public int createTrayMenu();
    
    public void setTrayMenu(TrayMenuData data);
    
}
