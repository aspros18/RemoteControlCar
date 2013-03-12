/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TrayItem;

/**
 * Methods of the tray item.
 * @author Zolt&aacute;n Farkas
 */
class NativeTrayItem implements NativeTrayObject {

    private final int KEY;
    
    private final TrayItem TRAY_ITEM;

    private NativeTrayMenu menu;
    
    private boolean visible;
    
    /**
     * Initializer.
     * @param trayItem the SWT tray item
     * @param img the initial image or null
     * @param key the key of the tray item
     */
    public NativeTrayItem(TrayItem trayItem, Image img, int key) {
        TRAY_ITEM = trayItem;
        KEY = key;
        if (img != null) setImage(img);
        else setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getKey() {
        return KEY;
    }

    /**
     * Returns whether tray item is visible or invisible.
     * The tray item is invisible as long as the image is not set.
     * @return true if it's visible; otherwise false
     */
    public boolean isVisible() {
        return visible && TRAY_ITEM.getImage() != null;
    }

    /**
     * Changes the tray item's visibility.
     * If the property equals with the specified parameter, it does nothing.
     * @param visible true to make it visible; otherwise false
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
        boolean v = isVisible();
        if (v != TRAY_ITEM.getVisible()) {
            TRAY_ITEM.setVisible(v);
        }
    }

    /**
     * Sets the specified image for the tray item.
     * The tray item will be visible too.
     * @param img the new non-null image
     */
    public void setImage(Image img) {
        TRAY_ITEM.setImage(img);
        setVisible(true); // it's automatic on GTK; maybe on Windows too
    }
    
    public TrayItem getTrayItem() {
        return TRAY_ITEM;
    }

    public NativeTrayMenu getNativeTrayMenu() {
        return menu;
    }

    public void setNativeTrayMenu(NativeTrayMenu menu) {
        this.menu = menu;
    }
    
}
