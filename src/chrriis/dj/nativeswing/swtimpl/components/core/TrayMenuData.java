package chrriis.dj.nativeswing.swtimpl.components.core;

import java.io.Serializable;

public class TrayMenuData implements Serializable, Cloneable {
    
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
