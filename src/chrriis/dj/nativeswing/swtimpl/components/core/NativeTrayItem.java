package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.TrayItem;

class NativeTrayItem extends NativeTrayObject {

    private final int KEY;
    
    private final TrayItem TRAY_ITEM;

    private NativeTrayMenu menu;
    
    public NativeTrayItem(TrayItem trayItem, int key) {
        TRAY_ITEM = trayItem;
        KEY = key;
    }

    @Override
    int getKey() {
        return KEY;
    }

    public TrayItem getTrayItem() {
        return TRAY_ITEM;
    }

    public NativeTrayMenu getNativeTrayMenu() {
        return menu;
    }

    public void setNativeTrayMenu(NativeTrayMenu menu) {
        this.menu = menu;
    }
    
}
