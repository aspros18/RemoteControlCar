package chrriis.dj.nativeswing.swtimpl.components;

abstract class JTrayObject {
    
    private final int KEY;

    private boolean disposed = false;
    
    public JTrayObject(int key) {
        KEY = key;
    }
    
    int getKey() {
        return KEY;
    }

    public boolean isDisposed() {
        return disposed;
    }
    
    void dispose() {
        this.disposed = true;
    }
    
    abstract void checkState();
    
}
