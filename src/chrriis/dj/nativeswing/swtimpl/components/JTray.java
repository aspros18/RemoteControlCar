package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class JTray {

    final static INativeTray NATIVE_TRAY = NativeCoreObjectFactory.create(INativeTray.class, "chrriis.dj.nativeswing.swtimpl.components.core.NativeTray", new Class<?>[0], new Object[0]);

    public static JTrayItem createTrayItem(RenderedImage image, String tooltip) {
        return createTrayItem(createImageData(image), tooltip);
    }

    public static JTrayItem createTrayItem(byte[] imageData, String tooltip) {
        int key = NATIVE_TRAY.createTrayItem(imageData, tooltip);
        return new JTrayItem(new TrayItemData(key, tooltip));
    }

    public static void dispose() {
        NATIVE_TRAY.dispose();
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

}
