package chrriis.dj.nativeswing.swtimpl.components;

abstract class JTrayObject {
    
    private final int KEY;

    private boolean disposed = false;
    
    JTrayObject(int key) {
        KEY = key;
    }
    
    int getKey() {
        return KEY;
    }

    public boolean isDisposed() {
        return disposed;
    }
    
    boolean dispose() {
        if (disposed) return false;
        this.disposed = true;
        return true;
    }
    
    abstract void checkState();
    
    JTrayContainer getTrayContainer() {
        return JTray.getTrayContainer();
    }
    
}
