package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

abstract class NativeTrayBaseMenu implements NativeTrayObject {
    
    private final int KEY;
    
    private final Menu MENU;
    
    private boolean active;
    
    public NativeTrayBaseMenu(Menu menu, int key, boolean active) {
        this.KEY = key;
        this.MENU = menu;
        this.active = active;
    }
    
    @Override
    public int getKey() {
        return KEY;
    }
    
    public abstract Integer getParentKey();
    
    public Menu getMenu() {
        return MENU;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
        if (!active) getMenu().setVisible(false);
    }
    
}
