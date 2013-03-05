package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.createImageData;
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

    public void setImage(RenderedImage image) {
        setImage(createImageData(image));
    }

    public void setImage(byte[] imageData) {
        NATIVE_TRAY.setImage(DATA.KEY, imageData);
    }

    public void setTooltip(String text) {
        NATIVE_TRAY.setTooltip(DATA.KEY, text);
        DATA.tooltip = text;
    }

    public void setVisible(boolean visible) {
        NATIVE_TRAY.setVisible(DATA.KEY, visible);
    }
    
}
