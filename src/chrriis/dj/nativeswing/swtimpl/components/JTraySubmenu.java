/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;

/**
 * A submenu which belongs to a {@link JMenuDropDownItem}.
 * When a drop down menu item is being created, it creates a submenu
 * which can not be changed after the initialization.
 * @see JMenuDropDownItem#getSubmenu()
 * @see JTraySubmenu#getParent()
 * @author Zolt&aacute;n Farkas
 */
public class JTraySubmenu extends JTrayBaseMenu {

    /**
     * The menu item that created this submenu.
     */
    private final JMenuDropDownItem PARENT;
    
    /**
     * Constructs a submenu.
     * @param parent the menu item that created this submenu
     */
    JTraySubmenu(JMenuDropDownItem parent) {
        super(NATIVE_TRAY.createTraySubmenu(parent.getKey()));
        PARENT = parent;
    }

    /**
     * Returns the menu item that created and belongs to this submenu.
     */
    public JMenuDropDownItem getParent() {
        return PARENT;
    }

}
