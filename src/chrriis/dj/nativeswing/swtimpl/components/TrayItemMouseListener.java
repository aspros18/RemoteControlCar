/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

/**
 * The listener interface for receiving mouse events on a {@link JTrayItem}.
 * @see JTrayItem#addMouseListener(TrayItemMouseListener)
 * @see JTrayItem#removeMouseListener(TrayItemMouseListener)
 * @author Zolt&aacute;n Farkas
 */
public interface TrayItemMouseListener {
    
    /**
     * Invoked when the left mouse button
     * has been pressed on a {@link JTrayItem}.
     */
    public void onClick(TrayItemMouseEvent e);
    
}
