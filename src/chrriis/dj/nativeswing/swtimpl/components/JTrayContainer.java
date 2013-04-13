/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
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

/**
 * A container that stores tray objects.
 * Tray objects can communicate to each other by
 * registering themselves to the tray container.
 * This class should not used outside from the API.
 * @author Zolt&aacute;n Farkas
 */
public final class JTrayContainer {
    
    /**
     * An instance of the {@link JTrayContainer}.
     */
    private static final JTrayContainer OBJ = new JTrayContainer();
    
    /**
     * A Set that contains the tray items.
     */
    private final Set<JTrayItem> TRAY_ITEMS = Collections.synchronizedSet(new HashSet<JTrayItem>());
    
    /**
     * A Set that contains the tray menus.
     */
    private final Set<JTrayMenu> TRAY_MENUS = Collections.synchronizedSet(new HashSet<JTrayMenu>());
    
    /**
     * A Set that contains the menu items (every types).
     */
    private final Set<JMenuBaseItem> MENU_ITEMS = Collections.synchronizedSet(new HashSet<JMenuBaseItem>());
    
    /**
     * A Map that contains the callbacks of the tray item's balloon messages.
     */
    private final Map<Integer, Runnable> MSG_CALLBACKS = Collections.synchronizedMap(new HashMap<Integer, Runnable>());
    
    /**
     * Returns an instance of the <code>JTrayContainer</code>.
     * Used for event handling and native pairing.
     */
    public static JTrayContainer getInstance(double passkey) {
        if (JTray.PASSKEY != passkey) throw new IllegalAccessError();
        return OBJ;
    }
    
    /**
     * Creates a byte array from the {@link RenderedImage}.
     * Used by {@link JTray}, {@link JTrayItem}, {@link JMenuCommonItem}
     */
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

    /**
     * Returns the Set that contains the tray items.
     * Used by constructor of {@link JTrayItem} and {@link JTrayItem#dispose(boolean)}.
     */
    Set<JTrayItem> getTrayItems() {
        return TRAY_ITEMS;
    }

    /**
     * Returns the Set that contains the tray menus.
     * Used by constructor of {@link JTrayMenu} and {@link JTrayMenu#dispose(boolean)}.
     */
    Set<JTrayMenu> getTrayMenus() {
        return TRAY_MENUS;
    }

    /**
     * Returns the Set that contains the menu items (every types).
     * Used by constructor of {@link JMenuBaseItem} and {@link JMenuBaseItem#dispose(boolean)}.
     */
    Set<JMenuBaseItem> getMenuItems() {
        return MENU_ITEMS;
    }
    
    /**
     * Returns the tray menu of the specified tray item.
     * Used by {@link JTrayItem#getTrayMenu()}
     * @param item a tray item
     * @return a tray menu or <code>null</code>
     */
    JTrayMenu findTrayMenu(JTrayItem item) {
        if (item == null) return null;
        for (JTrayMenu menu : TRAY_MENUS) {
            JTrayItem i = menu.getTrayItem();
            if (i == null) continue;
            if (i == item) return menu;
        }
        return null;
    }
    
    /**
     * Returns every menu items that belong to the specified menu.
     * Used by {@link JTrayBaseMenu#dispose()} to dispose the child items.
     */
    Set<JMenuBaseItem> findMenuItems(JTrayBaseMenu menu) {
        Set<JMenuBaseItem> items = new HashSet<JMenuBaseItem>();
        if (menu == null) return items;
        for (JMenuBaseItem item : MENU_ITEMS) {
            if (menu == item.getParent()) {
                items.add(item);
            }
        }
        return items;
    }
    
    /**
     * Disposes every tray object and clears the container.
     * Used by {@link JTray#dispose()}.
     */
    void dispose() {
        for (JTrayItem item : TRAY_ITEMS) {
            item.dispose(false);
        }
        TRAY_ITEMS.clear();
        
        for (JTrayMenu menu : TRAY_MENUS) {
            menu.dispose(false);
        }
        TRAY_MENUS.clear();
        
        for (JMenuBaseItem item : MENU_ITEMS) {
            item.dispose(false);
        }
        MENU_ITEMS.clear();
        
        MSG_CALLBACKS.clear();
    }
    
    /**
     * Returns the tray item whose key equals to the specified key.
     * Used by the native side to iterate {@link TrayItemMouseListener}s.
     * @return a tray item or <code>null</code>
     */
    public JTrayItem getTrayItem(int key) {
        return getTrayObject(TRAY_ITEMS, key);
    }

    /**
     * Returns the menu item whose key equals to the specified key.
     * Used by the native side to iterate {@link MenuItemActionListener}s and {@link MenuItemSelectionListener}s.
     * @return a menu item or <code>null</code>
     */
    public JMenuBaseItem getMenuItem(int key) {
        return getTrayObject(MENU_ITEMS, key);
    }
    
    /**
     * Returns the tray object whose key equals to the specified key.
     * Used by this container.
     * @param s the set to be iterated
     * @param key the specified key
     * @return a tray object or <code>null</code>
     * @see #getTrayItem(int)
     * @see #getMenuItem(int)
     */
    private <T extends JTrayObject> T getTrayObject(Set<T> s, int key) {
        for (T o : s) {
            if (o.getKey() == key) return o;
        }
        return null;
    }
    
    /**
     * Returns the callback whose key equals to the specified key.
     * Used by the native side to execute the callback.
     * @return a callback or <code>null</code>
     */
    public Runnable getMessageCallback(int key) {
        return MSG_CALLBACKS.get(key);
    }
    
    /**
     * Adds the specified callback to the container and returns the callback's key.
     * Used by {@link JTrayItem#showMessage(String, String, TrayMessageType, Runnable)}.
     * @return the key of the registered callback
     */
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

    /**
     * Removes the callback whose key equals to the specified key.
     * Used by the native side when a balloon tooltip message is disposed.
     */
    public void removeMessageCallback(int key) {
        synchronized (MSG_CALLBACKS) {
            MSG_CALLBACKS.remove(key);
        }
    }
    
    /**
     * Returns the key of the specified callback.
     * Used by this container.
     * @see #addMessageCallback(Runnable)
     * @return -1 if the callback is not in the container; otherwise the key
     */
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
    
    /**
     * Returns the first available key in the callback container.
     * Used by this container.
     * @see #addMessageCallback(Runnable)
     */
    private int getNextMessageKey() {
        int i = -1;
        boolean contains;
        do {
            contains = MSG_CALLBACKS.containsKey(++i);
        } while (contains);
        return i;
    }

}
