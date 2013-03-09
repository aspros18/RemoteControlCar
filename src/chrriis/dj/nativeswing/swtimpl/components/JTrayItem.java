package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.getTrayContainer;
import java.awt.image.RenderedImage;
import java.util.Collections;
import java.util.List;

public class JTrayItem extends JTrayObject {

    private String tooltip;
    private byte[] imageData;
    private boolean visible;
    
    private final List<TrayItemMouseListener> MOUSE_LISTENERS = Collections.synchronizedList(new JTrayListenerList<TrayItemMouseListener>(this));
    
    JTrayItem(int key, String tooltip, byte[] imageData) {
        super(key);
        this.tooltip = tooltip;
        this.imageData = imageData;
        this.visible = imageData != null;
        getTrayContainer().addTrayItem(this);
    }
    
    public JTrayMenu getTrayMenu() {
        return getTrayContainer().findTrayMenu(this);
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
        NATIVE_TRAY.setTrayItemImage(getKey(), imageData);
        this.imageData = imageData;
    }

    public void setTooltip(String text) {
        checkState();
        NATIVE_TRAY.setTrayItemTooltip(getKey(), text);
        this.tooltip = text;
    }

    public void setVisible(boolean visible) {
        checkState();
        if (visible && imageData == null) throw new NullPointerException("Tray item can't be visible without image");
        NATIVE_TRAY.setTrayItemVisible(getKey(), visible);
        this.visible = visible;
    }

    public void showMessage(String title, String message) {
        showMessage(title, message, null, null);
    }
    
    public void showMessage(String title, String message, TrayMessageType type) {
        showMessage(title, message, type, null);
    }
    
    public void showMessage(String title, String message, TrayMessageType type, Runnable callback) {
        checkState();
        if (title == null) throw new NullPointerException("Title can not be null");
        if (message == null) throw new NullPointerException("Message can not be null");
        if (type == null) type = TrayMessageType.INFO;
        int msgKey = -1;
        if (callback != null) msgKey = getTrayContainer().addMessageCallback(callback);
        NATIVE_TRAY.showMessage(getKey(), msgKey, title, message, type);
    }
    
    @Override
    public void dispose() {
        dispose(true);
    }
    
    void dispose(boolean outer) {
        if (isDisposed()) return;
        NATIVE_TRAY.disposeTrayItem(getKey());
        if (outer) getTrayContainer().removeTrayItem(this);
        super.dispose();
        visible = false;
        tooltip = null;
        imageData = null;
        synchronized (MOUSE_LISTENERS) {
            MOUSE_LISTENERS.clear();
        }
    }
    
    @Override
    void checkState() {
        if (isDisposed()) throw new IllegalStateException("Tray item is disposed");
    }
    
}
