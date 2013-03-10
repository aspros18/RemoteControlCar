package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import java.awt.image.RenderedImage;

class JMenuCommonItem<T> extends JMenuActiveItem<T> {

    public JMenuCommonItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
    }
    
    public void setImage(RenderedImage img) {
        setImage(JTrayContainer.createImageData(img));
    }
    
    public void setImage(byte[] imageData) {
        checkState();
        NATIVE_TRAY.setMenuItemImage(getKey(), imageData);
    }
    
}
