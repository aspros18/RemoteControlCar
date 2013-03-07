package chrriis.dj.nativeswing.swtimpl.components.core;

import java.io.Serializable;

public class MenuItemData implements Serializable {
    
    private final int KEY;
    
    private String text;
    
    private Boolean checked;
    
    private Boolean selected;
    
    private MenuItemData child;

    public MenuItemData(int key) {
        KEY = key;
    }
    
}
