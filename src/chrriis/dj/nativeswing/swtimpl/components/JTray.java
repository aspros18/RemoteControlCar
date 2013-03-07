package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.NativeInterfaceAdapter;
import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import java.awt.image.RenderedImage;
import java.util.Timer;

public class JTray {

    final static INativeTray NATIVE_TRAY = NativeCoreObjectFactory.create(INativeTray.class, "chrriis.dj.nativeswing.swtimpl.components.core.NativeTray", new Class<?>[0], new Object[0]);
    
    private static boolean disposed = false;
    
    private static Timer exitPrevent;
    
    static {
        NativeInterface.addNativeInterfaceListener(new NativeInterfaceAdapter() {

            @Override
            public void nativeInterfaceClosed() {
                dispose();
            }
            
        });
    }
    
    static JTrayContainer getTrayContainer() {
        return JTrayContainer.getInstance();
    }
    
    public static JTrayItem createTrayItem() {
        return createTrayItem((byte[]) null, null);
    }
    
    public static JTrayItem createTrayItem(RenderedImage image, String tooltip) {
        return createTrayItem(JTrayContainer.createImageData(image), tooltip);
    }

    public static JTrayItem createTrayItem(byte[] imageData, String tooltip) {
        checkState();
        if (exitPrevent == null) exitPrevent = new Timer();
        int key = NATIVE_TRAY.createTrayItem(imageData, tooltip);
        return getTrayContainer().createTrayItem(key, imageData, tooltip);
    }

    public static void dispose() {
        if (disposed) return;
        getTrayContainer().dispose();
        NATIVE_TRAY.dispose();
        disposed = true;
        if (exitPrevent != null) {
            exitPrevent.cancel();
            exitPrevent = null;
        }
    }

    private static void checkState() {
        if (disposed) throw new IllegalStateException("Tray is disposed");
    }
    
}
