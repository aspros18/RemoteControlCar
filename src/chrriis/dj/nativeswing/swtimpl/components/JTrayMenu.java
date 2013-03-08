package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.getTrayContainer;

public class JTrayMenu {
    
    private final int KEY;
    
    private Integer trayItemKey;
    
    private JTrayItem trayItem;
    
    private boolean active;
    
    private boolean disposed = false;
    
    public JTrayMenu() {
        this(null);
    }

    public JTrayMenu(JTrayItem trayItem) {
        this(trayItem, true);
    }
    
    public JTrayMenu(JTrayItem trayItem, boolean active) {
        applyTrayItem(trayItem);
        this.active = active;
        this.KEY = NATIVE_TRAY.createTrayMenu(trayItem == null ? null : trayItem.getKey(), active);
        getTrayContainer().addTrayMenu(this);
    }

    private JTrayMenu applyTrayItem(JTrayItem trayItem) {
        if (trayItem == null) {
            this.trayItem = null;
            this.trayItemKey = null;
            return null;
        }
        if (trayItem.isDisposed()) {
            throw new IllegalStateException("The selected tray item is disposed");
        }
        JTrayMenu oldMenu = trayItem.getTrayMenu();
        if (oldMenu != null && this != oldMenu) {
            oldMenu.trayItem = null;
            oldMenu.trayItemKey = null;
        }
        this.trayItem = trayItem;
        this.trayItemKey = trayItem.getKey();
        return oldMenu;
    }
    
    public JTrayItem getTrayItem() {
        return trayItem;
    }
    
    public void setTrayItem(JTrayItem trayItem) {
        checkState();
        JTrayMenu oldMenu = applyTrayItem(trayItem);
        boolean changed;
        if (trayItem == null) changed = this.trayItem != null;
        else changed = this != oldMenu;
        if (changed) NATIVE_TRAY.setTrayMenu(KEY, trayItemKey);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        checkState();
        NATIVE_TRAY.setTrayMenuActive(KEY, active);
        this.active = active;
    }

    public boolean isDisposed() {
        return disposed;
    }
    
    public void dispose() {
        dispose(true);
    }
    
    void dispose(boolean outer) {
        if (disposed) return;
        NATIVE_TRAY.disposeTrayMenu(KEY);
        disposed = true;
        if (outer) getTrayContainer().removeTrayMenu(this);
    }
    
    private void checkState() {
        if (disposed) throw new IllegalStateException("Tray menu is disposed");
    }
    
}
