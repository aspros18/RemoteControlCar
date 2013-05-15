/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

/**
 * An event which indicates that an action occurred in a {@link JTrayObject}.
 * @author Zolt&aacute;n Farkas
 * @see MenuItemActionListener
 * @see MenuItemSelectionListener
 * @param <T> type of the component on which the event occurred
 */
public class TrayActionEvent<T> {
    
    /**
     * The object on which the event occurred.
     */
    private final T COMPONENT;
    
    /**
     * Constructs an event.
     * @param component the object on which the event occurred
     */
    public TrayActionEvent(T component) {
       COMPONENT = component;
    }
    
    /**
     * The object on which the event occurred.
     */
    public T getComponent() {
        return COMPONENT;
    }
    
}
