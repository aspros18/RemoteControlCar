package chrriis.dj.nativeswing.swtimpl.components;

class TrayActionEvent<T> {
    
    private final T COMPONENT;
    
    public TrayActionEvent(T component) {
       COMPONENT = component;
    }
    
    public T getComponent() {
        return COMPONENT;
    }
    
}
