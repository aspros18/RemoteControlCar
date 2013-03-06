package chrriis.dj.nativeswing.swtimpl.components.core;

import java.io.Serializable;

public class TrayMenuData implements Serializable, Cloneable {
    
    public Integer key;
    
    public boolean active = true;

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
