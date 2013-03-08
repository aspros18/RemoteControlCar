package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTrayMenu implements NativeTrayObject {
    
    private final int KEY;
    
    private final Menu MENU;
    
    private Integer trayItemKey;
    
    private boolean active = true;
    
    public NativeTrayMenu(Menu menu, int key) {
        MENU = menu;
        KEY = key;
    }

    @Override
    public int getKey() {
        return KEY;
    }
    
    public Integer getTrayItemKey() {
        return trayItemKey;
    }
    
    public void setTrayItemKey(Integer trayItemKey) {
        this.trayItemKey = trayItemKey;
        boolean visible = getMenu().isVisible();
        getMenu().setVisible(false);
        if (visible && trayItemKey != null) getMenu().setVisible(true);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        if (!active) getMenu().setVisible(false);
    }
    
    public Menu getMenu() {
        return MENU;
    }
    
}
