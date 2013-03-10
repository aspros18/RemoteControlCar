package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

public class JTrayMenu extends JTrayBaseMenu {
    
    private Integer trayItemKey;
    
    private JTrayItem trayItem;
    
    private boolean active;
    
    public JTrayMenu() {
        this(null);
    }

    public JTrayMenu(JTrayItem trayItem) {
        this(trayItem, true);
    }
    
    public JTrayMenu(JTrayItem trayItem, boolean active) {
        super(createTrayMenu(trayItem, active));
        this.active = active;
        applyTrayItem(trayItem);
        getTrayContainer().getTrayMenus().add(this);
    }

    private static int createTrayMenu(JTrayItem trayItem, boolean active) {
        JTray.checkState();
        return NATIVE_TRAY.createTrayMenu(trayItem == null ? null : trayItem.getKey(), active);
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
        if (changed) NATIVE_TRAY.setTrayMenu(getKey(), trayItemKey);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        checkState();
        NATIVE_TRAY.setTrayMenuActive(getKey(), active);
        this.active = active;
    }
    
    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    boolean dispose(boolean outer) {
        if (!super.isDisposed()) return false;
        if (outer) getTrayContainer().getTrayMenus().remove(this);
        return true;
    }
    
}
