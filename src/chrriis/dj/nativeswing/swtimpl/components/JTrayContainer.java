package chrriis.dj.nativeswing.swtimpl.components;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

public final class JTrayContainer {
    
    private static final JTrayContainer OBJ = new JTrayContainer();
    
    private final Set<JTrayItem> TRAY_ITEMS = Collections.synchronizedSet(new HashSet<JTrayItem>());
    
    private final Set<JTrayMenu> TRAY_MENUS = Collections.synchronizedSet(new HashSet<JTrayMenu>());
    
    private final Map<Integer, Runnable> MSG_CALLBACKS = Collections.synchronizedMap(new HashMap<Integer, Runnable>());
    
    public static JTrayContainer getInstance(double passkey) {
        if (JTray.PASSKEY != passkey) throw new IllegalAccessError();
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
    
    void addTrayItem(JTrayItem item) {
        TRAY_ITEMS.add(item);
    }

    void removeTrayItem(JTrayItem item) {
        TRAY_ITEMS.remove(item);
    }
    
    void addTrayMenu(JTrayMenu menu) {
        TRAY_MENUS.add(menu);
    }

    void removeTrayMenu(JTrayMenu menu) {
        TRAY_MENUS.remove(menu);
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
            item.dispose(false);
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
    
    public int addMessageCallback(Runnable callback) {
        if (callback == null) return -1;
        synchronized (MSG_CALLBACKS) {
            int key = getMessageKey(callback);
            if (key == -1) {
                key = getNextMessageKey();
                MSG_CALLBACKS.put(key, callback);
            }
            return key;
        }
    }

    public void removeMessageCallback(int key) {
        synchronized (MSG_CALLBACKS) {
            MSG_CALLBACKS.remove(key);
        }
    }
    
    private int getMessageKey(Runnable callback) {
        int key = -1;
        Iterator<Map.Entry<Integer, Runnable>> it = MSG_CALLBACKS.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Runnable> e = it.next();
            if (e.getValue() == callback) {
                key = e.getKey();
                break;
            }
        }
        return key;
    }
    
    private int getNextMessageKey() {
        int i = -1;
        boolean contains;
        do {
            contains = MSG_CALLBACKS.containsKey(++i);
        } while (contains);
        return i;
    }

}
