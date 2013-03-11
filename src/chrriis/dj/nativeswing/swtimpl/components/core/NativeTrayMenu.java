package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTrayMenu extends NativeTrayBaseMenu {
    
    private Integer trayItemKey;
    
    public NativeTrayMenu(Menu menu, int key, boolean active) {
        super(menu, key, active);
    }

    @Override
    public Integer getParentKey() {
        return trayItemKey;
    }
    
    public void setTrayItem(NativeTrayItem nativeItem) {
        if (nativeItem != null) {
            NativeTrayMenu replaced = nativeItem.getNativeTrayMenu();
            if (replaced != null) replaced.setParentKey(null);
            nativeItem.setNativeTrayMenu(this);
            setParentKey(nativeItem.getKey());
        }
        else {
            setParentKey(null);
        }
    }
    
    private void setParentKey(Integer trayItemKey) {
        this.trayItemKey = trayItemKey;
        boolean visible = getMenu().isVisible();
        getMenu().setVisible(false);
        if (visible && trayItemKey != null) getMenu().setVisible(true);
    }
    
}
