package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.getTrayContainer;

public class JTrayMenu {
    
    private final int KEY;
    
    private Integer trayItemKey;
    
    private JTrayItem trayItem;
    
    private boolean active = true;
    
    private boolean disposed = false;
    
    public JTrayMenu() {
        this(null);
    }

    public JTrayMenu(JTrayItem trayItem) {
        KEY = NATIVE_TRAY.createTrayMenu();
        if (trayItem != null) setTrayItem(trayItem);
        getTrayContainer().addTrayMenu(this);
    }
    
    public JTrayItem getTrayItem() {
        return trayItem;
    }
    
    public void setTrayItem(JTrayItem trayItem) {
        checkState();
        boolean changed;
        if (trayItem == null) {
            trayItemKey = null;
            changed = this.trayItem != null;
        }
        else {
            if (trayItem.isDisposed()) throw new IllegalStateException("The selected tray item is disposed");
            JTrayMenu oldMenu = trayItem.getTrayMenu();
            if (oldMenu != null && this != oldMenu) {
                oldMenu.trayItem = null;
                oldMenu.trayItemKey = null;
            }
            trayItemKey = trayItem.getKey();
            changed = this != oldMenu;
        }
        this.trayItem = trayItem;
        if (changed) NATIVE_TRAY.setTrayMenu(KEY, trayItemKey);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        checkState();
        this.active = active;
        NATIVE_TRAY.setTrayMenuActive(KEY, active);
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
