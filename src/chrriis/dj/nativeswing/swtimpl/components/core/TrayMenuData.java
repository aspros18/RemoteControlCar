package chrriis.dj.nativeswing.swtimpl.components.core;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class TrayMenuData implements Serializable, Cloneable {
    
    private final Set<MenuItemData> MENU_ITEMS = new HashSet<MenuItemData>();
    
    public final int KEY;
    
    public boolean active = true;
    
    public Integer trayItemKey;
    
    public TrayMenuData(int key) {
        KEY = key;
    }
    
    @Override
    public TrayMenuData clone() {
        try{
            return (TrayMenuData) super.clone();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
}
