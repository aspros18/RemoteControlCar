package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.TrayItem;

class NativeTrayItem {

    private final TrayItem TRAY_ITEM;

    private NativeTrayMenu menu;

    public NativeTrayItem(TrayItem trayItem) {
        TRAY_ITEM = trayItem;
    }

    public TrayItem getTrayItem() {
        return TRAY_ITEM;
    }

    public NativeTrayMenu getNativeTrayMenu() {
        return menu;
    }
    
}
