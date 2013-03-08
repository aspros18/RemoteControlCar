package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

abstract class JMenuBaseItem {

    private final int KEY;
    
    private boolean disposed = false;
    
    JMenuBaseItem(int key) {
        KEY = key;
    }

    int getKey() {
        return KEY;
    }

    public boolean isDisposed() {
        return disposed;
    }
    
    public void dispose() {
        if (disposed) return;
        NATIVE_TRAY.disposeMenuItem(KEY);
        disposed = true;
    }
    
    protected void checkState() {
        if (disposed) throw new IllegalStateException("Menu item is disposed");
    }
    
}
