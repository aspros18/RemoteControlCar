package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import java.awt.image.RenderedImage;

public class JMenuItem extends JMenuActiveItem<MenuItemActionListener> {

    JMenuItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
        getTrayContainer().getMenuItems().add(this);
    }

    public void setImage(RenderedImage img) {
        setImage(JTrayContainer.createImageData(img));
    }
    
    public void setImage(byte[] imageData) {
        NATIVE_TRAY.setMenuItemImage(getKey(), imageData);
    }
    
    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    boolean dispose(boolean outer) {
        if (!super.dispose()) return false;
        if (outer) getTrayContainer().getMenuItems().remove(this);
        return true;
    }
    
}
