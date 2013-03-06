package chrriis.dj.nativeswing.swtimpl.components.core;

import chrriis.common.RunnableReturn;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

final class NativeTrayContainer {

    private static final NativeTrayContainer OBJ = new NativeTrayContainer();

    private final Set<Image> IMAGES = Collections.synchronizedSet(new HashSet<Image>());

    private final List<NativeTrayItem> TRAY_ITEMS = Collections.synchronizedList(new ArrayList<NativeTrayItem>());

    private final List<NativeTrayMenu> TRAY_MENUS = Collections.synchronizedList(new ArrayList<NativeTrayMenu>());

    private Shell shell;

    private NativeTrayContainer() {
    }

    public static NativeTrayContainer getInstance() {
        return OBJ;
    }

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
    
    public List<NativeTrayItem> getNativeTrayItems() {
        return TRAY_ITEMS;
    }

    public List<NativeTrayMenu> getNativeTrayMenus() {
        return TRAY_MENUS;
    }
    
    public int getNextTrayItemKey() {
        return getNextKey(TRAY_ITEMS);
    }

    public int getNextTrayMenuKey() {
        return getNextKey(TRAY_MENUS);
    }
    
    private static int getNextKey(List<? extends NativeTrayObject> ls) {
        int key = -1;
        boolean e;
        do {
            key++;
            e = false;
            for (NativeTrayObject obj : ls) {
                if (obj.getKey() == key) {
                    e = true;
                    break;
                }
            }
        } while (e);
        return key;
    }
    
    public TrayItem getTrayItem(int key) {
        NativeTrayItem o = getNativeTrayItem(key);
        return o == null ? null : o.getTrayItem();
    }

    public Menu getTrayMenu(int key) {
        NativeTrayMenu o = getNativeTrayMenu(key);
        return o == null ? null : o.getMenu();
    }
    
    public NativeTrayItem getNativeTrayItem(int key) {
        return getNativeTrayObject(TRAY_ITEMS, key);
    }
    
    public NativeTrayMenu getNativeTrayMenu(int key) {
        return getNativeTrayObject(TRAY_MENUS, key);
    }
    
    private static <T extends NativeTrayObject> T getNativeTrayObject(List<T> ls, int key) {
        for (T obj : ls) {
            if (obj.getKey() == key) return obj;
        }
        return null;
    }
    
    public Image createImage(Display display, byte[] data) {
        BufferedInputStream inputStreamReader = new BufferedInputStream(new ByteArrayInputStream(data));
        ImageData imageData = new ImageData(inputStreamReader);
        Image image = new Image(display, imageData);
        IMAGES.add(image);
        return image;
    }

    public void removeImage(Image image) {
        if (image != null) {
            image.dispose();
            IMAGES.remove(image);
        }
    }
    
    public void dispose() {
        for (NativeTrayItem item : TRAY_ITEMS) {
            item.getTrayItem().dispose();
        }
        TRAY_ITEMS.clear();
        for (NativeTrayMenu menu : TRAY_MENUS) {
            menu.getMenu().dispose();
        }
        TRAY_MENUS.clear();
        for (Image img : IMAGES) {
            img.dispose();
        }
        IMAGES.clear();
    }

}
