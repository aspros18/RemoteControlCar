/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

/**
 * The root class of the system tray classes.
 * @author Zolt&aacute;n Farkas
 */
abstract class JTrayObject {
    
    /**
     * The unique key of the tray object
     * that identifies the object of the native side.
     */
    private final int KEY;

    /**
     * Stores whether this tray object is disposed.
     */
    private boolean disposed = false;
    
    /**
     * Constructs a tray object.
     * @param key the unique key of the tray object
     */
    JTrayObject(int key) {
        KEY = key;
    }
    
    /**
     * Returns the unique key of the tray object.
     * It's used for identify the object of the native side.
     */
    int getKey() {
        return KEY;
    }

    /**
     * Returns whether this tray object is disposed.
     */
    public boolean isDisposed() {
        return disposed;
    }
    
    /**
     * Disposes this tray object.
     * If the tray object has already been disposed, it does nothing.
     */
    boolean dispose() {
        if (disposed) return false;
        this.disposed = true;
        return true;
    }
    
    /**
     * Checks whether the tray object is changeable and
     * if it's not changeable, it will throw an exception.
     * Setter methods call this before they modify the objects.
     */
    abstract void checkState();
    
    /**
     * Returns the {@link JTrayContainer} that stores the tray objects
     * so tray objects can communicate to each other.
     */
    JTrayContainer getTrayContainer() {
        return JTray.getTrayContainer();
    }
    
}
