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
    
    private final Set<JMenuItem> MENU_ITEMS = Collections.synchronizedSet(new HashSet<JMenuItem>());
    
    private final Set<JMenuSelectionItem> MENU_SELECTION_ITEMS = Collections.synchronizedSet(new HashSet<JMenuSelectionItem>());
    
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

    Set<JTrayItem> getTrayItems() {
        return TRAY_ITEMS;
    }

    Set<JTrayMenu> getTrayMenus() {
        return TRAY_MENUS;
    }

    Set<JMenuItem> getMenuItems() {
        return MENU_ITEMS;
    }

    Set<JMenuSelectionItem> getMenuSelectionItems() {
        return MENU_SELECTION_ITEMS;
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
        for (JTrayItem item : TRAY_ITEMS) {
            item.dispose(false);
        }
        TRAY_ITEMS.clear();
        
        for (JTrayMenu menu : TRAY_MENUS) {
            menu.dispose(false);
        }
        TRAY_MENUS.clear();
        
        for (JMenuItem item : MENU_ITEMS) {
            item.dispose(false);
        }
        MENU_ITEMS.clear();
        
        for (JMenuSelectionItem item : MENU_SELECTION_ITEMS) {
            item.dispose(false);
        }
        MENU_SELECTION_ITEMS.clear();
        
        MSG_CALLBACKS.clear();
    }
    
    public JTrayItem getTrayItem(int key) {
        return getTrayObject(TRAY_ITEMS, key);
    }

    public JMenuItem getMenuItem(int key) {
        return getTrayObject(MENU_ITEMS, key);
    }
    
    public JMenuSelectionItem getMenuSelectionItem(int key) {
        return getTrayObject(MENU_SELECTION_ITEMS, key);
    }
    
    private <T extends JTrayObject> T getTrayObject(Set<T> s, int key) {
        for (T o : s) {
            if (o.getKey() == key) return o;
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
