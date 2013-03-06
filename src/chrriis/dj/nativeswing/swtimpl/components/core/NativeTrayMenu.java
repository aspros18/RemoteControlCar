package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTrayMenu extends NativeTrayObject {
    
    private final Menu MENU;

    private final TrayMenuData DATA;
    
    public NativeTrayMenu(Menu menu, int key) {
        MENU = menu;
        DATA = new TrayMenuData(key);
    }

    @Override
    int getKey() {
        return DATA.KEY;
    }
    
    public boolean isActive() {
        return DATA.active;
    }
    
    public Menu getMenu() {
        return MENU;
    }
    
}
