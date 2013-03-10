package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray.MenuItemProperty;
import java.util.List;

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

    @Override
    public List<MenuItemSelectionListener> getActionListeners() {
        return super.getActionListeners();
    }

    @Override
    public void addActionListener(MenuItemSelectionListener l) {
        super.addActionListener(l);
    }

    @Override
    public void removeActionListener(MenuItemSelectionListener l) {
        super.removeActionListener(l);
    }
    
}
