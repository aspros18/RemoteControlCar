/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a native tray icon.
 * It can have a tooltip text, an image, a popup menu,
 * and a set of mouse listeners associated with it.
 * To create a {@link JTrayItem} use {@link JTray#createTrayItem()}.
 * @author Zolt&aacute;n Farkas
 */
public class JTrayItem extends JTrayObject {

    /**
     * Stores the current tooltip text.
     */
    private String tooltip;
    
    /**
     * Stores the current image bytes.
     */
    private byte[] imageData;
    
    /**
     * Stores whether the tray item is visible.
     */
    private boolean visible;
    
    /**
     * A list that contains the mouse listeners.
     */
    private final List<TrayItemMouseListener> MOUSE_LISTENERS = Collections.synchronizedList(new JTrayListenerList<TrayItemMouseListener>(this));
    
    /**
     * A list that contains the menu display listeners.
     */
    private final List<Runnable> MENU_DISPLAY_LISTENERS = Collections.synchronizedList(new JTrayListenerList<Runnable>(this));
    
    /**
     * Constructs a tray item (used by {@link JTray}).
     * @param key the key of the tray item
     * @param tooltip the tooltip text
     * @param imageData the bytes of the image
     */
    JTrayItem(int key, String tooltip, byte[] imageData) {
        super(key);
        this.tooltip = tooltip;
        this.imageData = imageData;
        this.visible = imageData != null;
        getTrayContainer().getTrayItems().add(this);
    }
    
    /**
     * Returns the tray item's popup menu.
     * @return the {@link JTrayMenu} object or null if the tray item has no menu
     */
    public JTrayMenu getTrayMenu() {
        return getTrayContainer().findTrayMenu(this);
    }
    
    /**
     * Returns the list of the mouse listeners.
     */
    public List<TrayItemMouseListener> getMouseListeners() {
        return MOUSE_LISTENERS;
    }
    
    /**
     * Adds the mouse listener to the tray item.
     * @param l the listener to be added
     */
    public void addMouseListener(TrayItemMouseListener l) {
        MOUSE_LISTENERS.add(l);
    }
    
    /**
     * Removes the mouse listener from the tray item.
     * @param l the listener to be removed
     */
    public void removeMouseListener(TrayItemMouseListener l) {
        MOUSE_LISTENERS.remove(l);
    }

    /**
     * Returns the list of the menu display listeners.
     */
    public List<Runnable> getMenuDisplayListeners() {
        return MENU_DISPLAY_LISTENERS;
    }
    
    /**
     * Adds the menu display listener to the tray item.
     * @param r the listener to be added
     */
    public void addMenuDisplayListener(Runnable r) {
        MENU_DISPLAY_LISTENERS.add(r);
    }
    
    /**
     * Removes the menu display listener from the tray item.
     * @param r the listener to be removed
     */
    public void removeMenuDisplayListener(Runnable r) {
        MENU_DISPLAY_LISTENERS.remove(r);
    }
    
    /**
     * Returns the current tooltip text.
     */
    public String getTooltip() {
        return tooltip;
    }

    /**
     * Returns whether the tray item is visible.
     */
    public boolean isVisible() {
        return visible && imageData != null;
    }

    /**
     * Sets the specified image for this tray item.
     * @param image the non-null image to be used
     * @throws NullPointerException if <code>image</code> is <code>null</code>
     */
    public void setImage(RenderedImage image) {
        setImage(JTrayContainer.createImageData(image));
    }

    /**
     * Sets the specified image for this tray item.
     * @param image the non-null bytes of the image to be used
     * @throws NullPointerException if <code>image</code> is <code>null</code>
     */
    public void setImage(byte[] imageData) {
        checkState();
        if (imageData == null) throw new NullPointerException("Image can't be null");
        NATIVE_TRAY.setTrayItemImage(getKey(), imageData);
        this.imageData = imageData;
        this.visible = true;
    }

    /**
     * Sets the tooltip string for this tray item.
     * @param text the string for the tooltip; if the value is
     * <code>null</code> no tooltip is shown
     */
    public void setTooltip(String text) {
        checkState();
        NATIVE_TRAY.setTrayItemTooltip(getKey(), text);
        this.tooltip = text;
    }

    /**
     * Sets the tray item to visible or invisible.
     * @param visible if true, the tray item will be visible; otherwise it will be invisible
     */
    public void setVisible(boolean visible) {
        checkState();
        if (visible && imageData == null) throw new NullPointerException("Tray item can't be visible without image");
        NATIVE_TRAY.setTrayItemVisible(getKey(), visible);
        this.visible = visible;
    }

    /**
     * Displays an informative balloon tooltip from the tray item.
     * @param title the non-null title that is displayed above the message
     * @param message the non-null message text
     * @throws NullPointerException if <code>title</code>
     * or <code>message</code> are <code>null</code>
     */
    public void showMessage(String title, String message) {
        showMessage(title, message, null, null);
    }
    
    /**
     * Displays a balloon tooltip message from the tray item.
     * @param title the non-null title that is displayed above the message
     * @param message the non-null message text
     * @param type the type of the message or null (default: informative)
     * @throws NullPointerException if <code>title</code>
     * or <code>message</code> are <code>null</code>
     */
    public void showMessage(String title, String message, TrayMessageType type) {
        showMessage(title, message, type, null);
    }
    
    /**
     * Displays a balloon tooltip message from the tray item.
     * @param title the non-null title that is displayed above the message
     * @param message the non-null message text
     * @param type the type of the message or null (default: informative)
     * @param callback the callback is called when the user clicks on the message
     * @throws NullPointerException if <code>title</code> or <code>message</code> are <code>null</code>
     */
    public void showMessage(String title, String message, TrayMessageType type, Runnable callback) {
        checkState();
        if (title == null) throw new NullPointerException("Title can not be null");
        if (message == null) throw new NullPointerException("Message can not be null");
        if (type == null) type = TrayMessageType.INFO;
        int msgKey = -1;
        if (callback != null) msgKey = getTrayContainer().addMessageCallback(callback);
        NATIVE_TRAY.showMessage(getKey(), msgKey, title, message, type);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    /**
     * This method is used by {@link JTrayContainer} when it is disposed.
     * When the tray container is disposing it disposes and removes every
     * tray object so it's unnecessary to indicate the tray item removing.
     * @param outer if false, tray container doesn't remove the item object
     */
    boolean dispose(boolean outer) {
        if (!super.dispose()) return false;
        NATIVE_TRAY.disposeTrayItem(getKey());
        if (outer) {
            getTrayContainer().getTrayItems().remove(this);
            JTray.onDispose();
        }
        visible = false;
        tooltip = null;
        imageData = null;
        synchronized (MOUSE_LISTENERS) {
            MOUSE_LISTENERS.clear();
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void checkState() {
        if (isDisposed()) throw new IllegalStateException("Tray item is disposed");
    }
    
}
