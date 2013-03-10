package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTrayBaseMenu implements NativeTrayObject {
    
    private final int KEY;
    
    private final Menu MENU;
    
    public NativeTrayBaseMenu(Menu menu, int key) {
        KEY = key;
        MENU = menu;
    }
    
    @Override
    public int getKey() {
        return KEY;
    }
    
    public Menu getMenu() {
        return MENU;
    }
    
}
