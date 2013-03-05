package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class JTrayItem {

    private final int KEY;

    private String tooltip;
    private byte[] imageData;
    private boolean visible;

    private final Timer TIMER = new Timer();
    
    private final List<TrayItemMouseListener> MOUSE_LISTENERS = Collections.synchronizedList(new ArrayList<TrayItemMouseListener>());

    private TimerTask task;
    
    JTrayItem(int key, String tooltip, byte[] imageData) {
        this.KEY = key;
        this.tooltip = tooltip;
        this.imageData = imageData;
        this.visible = imageData != null;
        setBlockerThread();
    }

    int getKey() {
        return KEY;
    }
    
    private void setBlockerThread() {
        if (isVisible()) {
            if (task != null) task.cancel();
            TIMER.schedule(task = new TimerTask() {

                @Override
                public void run() {
                    ;
                }

            }, 1000, 1000);
        }
        else {
            if (task != null) task.cancel();
        }
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
        if (imageData == null) throw new NullPointerException("Image can't be null");
        NATIVE_TRAY.setImage(KEY, imageData);
        this.imageData = imageData;
    }

    public void setTooltip(String text) {
        NATIVE_TRAY.setTooltip(KEY, text);
        this.tooltip = text;
    }

    public void setVisible(boolean visible) {
        if (visible && imageData == null) throw new NullPointerException("Tray item can't be visible without image");
        NATIVE_TRAY.setVisible(KEY, visible);
        this.visible = visible;
        setBlockerThread();
    }
    
}
