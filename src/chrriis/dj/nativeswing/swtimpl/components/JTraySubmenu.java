package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

public class JTraySubmenu extends JTrayBaseMenu {

    JTraySubmenu(JMenuDropDownItem owner) {
        super(NATIVE_TRAY.createTraySubmenu(owner.getKey()));
    }
    
}
