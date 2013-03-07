package chrriis.dj.nativeswing.swtimpl.components;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;

public class JTrayContainer {
    
    private static final Set<JTrayItem> TRAY_ITEMS = Collections.synchronizedSet(new HashSet<JTrayItem>());
    
    private static final Set<JTrayMenu> TRAY_MENUS = Collections.synchronizedSet(new HashSet<JTrayMenu>());
    
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
    
    static JTrayItem createTrayItem(int key, byte[] imageData, String tooltip) {
        JTrayItem item = new JTrayItem(key, tooltip, imageData);
        TRAY_ITEMS.add(item);
        return item;
    }

    static void addTrayMenu(JTrayMenu menu) {
        TRAY_MENUS.add(menu);
    }

    static void removeTrayMenu(JTrayMenu menu) {
        TRAY_MENUS.add(menu);
    }
    
    static JTrayMenu findTrayMenu(JTrayItem item) {
        if (item == null) return null;
        for (JTrayMenu menu : TRAY_MENUS) {
            JTrayItem i = menu.getTrayItem();
            if (i == null) continue;
            if (i == item) return menu;
        }
        return null;
    }
    
    static void dispose() {
        for (JTrayItem item : TRAY_ITEMS) {
            if (!item.isDisposed()) item.dispose();
        }
        TRAY_ITEMS.clear();
        TRAY_MENUS.clear();
    }
    
    public static JTrayItem getTrayItem(int key) {
        for (JTrayItem item : TRAY_ITEMS) {
            if (item.getKey() == key) return item;
        }
        return null;
    }
    
}
