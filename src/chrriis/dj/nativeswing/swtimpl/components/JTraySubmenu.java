package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

public class JTraySubmenu extends JTrayBaseMenu {

    public JTraySubmenu() {
        this(null);
    }

    public JTraySubmenu(JMenuDropDownItem item) {
        super(NATIVE_TRAY.createTraySubmenu(item == null ? null : item.getKey()));
    }

}
