package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.getTrayContainer;

public class JTrayMenu extends JTrayObject {
    
    private Integer trayItemKey;
    
    private JTrayItem trayItem;
    
    private boolean active;
    
    public JTrayMenu() {
        this(null);
    }

    public JTrayMenu(JTrayItem trayItem) {
        this(trayItem, true);
    }
    
    public JTrayMenu(JTrayItem trayItem, boolean active) {
        super(NATIVE_TRAY.createTrayMenu(trayItem == null ? null : trayItem.getKey(), active));
        this.active = active;
        applyTrayItem(trayItem);
        getTrayContainer().addTrayMenu(this);
    }

    private JTrayMenu applyTrayItem(JTrayItem trayItem) {
        if (trayItem == null) {
            this.trayItem = null;
            this.trayItemKey = null;
            return null;
        }
        if (trayItem.isDisposed()) {
            throw new IllegalStateException("The selected tray item is disposed");
        }
        JTrayMenu oldMenu = trayItem.getTrayMenu();
        if (oldMenu != null && this != oldMenu) {
            oldMenu.trayItem = null;
            oldMenu.trayItemKey = null;
        }
        this.trayItem = trayItem;
        this.trayItemKey = trayItem.getKey();
        return oldMenu;
    }
    
    public JTrayItem getTrayItem() {
        return trayItem;
    }
    
    public void setTrayItem(JTrayItem trayItem) {
        checkState();
        JTrayMenu oldMenu = applyTrayItem(trayItem);
        boolean changed;
        if (trayItem == null) changed = this.trayItem != null;
        else changed = this != oldMenu;
        if (changed) NATIVE_TRAY.setTrayMenu(getKey(), trayItemKey);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        checkState();
        NATIVE_TRAY.setTrayMenuActive(getKey(), active);
        this.active = active;
    }
    
    public JMenuItem addMenuItem(String text) {
        return addMenuItem(text, true);
    }
    
    public JMenuItem addMenuItem(String text, boolean enabled) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), text, enabled, false, MenuItemType.NORMAL);
        return new JMenuItem(this, key, text, enabled);
    }
    
    public JMenuCheckItem addMenuCheckItem(String text) {
        return addMenuCheckItem(text, false);
    }
    
    public JMenuCheckItem addMenuCheckItem(String text, boolean selected) {
        return addMenuCheckItem(text, true, selected);
    }
    
    public JMenuCheckItem addMenuCheckItem(String text, boolean enabled, boolean selected) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), text, enabled, selected, MenuItemType.CHECK);
        return new JMenuCheckItem(this, key, text, enabled, selected);
    }
    
    public JMenuRadioItem addMenuRadioItem(String text) {
        return addMenuRadioItem(text, false);
    }
    
    public JMenuRadioItem addMenuRadioItem(String text, boolean selected) {
        return addMenuRadioItem(text, true, selected);
    }
    
    public JMenuRadioItem addMenuRadioItem(String text, boolean enabled, boolean selected) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), text, enabled, selected, MenuItemType.RADIO);
        return new JMenuRadioItem(this, key, text, enabled, selected);
    }
    
    public JMenuSeparator addMenuSeparator() {
        int key = NATIVE_TRAY.createMenuItem(getKey(), null, false, false, MenuItemType.SEPARATOR);
        return new JMenuSeparator(this, key);
    }
    
    @Override
    public void dispose() {
        dispose(true);
    }
    
    void dispose(boolean outer) {
        if (isDisposed()) return;
        NATIVE_TRAY.disposeTrayMenu(getKey());
        super.dispose();
        if (outer) getTrayContainer().removeTrayMenu(this);
    }
    
    @Override
    void checkState() {
        if (isDisposed()) throw new IllegalStateException("Tray menu is disposed");
    }
    
}
