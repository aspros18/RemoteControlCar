package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

class JMenuBaseItem extends JTrayObject {

    private final JTrayMenu PARENT;
    
    JMenuBaseItem(JTrayMenu parent, int key) {
        super(key);
        PARENT = parent;
    }

    public JTrayMenu getParent() {
        return PARENT;
    }
    
    @Override
    public void dispose() {
        if (isDisposed()) return;
        NATIVE_TRAY.disposeMenuItem(getKey());
        super.dispose();
    }
    
    @Override
    void checkState() {
        if (PARENT.isDisposed()) throw new IllegalStateException("Parent menu is disposed");
        if (isDisposed()) throw new IllegalStateException("Menu item is disposed");
    }
    
}
