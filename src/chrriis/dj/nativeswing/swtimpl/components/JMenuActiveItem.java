/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import java.util.Collections;
import java.util.List;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray.MenuItemProperty;

/**
 * Common methods of {@link JMenuCommonItem} and {@link JMenuSelectionItem}.
 * The active menu items are selectable and they have got a label text and enabled state.
 * @param <T> type of the event listeners
 * @author Zolt&aacute;n Farkas
 */
class JMenuActiveItem<T> extends JMenuBaseItem {

    /**
     * A list that contains the event listeners.
     */
    private final List<T> LISTENERS = Collections.synchronizedList(new JTrayListenerList<T>(this));

    /**
     * A string that stores the label text.
     */
    private String text;

    /**
     * A boolean that stores the enabled state.
     */
    private boolean enabled;

    /**
     * Constructs a menu item.
     * @param parent the menu that is the parent of this tray object
     * @param key the unique key of the tray object
     * @param text the initial label text
     * @param enabled the initial enabled state
     */
    JMenuActiveItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key);
        this.text = text;
        this.enabled = enabled;
    }
    
    // Some menu item don't support the event handling because they can not be selected
    // therefore the child classes, that support it, have to extend the visibility
    // of this methods to public from protected.
    
    /**
     * Returns the list of the action listeners.
     */
    protected List<T> getActionListeners() {
        return LISTENERS;
    }

    /**
     * Adds the action listener to the tray item.
     * @param l the listener to be added
     */
    protected void addActionListener(T l) {
        LISTENERS.add(l);
    }

    /**
     * Removes the action listener from the tray item.
     * @param l the listener to be removed
     */
    protected void removeActionListener(T l) {
        LISTENERS.remove(l);
    }

    /**
     * Returns the actual label text of the menu item.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the specified label text to the menu item.
     * @param text the label text
     */
    public void setText(String text) {
        if (text == null) throw new IllegalArgumentException("Menu item text can not be null");
        checkState();
        NATIVE_TRAY.setMenuItemText(getKey(), text);
        this.text = text;
    }

    /**
     * Returns whether this menu item is enabled or not.
     * @return true if this menu item is enabled; otherwise false
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets this menu item to enabled or disabled.
     * @param enabled if true, this menu item will be enabled; otherwise it will be disabled
     */
    public void setEnabled(boolean enabled) {
        checkState();
        applyEnabled(enabled);
        this.enabled = enabled;
    }

    /**
     * Calls a native side method that makes this menu item to enabled or disabled.
     * This medhod is used by {@link #setEnabled(boolean)}.
     * Child classes can redefine this method if they behaviors are different.
     * @param enabled if true, this menu item will be enabled; otherwise it will be disabled
     */
    protected void applyEnabled(boolean enabled) {
        NATIVE_TRAY.setMenuItemProperty(getKey(), MenuItemProperty.ENABLED, enabled);
    }

}
