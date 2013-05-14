/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray.MenuItemType;
import java.util.Set;

/**
 * Common methods of {@link JTrayMenu} and {@link JTraySubmenu}.
 * They can create every types of the menu items.
 * @author Zolt&aacute;n Farkas
 */
class JTrayBaseMenu extends JTrayObject {

    /**
     * Constructs a tray menu.
     * @param key the unique key of the tray object
     */
    JTrayBaseMenu(int key) {
        super(key);
    }

    /**
     * Adds a {@link JMenuItem} to this menu.
     * @param text the label of the menu item
     * @return the created menu item
     */
    public JMenuItem addMenuItem(String text) {
        return addMenuItem(text, true);
    }

    /**
     * Adds a {@link JMenuItem} to this menu.
     * @param text the label of the menu item
     * @param enabled defines whether or not this menu item can be chosen
     * @return the created menu item
     */
    public JMenuItem addMenuItem(String text, boolean enabled) {
        return addMenuItem(null, text, enabled);
    }

    /**
     * Adds a {@link JMenuItem} to this menu.
     * @param index the position at which the menu item should be inserted
     * @param text the label of the menu item
     * @param enabled defines whether or not this menu item can be chosen
     * @return the created menu item
     */
    public JMenuItem addMenuItem(Integer index, String text, boolean enabled) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, text, enabled, false, MenuItemType.NORMAL);
        return new JMenuItem(this, key, text, enabled);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param text the label of the menu check item
     * @return the created menu check item
     */
    public JMenuSelectionItem addMenuCheckItem(String text) {
        return addMenuCheckItem(text, false);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param text the label of the menu check item
     * @param selected the initial state of the menu check item
     * @return the created menu check item
     */
    public JMenuSelectionItem addMenuCheckItem(String text, boolean selected) {
        return addMenuCheckItem(text, true, selected);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param text the label of the menu check item
     * @param enabled defines whether or not this menu check item can be chosen
     * @param selected the initial state of the menu check item
     * @return the created menu check item
     */
    public JMenuSelectionItem addMenuCheckItem(String text, boolean enabled, boolean selected) {
        return addMenuCheckItem(null, text, enabled, selected);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param index the position at which the menu check item should be inserted
     * @param text the label of the menu check item
     * @param enabled defines whether or not this menu check item can be chosen
     * @param selected the initial state of the menu check item
     * @return the created menu check item
     */
    public JMenuSelectionItem addMenuCheckItem(Integer index, String text, boolean enabled, boolean selected) {
        return addMenuCheckItem(index, text, enabled, selected, false);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param text the label of the menu radio item
     * @return the created menu radio item
     */
    public JMenuSelectionItem addMenuRadioItem(String text) {
        return addMenuRadioItem(text, false);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param text the label of the menu radio item
     * @param selected the initial state of the menu radio item
     * @return the created menu radio item
     */
    public JMenuSelectionItem addMenuRadioItem(String text, boolean selected) {
        return addMenuRadioItem(text, true, selected);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param text the label of the menu radio item
     * @param enabled defines whether or not this menu radio item can be chosen
     * @param selected the initial state of the menu radio item
     * @return the created menu radio item
     */
    public JMenuSelectionItem addMenuRadioItem(String text, boolean enabled, boolean selected) {
        return addMenuRadioItem(null, text, enabled, selected);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param index the position at which the menu radio item should be inserted
     * @param text the label of the menu radio item
     * @param enabled defines whether or not this menu radio item can be chosen
     * @param selected the initial state of the menu radio item
     * @return the created menu radio item
     */
    public JMenuSelectionItem addMenuRadioItem(Integer index, String text, boolean enabled, boolean selected) {
        return addMenuCheckItem(index, text, enabled, selected, true);
    }

    /**
     * Adds a {@link JMenuSelectionItem} to this menu.
     * @param index the position at which the menu selection item should be inserted
     * @param text the label of the menu selection item
     * @param enabled defines whether or not this menu selection item can be chosen
     * @param selected the initial state of the menu selection item
     * @param radio defines whether or not this menu item is a radio item
     * @return the created menu selection item
     */
    private JMenuSelectionItem addMenuCheckItem(Integer index, String text, boolean enabled, boolean selected, boolean radio) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, text, enabled, selected, radio ? MenuItemType.RADIO : MenuItemType.CHECK);
        return new JMenuSelectionItem(this, key, text, enabled, selected);
    }

    /**
     * Adds a {@link JMenuDropDownItem} to this menu.
     * @param text the label of the menu drop down item
     * @return the created menu drop down item
     */
    public JMenuDropDownItem addMenuDropDownItem(String text) {
        return addMenuDropDownItem(text, true);
    }

    /**
     * Adds a {@link JMenuDropDownItem} to this menu.
     * @param text the label of the menu drop down item
     * @param enabled defines whether or not this menu drop down item can be chosen
     * @return the created menu drop down item
     */
    public JMenuDropDownItem addMenuDropDownItem(String text, boolean enabled) {
        return addMenuDropDownItem(null, text, enabled);
    }

    /**
     * Adds a {@link JMenuDropDownItem} to this menu.
     * @param index the position at which the menu drop down item should be inserted
     * @param text the label of the menu drop down item
     * @param enabled defines whether or not this menu drop down item can be chosen
     * @return the created menu drop down item
     */
    public JMenuDropDownItem addMenuDropDownItem(Integer index, String text, boolean enabled) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, text, enabled, false, MenuItemType.DROP_DOWN);
        return new JMenuDropDownItem(this, key, text, enabled);
    }

    /**
     * Adds a separator line to the menu.
     * @return the created separator
     */
    public JMenuSeparator addMenuSeparator() {
        return addMenuSeparator(null);
    }

    /**
     * Adds a separator line to the menu.
     * @param index the position at which the menu separator should be inserted
     * @return the created separator
     */
    public JMenuSeparator addMenuSeparator(Integer index) {
        int key = NATIVE_TRAY.createMenuItem(getKey(), index, null, false, false, MenuItemType.SEPARATOR);
        return new JMenuSeparator(this, key);
    }

    /**
     * Disposes this tray object.
     * If the tray object has already been disposed, it does nothing.
     * If the menu disposes, the menu items will be disposed too.
     */
    @Override
    boolean dispose() {
        if (!super.dispose()) return false;
        Set<JMenuBaseItem> items = getTrayContainer().findMenuItems(this);
        for (JMenuBaseItem item : items) {
            item.dispose();
        }
        NATIVE_TRAY.disposeTrayMenu(getKey());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void checkState() {
        if (isDisposed()) throw new IllegalStateException("Tray menu is disposed");
    }

}
