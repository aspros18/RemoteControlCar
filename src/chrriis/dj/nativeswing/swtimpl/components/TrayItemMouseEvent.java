/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

/**
 * An event which indicates that a mouse action occurred in a {@link JTrayItem}.
 * When the left mouse button is clicked, an event is generated and sent to the
 * registered {@link TrayItemMouseListener}s.
 * When the left mouse button is clicked twice, an extra event is generated
 * that can be detected using {@link #isDoubleClick()}.
 * @see TrayItemMouseListener
 * @author Zolt&aacute;n Farkas
 */
public class TrayItemMouseEvent extends TrayActionEvent<JTrayItem> {

    /**
     * Indicates whether the left button is clicked twice.
     * @see #isDoubleClick()
     */
    private final boolean DOUBLE_CLICK;
    
    /**
     * Constructs a {@link TrayItemMouseEvent} object with the specified
     * source component and double click sign.
     * @param component the {@link JTrayItem} that originated the event
     * @param doubleClick the property that indicates whether
     * the left button is clicked twice
     */
    public TrayItemMouseEvent(JTrayItem component, boolean doubleClick) {
        super(component);
        this.DOUBLE_CLICK = doubleClick;
    }

    /**
     * Indicates whether the left button is clicked twice.
     */
    public boolean isDoubleClick() {
        return DOUBLE_CLICK;
    }
    
}
