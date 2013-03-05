package chrriis.dj.nativeswing.swtimpl.components;

import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.createImageData;
import java.awt.image.RenderedImage;

public class JTrayItem {

    private final TrayItemData DATA;

    public JTrayItem(TrayItemData data) {
        DATA = data;
    }

    public String getTooltip() {
        return DATA.tooltip;
    }

    public void setImage(RenderedImage image) {
        setImage(createImageData(image));
    }

    public void setImage(byte[] imageData) {
        NATIVE_TRAY.setImage(DATA.KEY, imageData);
    }

    public void setTooltip(String text) {
        NATIVE_TRAY.setTooltip(DATA.KEY, text);
        DATA.tooltip = text;
    }

}
