package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TrayItem;

class NativeTrayItem implements NativeTrayObject {

    private final int KEY;
    
    private final TrayItem TRAY_ITEM;

    private NativeTrayMenu menu;
    
    private boolean visible;
    
    public NativeTrayItem(TrayItem trayItem, int key) {
        TRAY_ITEM = trayItem;
        KEY = key;
        setVisible(true);
    }

    @Override
    public int getKey() {
        return KEY;
    }

    public boolean isVisible() {
        return visible && TRAY_ITEM.getImage() != null;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        boolean v = isVisible();
        if (v != TRAY_ITEM.getVisible()) {
            TRAY_ITEM.setVisible(v);
        }
    }

    public void setImage(Image img) {
        TRAY_ITEM.setImage(img);
        setVisible(true);
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
