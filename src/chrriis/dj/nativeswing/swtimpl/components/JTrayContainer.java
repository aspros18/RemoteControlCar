package chrriis.dj.nativeswing.swtimpl.components;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

public final class JTrayContainer {
    
    private static final JTrayContainer OBJ = new JTrayContainer();
    
    private final Set<JTrayItem> TRAY_ITEMS = Collections.synchronizedSet(new HashSet<JTrayItem>());
    
    private final Set<JTrayMenu> TRAY_MENUS = Collections.synchronizedSet(new HashSet<JTrayMenu>());
    
    private final Map<Integer, Runnable> MSG_CALLBACKS = Collections.synchronizedMap(new HashMap<Integer, Runnable>());
    
    public static JTrayContainer getInstance() {
        return OBJ;
    }
    
    static byte[] createImageData(RenderedImage img) {
        if (img == null) return null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            baos.flush();
            byte[] data = baos.toByteArray();
            baos.close();
            return data;
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    JTrayItem createTrayItem(int key, byte[] imageData, String tooltip) {
        JTrayItem item = new JTrayItem(key, tooltip, imageData);
        TRAY_ITEMS.add(item);
        return item;
    }

    void addTrayMenu(JTrayMenu menu) {
        TRAY_MENUS.add(menu);
    }

    void removeTrayMenu(JTrayMenu menu) {
        TRAY_MENUS.add(menu);
    }
    
    JTrayMenu findTrayMenu(JTrayItem item) {
        if (item == null) return null;
        for (JTrayMenu menu : TRAY_MENUS) {
            JTrayItem i = menu.getTrayItem();
            if (i == null) continue;
            if (i == item) return menu;
        }
        return null;
    }
    
    void dispose() {
        MSG_CALLBACKS.clear();
        for (JTrayItem item : TRAY_ITEMS) {
            item.dispose();
        }
        TRAY_ITEMS.clear();
        for (JTrayMenu menu : TRAY_MENUS) {
            menu.dispose(false);
        }
        TRAY_MENUS.clear();
    }
    
    public JTrayItem getTrayItem(int key) {
        for (JTrayItem item : TRAY_ITEMS) {
            if (item.getKey() == key) return item;
        }
        return null;
    }
    
    public Runnable getMessageCallback(int key) {
        return MSG_CALLBACKS.get(key);
    }
    
    public void setMessageCallback(int key, Runnable callback) {
        if (callback != null) MSG_CALLBACKS.put(key, callback);
        else MSG_CALLBACKS.remove(key);
    }
    
}
