package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.components.core.TrayMenuData;

public class JTrayMenu {
    
    private final TrayMenuData DATA;

    private JTrayItem trayItem;
    
    public JTrayMenu() {
        this(null);
    }

    public JTrayMenu(JTrayItem trayItem) {
        DATA = new TrayMenuData();
        setTrayItem(trayItem);
    }
    
    public JTrayItem getTrayItem() {
        return trayItem;
    }
    
    public void setTrayItem(JTrayItem trayItem) {
        if (trayItem == null) DATA.key = null;
        else DATA.key = trayItem.getKey();
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
        JTray.NATIVE_TRAY.setTrayMenu(DATA);
    }
    
}
