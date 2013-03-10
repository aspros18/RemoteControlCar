package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTrayMenu extends NativeTrayBaseMenu {
    
    private Integer trayItemKey;
    
    private boolean active = true;
    
    public NativeTrayMenu(Menu menu, int key, boolean active) {
        super(menu, key);
        this.active = active;
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
    
}
