package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.components.core.TrayMenuData;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

public class JTrayMenu {
    
    private final TrayMenuData DATA;

    private JTrayItem trayItem;
    
    public JTrayMenu() {
        this(null);
    }

    public JTrayMenu(JTrayItem trayItem) {
        DATA = new TrayMenuData(NATIVE_TRAY.createTrayMenu());
        if (trayItem != null) setTrayItem(trayItem);
        JTrayContainer.addTrayMenu(this);
    }
    
    public JTrayItem getTrayItem() {
        return trayItem;
    }
    
    public void setTrayItem(JTrayItem trayItem) {
        boolean changed;
        if (trayItem == null) {
            DATA.trayItemKey = null;
            changed = this.trayItem != null;
        }
        else {
            if (trayItem.isDisposed()) throw new IllegalStateException("The selected tray item is disposed");
            JTrayMenu oldMenu = trayItem.getTrayMenu();
            if (oldMenu != null && this != oldMenu) {
                oldMenu.trayItem = null;
                oldMenu.DATA.trayItemKey = null;
            }
            DATA.trayItemKey = trayItem.getKey();
            changed = this != oldMenu;
        }
        this.trayItem = trayItem;
        if (changed) refresh();
    }
    
    public boolean isActive() {
        return DATA.active;
    }
    
    public void setActive(boolean active) {
        DATA.active = active;
        refresh();
    }
    
    private void refresh() {
        NATIVE_TRAY.setTrayMenu(DATA);
    }
    
}
