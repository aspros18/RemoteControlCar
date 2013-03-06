package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTrayMenu {
    
    private final Menu MENU;

    private final TrayMenuData DATA;
    
    public NativeTrayMenu(Menu menu, TrayMenuData data) {
        MENU = menu;
        DATA = data;
    }

    public boolean isActive() {
        return DATA.active;
    }
    
    public Menu getMenu() {
        return MENU;
    }
    
}
