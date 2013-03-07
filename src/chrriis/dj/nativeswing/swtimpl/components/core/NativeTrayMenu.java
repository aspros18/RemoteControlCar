package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTrayMenu implements NativeTrayObject {
    
    private final Menu MENU;

    private final TrayMenuData DATA;
    
    public NativeTrayMenu(Menu menu, int key) {
        MENU = menu;
        DATA = new TrayMenuData(key);
    }

    @Override
    public int getKey() {
        return DATA.KEY;
    }
    
    public Integer getTrayItemKey() {
        return DATA.trayItemKey;
    }
    
    public void setTrayItemKey(Integer key) {
        DATA.trayItemKey = key;
        boolean visible = getMenu().isVisible();
        getMenu().setVisible(false);
        if (visible && key != null) getMenu().setVisible(true);
    }
    
    public boolean isActive() {
        return DATA.active;
    }
    
    public void setActive(boolean active) {
        DATA.active = active;
        if (!active) getMenu().setVisible(false);
    }
    
    public Menu getMenu() {
        return MENU;
    }
    
}
