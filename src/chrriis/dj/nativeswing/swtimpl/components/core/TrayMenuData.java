package chrriis.dj.nativeswing.swtimpl.components.core;

import java.util.HashSet;
import java.util.Set;

public class TrayMenuData extends TrayData<TrayMenuData> {
    
    private final Set<MenuItemData> MENU_ITEMS = new HashSet<MenuItemData>();
    
    public boolean active = true;
    
    public Integer trayItemKey;
    
    public TrayMenuData(int key) {
        super(key);
    }
    
}
