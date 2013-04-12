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
     * A timer that is not used for nothing but if it exists,
     * it prevents {@link NativeInterface#runEventPump()} to be finished.
     * @see #onDispose()
     */
    private static Timer exitPrevent;
    
    /**
     * Registrates a listener that will dispose the system tray
     * if NativeInterface is closed.
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
    
    /**
     * Returns the container of the Swing side that is used by
     * {@link JTrayObject} to registrate itself and find other objects.
     * {@link JTrayContainer#getInstance(double)} waits a passkey
     * that is used for check whether the caller has the permission
     * to use the container.
     * This method is available inside the package so tray objects
     * are able to use the container but other objects can not.
     */
    static JTrayContainer getTrayContainer() {
        return JTrayContainer.getInstance(PASSKEY);
    }
    
    /**
     * Returns whether the native system tray is supported on the current platform.
     * If the native system tray is not supported, {@link #createTrayItem(byte[], String)}
     * will throw an {@link UnsupportedOperationException} when it is called.
     * @return true if the native system tray is available, otherwise false
     */
    public static boolean isSupported() {
        return supported == null ? supported = NATIVE_TRAY.isSupported() : !disposed && supported;
    }
    
    /**
     * Returns whether the native system tray is disposed.
     * If the native system tray is disposed, {@link #createTrayItem(byte[], String)}
     * will throw an {@link IllegalStateException} when it is called.
     * @return true if the native system tray is disposed, otherwise false
     */
    public static boolean isDisposed() {
        return disposed;
    }
    
    /**
     * Creates a tray item to the native system tray.
     * The tray item will be invisible till it has no image specified.
     * @throws UnsupportedOperationException if the native system tray is not supported
     * @throws IllegalStateException if the native system tray is disposed
     */
    public static JTrayItem createTrayItem() {
        return createTrayItem((byte[]) null, null);
    }
    
    /**
     * Creates a tray item to the native system tray.
     * The tray item becomes visible in the system tray once it is created with image.
     * @param image the image to be used or <code>null</code>
     * @param tooltip the string for the tooltip;
     * if the value is <code>null</code> no tooltip is shown
     * @throws UnsupportedOperationException if the native system tray is not supported
     * @throws IllegalStateException if the native system tray is disposed
     */
    public static JTrayItem createTrayItem(RenderedImage image, String tooltip) {
        return createTrayItem(JTrayContainer.createImageData(image), tooltip);
    }

    /**
     * Creates a tray item to the native system tray.
     * The tray item becomes visible in the system tray once it is created with image.
     * @param imageData the bytes of the image to be used or <code>null</code>
     * @param tooltip the string for the tooltip;
     * if the value is <code>null</code> no tooltip is shown
     * @throws UnsupportedOperationException if the native system tray is not supported
     * @throws IllegalStateException if the native system tray is disposed
     */
    public static JTrayItem createTrayItem(byte[] imageData, String tooltip) {
        checkState();
        if (exitPrevent == null) exitPrevent = new Timer();
        int key = NATIVE_TRAY.createTrayItem(imageData, tooltip);
        return new JTrayItem(key, tooltip, imageData);
    }

    /**
     * Disposes every native components and releases the resources held by it.
     * The <code>JTray</code> won't be able to create any component
     * after this method has been called.
     * {@link NativeInterface#runEventPump()} will be finished after
     * this call if there are not other native components.
     */
    public static void dispose() {
        if (isDisposed()) return;
        if (!isSupported()) return;
        NATIVE_TRAY.dispose();
        getTrayContainer().dispose();
        disposed = true;
        onDispose();
    }

    /**
     * If there are no more tray items, cancels the timer that prevents
     * {@link NativeInterface#runEventPump()} to be finished.
     * It is called after a tray item or every native components are disposed.
     */
    static void onDispose() {
        if (exitPrevent != null && getTrayContainer().getTrayItems().isEmpty()) {
            exitPrevent.cancel();
            exitPrevent = null;
        }
    }
    
    /**
     * Avoids unexpected errors, instead throws communicative exceptions.
     * @throws UnsupportedOperationException if the native system tray is not supported
     * @throws IllegalStateException if the native system tray is disposed
     */
    static void checkState() {
        if (!isSupported()) throw new UnsupportedOperationException("Tray is not supported");
        if (isDisposed()) throw new IllegalStateException("Tray is disposed");
    }
    
}
