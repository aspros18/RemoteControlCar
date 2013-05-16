/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray.MenuItemProperty;
import java.util.List;

/**
 * A check- or radio-menu item.
 * It has a label text, an enabled state and it can be checked.
 * On the other hand it has no image support because the checkbox
 * or the radio button uses the place of the image.
 * @author Zolt&aacute;n Farkas
 */
public class JMenuSelectionItem extends JMenuActiveItem<MenuItemSelectionListener> {

    /**
     * Stores the selection state.
     */
    private boolean selected;

    /**
     * Constructs a selection menu item.
     * @param parent the menu that is the parent of this tray object
     * @param key the unique key of the tray object
     * @param text the initial label text
     * @param enabled the initial enabled state
     * @param selected the initial selection state
     */
    JMenuSelectionItem(JTrayBaseMenu parent, int key, String text, boolean enabled, boolean selected) {
        super(parent, key, text, enabled);
        this.selected = selected;
    }

    /**
     * Returns whether this menu item is selected or not.
     * @return true if the menu item is selected; otherwise false
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the menu item selected or unselected.
     * @param selected if true, the menu item will be selected; otherwise it will be unselected
     */
    public void setSelected(boolean selected) {
        checkState();
        this.selected = selected;
        NATIVE_TRAY.setMenuItemProperty(getKey(), MenuItemProperty.SELECTION, selected);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MenuItemSelectionListener> getActionListeners() {
        return super.getActionListeners();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addActionListener(MenuItemSelectionListener l) {
        super.addActionListener(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeActionListener(MenuItemSelectionListener l) {
        super.removeActionListener(l);
    }

}
