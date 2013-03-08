package chrriis.dj.nativeswing.swtimpl.components;

abstract class JMenuBaseItem {

    private final int KEY;
    
    JMenuBaseItem(int key) {
        KEY = key;
    }

    int getKey() {
        return KEY;
    }
    
    public void dispose() {
        // TODO
    }
    
}
