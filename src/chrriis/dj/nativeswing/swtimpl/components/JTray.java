package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import java.awt.image.RenderedImage;

public class JTray {

    final static INativeTray NATIVE_TRAY = NativeCoreObjectFactory.create(INativeTray.class, "chrriis.dj.nativeswing.swtimpl.components.core.NativeTray", new Class<?>[0], new Object[0]);
    
    public static JTrayItem createTrayItem(RenderedImage image, String tooltip) {
        return createTrayItem(JTrayContainer.createImageData(image), tooltip);
    }

    public static JTrayItem createTrayItem(byte[] imageData, String tooltip) {
        int key = NATIVE_TRAY.createTrayItem(imageData, tooltip);
        return JTrayContainer.createTrayItem(key, imageData, tooltip);
    }

    public static void dispose() {
        JTrayContainer.dispose();
    }

}
