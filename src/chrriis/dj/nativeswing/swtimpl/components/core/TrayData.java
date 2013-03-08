package chrriis.dj.nativeswing.swtimpl.components.core;

import java.io.Serializable;

class TrayData<T extends TrayData> implements Serializable, Cloneable {

    public final int KEY;

    public TrayData(int key) {
        KEY = key;
    }
    
    @Override
    public T clone() {
        try {
            return (T) super.clone();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
}
