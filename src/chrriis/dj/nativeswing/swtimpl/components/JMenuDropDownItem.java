package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

public class JMenuDropDownItem extends JMenuCommonItem<Object> {

    private final JTraySubmenu SUBMENU;
    
    JMenuDropDownItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
        SUBMENU = new JTraySubmenu(this);
    }

    public JTraySubmenu getSubmenu() {
        return SUBMENU;
    }

    @Override
    protected void applyEnabled(boolean enabled) {
        NATIVE_TRAY.setTrayMenuActive(SUBMENU.getKey(), enabled);
    }

    @Override
    public boolean dispose() {
        if (!super.dispose()) return false;
        SUBMENU.dispose();
        return true;
    }

}
