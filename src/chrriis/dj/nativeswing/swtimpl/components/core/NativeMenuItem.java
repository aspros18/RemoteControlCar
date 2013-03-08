package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.MenuItem;

public class NativeMenuItem implements NativeTrayObject {
    
    private final int KEY;

    private final MenuItem ITEM;
    
    public NativeMenuItem(int key, MenuItem item) {
        KEY = key;
        ITEM = item;
    }

    @Override
    public int getKey() {
        return KEY;
    }

    public MenuItem getMenuItem() {
        return ITEM;
    }
    
}
