package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JTrayItem {

    private final TrayItemData DATA;

    private final List<TrayItemMouseListener> MOUSE_LISTENERS = Collections.synchronizedList(new ArrayList<TrayItemMouseListener>());

    JTrayItem(TrayItemData data) {
        DATA = data;
    }

    int getKey() {
        return DATA.KEY;
    }
    
    public List<TrayItemMouseListener> getMouseListeners() {
        return MOUSE_LISTENERS;
    }
    
    public void addMouseListener(TrayItemMouseListener l) {
        MOUSE_LISTENERS.add(l);
    }
    
    public void removeMouseListener(TrayItemMouseListener l) {
        MOUSE_LISTENERS.remove(l);
    }
    
    public String getTooltip() {
        return DATA.tooltip;
    }

    public boolean isVisible() {
        return DATA.visible && DATA.imageData != null;
    }
    
    public void setImage(RenderedImage image) {
        setImage(JTrayContainer.createImageData(image));
    }

    public void setImage(byte[] imageData) {
        if (imageData == null) throw new NullPointerException("Image can't be null");
        NATIVE_TRAY.setImage(DATA.KEY, imageData);
        DATA.imageData = imageData;
    }

    public void setTooltip(String text) {
        NATIVE_TRAY.setTooltip(DATA.KEY, text);
        DATA.tooltip = text;
    }

    public void setVisible(boolean visible) {
        if (visible && DATA.imageData == null) throw new NullPointerException("Tray item can't be visible without image");
        NATIVE_TRAY.setVisible(DATA.KEY, visible);
        DATA.visible = visible;
    }
    
}
