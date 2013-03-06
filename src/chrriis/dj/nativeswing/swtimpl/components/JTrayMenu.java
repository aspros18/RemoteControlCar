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
    }
    
    public JTrayItem getTrayItem() {
        return trayItem;
    }
    
    public void setTrayItem(JTrayItem trayItem) {
        if (trayItem == null) {
            DATA.trayItemKey = null;
        }
        else {
            if (trayItem.isDisposed()) throw new IllegalStateException("The selected tray item is disposed");
            DATA.trayItemKey = trayItem.getKey();
        }
        this.trayItem = trayItem;
        refresh();
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
