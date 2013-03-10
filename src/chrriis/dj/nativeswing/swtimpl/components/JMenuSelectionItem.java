package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray.MenuItemProperty;

public class JMenuSelectionItem extends JMenuActiveItem<MenuItemSelectionListener> {

    private boolean selected;
    
    JMenuSelectionItem(JTrayBaseMenu parent, int key, String text, boolean enabled, boolean selected) {
        super(parent, key, text, enabled);
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        checkState();
        this.selected = selected;
        NATIVE_TRAY.setMenuItemProperty(getKey(), MenuItemProperty.SELECTION, selected);
    }
    
}
