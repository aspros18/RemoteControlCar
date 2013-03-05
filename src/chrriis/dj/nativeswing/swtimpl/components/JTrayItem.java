package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.createImageData;
import java.awt.image.RenderedImage;

public class JTrayItem {

    private final int KEY;

    public JTrayItem(int key) {
        KEY = key;
    }

    public void setImage(RenderedImage image) {
        setImage(createImageData(image));
    }

    public void setImage(byte[] imageData) {
        NATIVE_TRAY.setImage(KEY, imageData);
    }

    public void setTooltip(String text) {
        NATIVE_TRAY.setTooltip(KEY, text);
    }

}
