/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

/**
 * The listener interface for receiving action events on a {@link JMenuSelectionItem}.
 * @see JMenuSelectionItem#addActionListener(MenuItemSelectionListener)
 * @see JMenuSelectionItem#removeActionListener(MenuItemSelectionListener)
 * @author Zolt&aacute;n Farkas
 */
public interface MenuItemSelectionListener {
    
    /**
     * Invoked when the selection property of a {@link JMenuSelectionItem} has been changed.
     * You can get the new selection property by using {@link JMenuSelectionItem#isSelected()}.
     * If the user select the same radio button again, the selection property won't be changed
     * but the event will be generated again.
     */
    public void onSelectionChanged(TrayActionEvent<JMenuSelectionItem> e);
    
}
