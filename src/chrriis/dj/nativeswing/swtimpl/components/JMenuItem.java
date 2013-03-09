package chrriis.dj.nativeswing.swtimpl.components;

public class JMenuItem extends JMenuActiveItem<MenuItemActionListener> {

    JMenuItem(JTrayMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
        getTrayContainer().getMenuItems().add(this);
    }

    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    boolean dispose(boolean outer) {
        if (!super.dispose()) return false;
        if (outer) getTrayContainer().getMenuItems().remove(this);
        return true;
    }
    
}
