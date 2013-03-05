package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.NativeInterfaceAdapter;
import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import java.awt.image.RenderedImage;
import java.util.Timer;

public class JTray {

    final static INativeTray NATIVE_TRAY = NativeCoreObjectFactory.create(INativeTray.class, "chrriis.dj.nativeswing.swtimpl.components.core.NativeTray", new Class<?>[0], new Object[0]);
    
    private static Timer exitPrevent;
    
    static {
        NativeInterface.addNativeInterfaceListener(new NativeInterfaceAdapter() {

            @Override
            public void nativeInterfaceClosed() {
                dispose();
            }
            
        });
    }
    
    public static JTrayItem createTrayItem() {
        return createTrayItem((byte[]) null, null);
    }
    
    public static JTrayItem createTrayItem(RenderedImage image, String tooltip) {
        return createTrayItem(JTrayContainer.createImageData(image), tooltip);
    }

    public static JTrayItem createTrayItem(byte[] imageData, String tooltip) {
        if (exitPrevent == null) exitPrevent = new Timer();
        int key = NATIVE_TRAY.createTrayItem(imageData, tooltip);
        return JTrayContainer.createTrayItem(key, imageData, tooltip);
    }

    public static void dispose() {
        NATIVE_TRAY.dispose();
        JTrayContainer.clear();
        if (exitPrevent != null) {
            exitPrevent.cancel();
            exitPrevent = null;
        }
    }

}
