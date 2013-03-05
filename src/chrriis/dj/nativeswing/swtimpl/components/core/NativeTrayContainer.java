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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TrayItem;

final class NativeTrayContainer {

    public static class TrayItemData {
        
        final TrayItem ITEM;
        final Menu MENU;
        final Listener LISTENER_MENU;

        TrayItemData(TrayItem item, Menu menu, Listener listenerMenu) {
            ITEM = item;
            MENU = menu;
            LISTENER_MENU = listenerMenu;
        }
        
    }
    
    private static final NativeTrayContainer OBJ = new NativeTrayContainer();

    private final Set<Image> IMAGES = Collections.synchronizedSet(new HashSet<Image>());

    private final List<TrayItemData> TRAY_ITEMS = Collections.synchronizedList(new ArrayList<TrayItemData>());

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
    
    public List<TrayItemData> getTrayItemDatas() {
        return TRAY_ITEMS;
    }
    
    public Image createImage(Display display, byte[] data) {
        BufferedInputStream inputStreamReader = new BufferedInputStream(new ByteArrayInputStream(data));
        ImageData imageData = new ImageData(inputStreamReader);
        Image image = new Image(display, imageData);
        IMAGES.add(image);
        return image;
    }

    public void dispose() {
        for (TrayItemData data : TRAY_ITEMS) {
            data.ITEM.dispose();
        }
        TRAY_ITEMS.clear();
        for (Image img : IMAGES) {
            img.dispose();
        }
        IMAGES.clear();
    }

}
