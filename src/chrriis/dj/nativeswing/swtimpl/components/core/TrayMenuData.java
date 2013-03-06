package chrriis.dj.nativeswing.swtimpl.components.core;

import java.io.Serializable;

public class TrayMenuData implements Serializable, Cloneable {
    
    public final Integer KEY;
    
    public Integer trayItemKey;
    
    public boolean active = true;

    public TrayMenuData(Integer key) {
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
