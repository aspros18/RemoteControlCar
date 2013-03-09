package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

abstract class JMenuBaseItem extends JTrayListenerObject {

    private final int KEY;
    
    private final JTrayMenu PARENT;
    
    private boolean disposed = false;
    
    JMenuBaseItem(JTrayMenu parent, int key) {
        KEY = key;
        PARENT = parent;
    }

    int getKey() {
        return KEY;
    }

    public JTrayMenu getParent() {
        return PARENT;
    }

    public boolean isDisposed() {
        return disposed;
    }
    
    public void dispose() {
        if (disposed) return;
        NATIVE_TRAY.disposeMenuItem(KEY);
        disposed = true;
    }
    
    @Override
    void checkState() {
        if (PARENT.isDisposed()) throw new IllegalStateException("Parent menu is disposed");
        if (disposed) throw new IllegalStateException("Menu item is disposed");
    }
    
}
