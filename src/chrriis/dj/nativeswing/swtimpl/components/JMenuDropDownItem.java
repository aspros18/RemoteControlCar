package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

public class JMenuDropDownItem extends JMenuCommonItem<Object> {

    JMenuDropDownItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
    }
    
    public void setTraySubmenu(JTraySubmenu submenu) {
        NATIVE_TRAY.setTraySubmenu(getKey(), submenu == null ? null : submenu.getKey());
    }
    
}
