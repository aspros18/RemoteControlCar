/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components.core;

import chrriis.common.RunnableReturn;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

/**
 * Native side container that stores native components.
 * @author Zolt&aacute;n Farkas
 */
final class NativeTrayContainer {

    private static final NativeTrayContainer OBJ = new NativeTrayContainer();

    private final Set<Image> IMAGES = Collections.synchronizedSet(new HashSet<Image>());

    private final Set<NativeTrayItem> TRAY_ITEMS = Collections.synchronizedSet(new HashSet<NativeTrayItem>());

    private final Set<NativeTrayBaseMenu> TRAY_MENUS = Collections.synchronizedSet(new HashSet<NativeTrayBaseMenu>());
    
    private final Set<NativeMenuItem> MENU_ITEMS = Collections.synchronizedSet(new HashSet<NativeMenuItem>());

    private Shell shell;

    /**
     * Inheritance and instantiation are disabled.
     */
    private NativeTrayContainer() {
    }

    /**
     * Returns an instance of the <code>NativeTrayContainer</code>.
     */
    public static NativeTrayContainer getInstance() {
        return OBJ;
    }

    /**
     * Safe equals.
     * @return true if <code>i1</code> and <code>i2</code> are equals
     * or both of them are <code>null</code>; otherwise false
     */
    public static boolean equals(Object i1, Object i2) {
        return !(i1 == null ^ i2 == null) && (i1 == null || i1.equals(i2));
    }
    
    /**
     * Returns an SWT <code>Shell</code> to create <code>Menu</code> and <code>ToolTip</code>.
     * The <code>Shell</code> is created at the first call.
     */
    public Shell getShell() {
        if (shell != null) return shell;
        final Display display = Display.getCurrent();
        if (display == null) return null;
        RunnableReturn<Shell> r = new RunnableReturn<Shell>() {

            @Override
            protected Shell createReturn() throws Exception {
                return new Shell(display);
            }
            
        };
        display.syncExec(r);
        return shell = r.getReturn();
    }
    
    public Set<NativeTrayItem> getNativeTrayItems() {
        return TRAY_ITEMS;
    }

    public Set<NativeTrayBaseMenu> getNativeTrayMenus() {
        return TRAY_MENUS;
    }

    public Set<NativeMenuItem> getNativeMenuItems() {
        return MENU_ITEMS;
    }
    
    /**
     * @see #getNextKey(Set)
     */
    public int getNextTrayItemKey() {
        return getNextKey(TRAY_ITEMS);
    }

    /**
     * @see #getNextKey(Set)
     */
    public int getNextTrayMenuKey() {
        return getNextKey(TRAY_MENUS);
    }
    
    /**
     * @see #getNextKey(Set)
     */
    public int getNextMenuItemKey() {
        return getNextKey(MENU_ITEMS);
    }
    
    /**
     * Returns the first free key in the specified <code>Set</code>.
     * @param s the <code>Set</code>
     */
    private static int getNextKey(Set<? extends NativeTrayObject> s) {
        int key = -1;
        boolean e;
        do {
            key++;
            e = false;
            for (NativeTrayObject obj : s) {
                if (obj.getKey() == key) {
                    e = true;
                    break;
                }
            }
        } while (e);
        return key;
    }
    
    /**
     * @see #getNativeTrayObject(Set, Integer)
     */
    public TrayItem getTrayItem(int key) {
        NativeTrayItem o = getNativeTrayItem(key);
        return o == null ? null : o.getTrayItem();
    }

    /**
     * @see #getNativeTrayObject(Set, Integer)
     */
    public Menu getTrayMenu(int key) {
        NativeTrayBaseMenu o = getNativeTrayMenu(key);
        return o == null ? null : o.getMenu();
    }
    
    /**
     * @see #getNativeTrayObject(Set, Integer)
     */
    public MenuItem getMenuItem(int key) {
        NativeMenuItem o = getNativeMenuItem(key);
        return o == null ? null : o.getMenuItem();
    }
    
    /**
     * @see #getNativeTrayObject(Set, Integer)
     */
    public NativeTrayItem getNativeTrayItem(Integer key) {
        return getNativeTrayObject(TRAY_ITEMS, key);
    }
    
    /**
     * @see #getNativeTrayObject(Set, Integer)
     */
    public NativeTrayBaseMenu getNativeTrayMenu(Integer key) {
        return getNativeTrayObject(TRAY_MENUS, key);
    }
    
    /**
     * @see #getNativeTrayObject(Set, Integer)
     */
    public NativeMenuItem getNativeMenuItem(Integer key) {
        return getNativeTrayObject(MENU_ITEMS, key);
    }
    
    /**
     * Finds the tray object in the specified <code>Set</code>.
     * @param s the <code>Set</code>
     * @param key the key of the object
     * @return tray object or <code>null</code>
     */
    private static <T extends NativeTrayObject> T getNativeTrayObject(Set<T> s, Integer key) {
        if (key == null) return null;
        for (T obj : s) {
            if (obj.getKey() == key) return obj;
        }
        return null;
    }
    
    /**
     * Creates an image from the byte array.
     * @param display the SWT <code>Display</code>
     * @param data the byte array
     * @see #removeImage(Image) 
     * @see #dispose()
     */
    public Image createImage(Display display, byte[] data) {
        try {
            if (display == null || data == null) return null;
            BufferedInputStream inputStreamReader = new BufferedInputStream(new ByteArrayInputStream(data));
            ImageData imageData = new ImageData(inputStreamReader);
            Image image = new Image(display, imageData);
            IMAGES.add(image);
            return image;
        }
        catch (Exception ex) {
            return null;
        }
    }

    /**
     * Removes and disposes the specified image.
     */
    public void removeImage(Image image) {
        if (image != null) {
            image.dispose();
            IMAGES.remove(image);
        }
    }
    
    /**
     * Disposes tray items, tray menus, menu items and images.
     */
    public void dispose() {
        for (NativeTrayItem item : TRAY_ITEMS) {
            item.getTrayItem().dispose();
        }
        TRAY_ITEMS.clear();
        for (NativeTrayBaseMenu menu : TRAY_MENUS) {
            menu.getMenu().dispose();
        }
        TRAY_MENUS.clear();
        for (NativeMenuItem item : MENU_ITEMS) {
            item.getMenuItem().dispose();
        }
        MENU_ITEMS.clear();
        for (Image img : IMAGES) {
            img.dispose();
        }
        IMAGES.clear();
    }

}
