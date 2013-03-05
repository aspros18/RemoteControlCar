package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class JTray {

    private static INativeTray nativeFileDialog = NativeCoreObjectFactory.create(INativeTray.class, "chrriis.dj.nativeswing.swtimpl.components.core.NativeTray", new Class<?>[0], new Object[0]);

    public static JTrayItem createTrayItem(RenderedImage icon, String tooltip) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(icon, "png", baos);
            baos.flush();
            byte[] data = baos.toByteArray();
            baos.close();
            return createTrayItem(data, tooltip);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static JTrayItem createTrayItem(byte[] iconData, String tooltip) {
        int key = nativeFileDialog.createTrayItem(iconData, tooltip);
        return new JTrayItem(key);
    }

}
