package chrriis.test;

import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class JTest {

    private static INativeTest nativeFileDialog = NativeCoreObjectFactory.create(INativeTest.class, "chrriis.test.NativeTest", new Class<?>[0], new Object[0]);

    public static void test(RenderedImage icon) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(icon, "png", baos);
            baos.flush();
            byte[] data = baos.toByteArray();
            baos.close();
            test(data);
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void test(byte[] icon) {
        nativeFileDialog.test(icon);
    }

}
