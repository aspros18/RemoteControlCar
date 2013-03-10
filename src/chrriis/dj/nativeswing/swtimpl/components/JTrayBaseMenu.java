package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray.MenuItemType;
import java.util.Set;

class JTrayBaseMenu extends JTrayObject {

    public JTrayBaseMenu(int key) {
        super(key);
    }

    public JMenuItem addMenuItem(String text) {
        return addMenuItem(text, true);
    }
    
    public JMenuItem addMenuItem(String text, boolean enabled) {
        return addMenuItem(null, text, enabled);
    }
    
    public JMenuItem addMenuItem(Integer index, String text, boolean enabled) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, text, enabled, false, MenuItemType.NORMAL);
        return new JMenuItem(this, key, text, enabled);
    }
    
    public JMenuSelectionItem addMenuCheckItem(String text) {
        return addMenuCheckItem(text, false);
    }
    
    public JMenuSelectionItem addMenuCheckItem(String text, boolean selected) {
        return addMenuCheckItem(text, true, selected);
    }
    
    public JMenuSelectionItem addMenuCheckItem(String text, boolean enabled, boolean selected) {
        return addMenuCheckItem(null, text, enabled, selected);
    }
    
    public JMenuSelectionItem addMenuCheckItem(Integer index, String text, boolean enabled, boolean selected) {
        return addMenuCheckItem(index, text, enabled, selected, false);
    }
    
    public JMenuSelectionItem addMenuRadioItem(String text) {
        return addMenuRadioItem(text, false);
    }
    
    public JMenuSelectionItem addMenuRadioItem(String text, boolean selected) {
        return addMenuRadioItem(text, true, selected);
    }
    
    public JMenuSelectionItem addMenuRadioItem(String text, boolean enabled, boolean selected) {
        return addMenuRadioItem(null, text, enabled, selected);
    }
    
    public JMenuSelectionItem addMenuRadioItem(Integer index, String text, boolean enabled, boolean selected) {
        return addMenuCheckItem(index, text, enabled, selected, true);
    }
    
    private JMenuSelectionItem addMenuCheckItem(Integer index, String text, boolean enabled, boolean selected, boolean radio) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, text, enabled, selected, radio ? MenuItemType.RADIO : MenuItemType.CHECK);
        return new JMenuSelectionItem(this, key, text, enabled, selected);
    }
    
    public JMenuDropDownItem addMenuDropDownItem(String text) {
        return addMenuDropDownItem(text, true);
    }
    
    public JMenuDropDownItem addMenuDropDownItem(String text, boolean enabled) {
        return addMenuDropDownItem(null, text, enabled);
    }
    
    public JMenuDropDownItem addMenuDropDownItem(Integer index, String text, boolean enabled) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, text, enabled, false, MenuItemType.DROP_DOWN);
        return new JMenuDropDownItem(this, key, text, enabled);
    }
    
    public JMenuSeparator addMenuSeparator() {
        return addMenuSeparator(null);
    }
    
    public JMenuSeparator addMenuSeparator(Integer index) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, null, false, false, MenuItemType.SEPARATOR);
        return new JMenuSeparator(this, key);
    }

    @Override
    boolean dispose() {
        if (!super.dispose()) return false;
        Set<JMenuBaseItem> items = getTrayContainer().findMenuItems(this);
        for (JMenuBaseItem item : items) {
            item.dispose();
        }
        NATIVE_TRAY.disposeTrayMenu(getKey());
        return true;
    }
    
    @Override
    void checkState() {
        if (isDisposed()) throw new IllegalStateException("Tray menu is disposed");
    }
    
}
