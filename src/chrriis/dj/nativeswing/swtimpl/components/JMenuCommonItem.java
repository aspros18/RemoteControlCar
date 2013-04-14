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

/**
 * Common methods of {@link JMenuItem} and {@link JMenuDropDownItem}.
 * A common menu item has no selection state but it can have an image.
 * @author Zolt&aacute;n Farkas
 */
class JMenuCommonItem<T> extends JMenuActiveItem<T> {
    
    /**
     * Constructs a menu item.
     * @see JMenuActiveItem
     */
    JMenuCommonItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
    }
    
    /**
     * Sets the specified image to the menu item.
     * The image will appear only if the graphical system supports it.
     * @param img the image to be set
     */
    public void setImage(RenderedImage img) {
        setImage(JTrayContainer.createImageData(img));
    }
    
    /**
     * Sets the specified image to the menu item.
     * The image will appear only if the graphical system supports it.
     * @param img the bytes of the image to be set
     */
    public void setImage(byte[] imageData) {
        checkState();
        NATIVE_TRAY.setMenuItemImage(getKey(), imageData);
    }
    
}
