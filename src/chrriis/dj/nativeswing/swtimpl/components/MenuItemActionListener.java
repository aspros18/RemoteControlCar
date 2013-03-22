/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

/**
 * The listener interface for receiving action events on a {@link JMenuItem}.
 * @see JMenuItem#addActionListener(MenuItemActionListener)
 * @see JMenuItem#removeActionListener(MenuItemActionListener)
 * @author Zolt&aacute;n Farkas
 */
public interface MenuItemActionListener {
    
    /**
     * Invoked when a {@link JMenuItem} has been selected.
     */
    public void onAction(TrayActionEvent<JMenuItem> e);
    
}
