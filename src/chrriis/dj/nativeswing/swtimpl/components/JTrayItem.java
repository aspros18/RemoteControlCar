package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.getTrayContainer;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

abstract class JTrayObject {
    
    abstract void checkState();
    
}

class TrayObjectList<T> extends ArrayList<T> {

    private final JTrayObject OWNER;
    
    public TrayObjectList(JTrayObject owner) {
        OWNER = owner;
    }

    private void checkState() {
        if (OWNER != null) OWNER.checkState();
    }
    
    @Override
    public boolean add(T e) {
        checkState();
        return super.add(e);
    }

    @Override
    public void add(int index, T element) {
        checkState();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        checkState();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        checkState();
        return super.addAll(index, c);
    }
    
}

public class JTrayItem extends JTrayObject {

    private final int KEY;
    
    private String tooltip;
    private byte[] imageData;
    private boolean visible, disposed;
    
    private final List<TrayItemMouseListener> MOUSE_LISTENERS = Collections.synchronizedList(new TrayObjectList<TrayItemMouseListener>(this));
    
    JTrayItem(int key, String tooltip, byte[] imageData) {
        this.KEY = key;
        this.tooltip = tooltip;
        this.imageData = imageData;
        this.visible = imageData != null;
        this.disposed = false;
        getTrayContainer().addTrayItem(this);
    }

    int getKey() {
        return KEY;
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
        NATIVE_TRAY.setTrayItemImage(KEY, imageData);
        this.imageData = imageData;
    }

    public void setTooltip(String text) {
        checkState();
        NATIVE_TRAY.setTrayItemTooltip(KEY, text);
        this.tooltip = text;
    }

    public void setVisible(boolean visible) {
        checkState();
        if (visible && imageData == null) throw new NullPointerException("Tray item can't be visible without image");
        NATIVE_TRAY.setTrayItemVisible(KEY, visible);
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
        NATIVE_TRAY.showMessage(KEY, msgKey, title, message, type);
    }
    
    public boolean isDisposed() {
        return disposed;
    }
    
    public void dispose() {
        dispose(true);
    }
    
    void dispose(boolean outer) {
        if (disposed) return;
        NATIVE_TRAY.disposeTrayItem(KEY);
        if (outer) getTrayContainer().removeTrayItem(this);
        disposed = true;
        visible = false;
        tooltip = null;
        imageData = null;
        synchronized (MOUSE_LISTENERS) {
            MOUSE_LISTENERS.clear();
        }
    }
    
    @Override
    void checkState() {
        if (disposed) throw new IllegalStateException("Tray item is disposed");
    }
    
}
