/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.NativeInterfaceAdapter;
import chrriis.dj.nativeswing.swtimpl.internal.NativeCoreObjectFactory;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import java.awt.image.RenderedImage;
import java.util.Timer;

/**
 * This class represents the native system tray for a desktop.
 * On some platforms the system tray may not be present or may not
 * be supported, in this case {@link #createTrayItem(byte[], String)}
 * throws {@link UnsupportedOperationException}. To detect whether
 * the system tray is supported, use {@link #isSupported()}.
 * @author Zolt&aacute;n Farkas
 */
public final class JTray {

    /**
     * Passkey for the instance of the {@link JTrayContainer} class.
     * @see JTrayContainer#getInstance(double)
     */
    final static double PASSKEY = Math.random();
    
    /**
     * The object that executes the requests on the native side and sends back the UI events.
     */
    final static INativeTray NATIVE_TRAY = NativeCoreObjectFactory.create(INativeTray.class, "chrriis.dj.nativeswing.swtimpl.components.core.NativeTray", new Class<?>[] {Double.class}, new Object[] {PASSKEY});
    
    /**
     * Stores whether system tray is disposed.
     */
    private static boolean disposed = false;
    
    /**
     * Stores whether system tray is supported.
     */
    private static Boolean supported;
    
    /**
     * A timer that is not used for nothing but if it exists, it prevents {@link NativeInterface#runEventPump()} to be finished.
     * @see #onDispose()
     */
    private static Timer exitPrevent;
    
    /**
     * Registrates a listener that will dispose the system tray if NativeInterface is closed.
     */
    static {
        NativeInterface.addNativeInterfaceListener(new NativeInterfaceAdapter() {

            @Override
            public void nativeInterfaceClosed() {
                dispose();
            }
            
        });
    }

    /**
     * Inheritance and instantiation are disabled.
     */
    private JTray() {
        ;
    }
    
    static JTrayContainer getTrayContainer() {
        return JTrayContainer.getInstance(PASSKEY);
    }
    
    public static boolean isSupported() {
        return supported == null ? supported = NATIVE_TRAY.isSupported() : supported;
    }
    
    public static boolean isDisposed() {
        return disposed;
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
        return new JTrayItem(key, tooltip, imageData);
    }

    public static void dispose() {
        if (isDisposed()) return;
        if (!isSupported()) return;
        NATIVE_TRAY.dispose();
        getTrayContainer().dispose();
        disposed = true;
        onDispose();
    }

    static void onDispose() {
        if (exitPrevent != null && getTrayContainer().getTrayItems().isEmpty()) {
            exitPrevent.cancel();
            exitPrevent = null;
        }
    }
    
    static void checkState() {
        if (!isSupported()) throw new UnsupportedOperationException("Tray is not supported");
        if (isDisposed()) throw new IllegalStateException("Tray is disposed");
    }
    
}
