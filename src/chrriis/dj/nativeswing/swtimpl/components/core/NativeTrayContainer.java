package chrriis.dj.nativeswing.swtimpl.components.core;

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
import org.eclipse.swt.widgets.TrayItem;

final class NativeTrayContainer {

    private static final NativeTrayContainer OBJ = new NativeTrayContainer();

    private final Set<Image> IMAGES = Collections.synchronizedSet(new HashSet<Image>());

    private final List<TrayItem> TRAY_ITEMS = Collections.synchronizedList(new ArrayList<TrayItem>());

    private NativeTrayContainer() {
    }

    public static NativeTrayContainer getInstance() {
        return OBJ;
    }

    public List<TrayItem> getTrayItems() {
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
        for (TrayItem item : TRAY_ITEMS) {
            item.dispose();
        }
        TRAY_ITEMS.clear();
        for (Image img : IMAGES) {
            img.dispose();
        }
        IMAGES.clear();
    }

}
