package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JTrayItem {

    private final int KEY;
    
    private String tooltip;
    private byte[] imageData;
    private boolean visible, disposed;
    
    private final List<TrayItemMouseListener> MOUSE_LISTENERS = Collections.synchronizedList(new ArrayList<TrayItemMouseListener>());
    
    JTrayItem(int key, String tooltip, byte[] imageData) {
        this.KEY = key;
        this.tooltip = tooltip;
        this.imageData = imageData;
        this.visible = imageData != null;
        this.disposed = false;
    }

    int getKey() {
        return KEY;
    }
    
    public JTrayMenu getTrayMenu() {
        return JTrayContainer.findTrayMenu(this);
    }
    
    public List<TrayItemMouseListener> getMouseListeners() {
        return MOUSE_LISTENERS;
    }
    
    public void addMouseListener(TrayItemMouseListener l) {
        if (disposed) throw new IllegalStateException("Tray item is disposed");
        MOUSE_LISTENERS.add(l);
    }
    
    public void removeMouseListener(TrayItemMouseListener l) {
        MOUSE_LISTENERS.remove(l);
    }
    
    public String getTooltip() {
        return tooltip;
    }

    public boolean isVisible() {
        return visible && imageData != null;
    }

    public void setImage(RenderedImage image) {
        setImage(JTrayContainer.createImageData(image));
    }

    public void setImage(byte[] imageData) {
        checkState();
        if (imageData == null) throw new NullPointerException("Image can't be null");
        NATIVE_TRAY.setImage(KEY, imageData);
        this.imageData = imageData;
    }

    public void setTooltip(String text) {
        checkState();
        NATIVE_TRAY.setTooltip(KEY, text);
        this.tooltip = text;
    }

    public void setVisible(boolean visible) {
        checkState();
        if (visible && imageData == null) throw new NullPointerException("Tray item can't be visible without image");
        NATIVE_TRAY.setVisible(KEY, visible);
        this.visible = visible;
    }

    public boolean isDisposed() {
        return disposed;
    }
    
    public void dispose() {
        if (disposed) return;
        NATIVE_TRAY.dispose(KEY);
        disposed = true;
        visible = false;
        tooltip = null;
        imageData = null;
        synchronized (MOUSE_LISTENERS) {
            MOUSE_LISTENERS.clear();
        }
    }
    
    private void checkState() {
        if (disposed) throw new IllegalStateException("Tray item is disposed");
    }
    
}
