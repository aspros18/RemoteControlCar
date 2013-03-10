package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Menu;

class NativeTraySubmenu extends NativeTrayBaseMenu {

    private final NativeMenuItem PARENT;
    
    public NativeTraySubmenu(Menu menu, NativeMenuItem parent, int key) {
        super(menu, key, true);
        PARENT = parent;
    }

    @Override
    public Integer getParentKey() {
        return PARENT.getKey();
    }

    @Override
    public boolean isActive() {
        return super.isActive() && PARENT.getMenuItem().isEnabled();
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        PARENT.getMenuItem().setEnabled(active);
    }

}
