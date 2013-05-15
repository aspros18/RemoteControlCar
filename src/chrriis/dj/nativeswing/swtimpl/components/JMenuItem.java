/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import java.util.List;

/**
 * A regular menu item.
 * It has a label text and it can be selected too,
 * it supports images and it has enabled state.
 * On the other hand it has no selection state and it has no submenu.
 * @author Zolt&aacute;n Farkas
 */
public class JMenuItem extends JMenuCommonItem<MenuItemActionListener> {

    /**
     * Constructs a menu item.
     * @param parent the menu that is the parent of this tray object
     * @param key the unique key of the tray object
     * @param text the initial label text
     * @param enabled the initial enabled state
     */
    JMenuItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MenuItemActionListener> getActionListeners() {
        return super.getActionListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addActionListener(MenuItemActionListener l) {
        super.addActionListener(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeActionListener(MenuItemActionListener l) {
        super.removeActionListener(l);
    }

}
