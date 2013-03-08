package chrriis.dj.nativeswing.swtimpl.components.core;

public class MenuItemData extends TrayData<MenuItemData> {
    
    public String text;
    
    public Boolean checked;
    
    public Boolean selected;
    
    public MenuItemData child;
    
    public boolean enabled = true;
    
    public MenuItemData(int key) {
        super(key);
    }
    
}
