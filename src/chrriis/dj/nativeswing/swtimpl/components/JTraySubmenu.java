package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

public class JTraySubmenu extends JTrayBaseMenu {

    private final JMenuDropDownItem PARENT;
    
    JTraySubmenu(JMenuDropDownItem parent) {
        super(NATIVE_TRAY.createTraySubmenu(parent.getKey()));
        PARENT = parent;
    }

    public JMenuDropDownItem getParent() {
        return PARENT;
    }

}
