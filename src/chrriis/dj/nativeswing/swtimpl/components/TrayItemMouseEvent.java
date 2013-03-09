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

public class TrayItemMouseEvent extends TrayActionEvent<JTrayItem> {

    private final boolean DOUBLE_CLICK;
    
    public TrayItemMouseEvent(JTrayItem component, boolean doubleClick) {
        super(component);
        this.DOUBLE_CLICK = doubleClick;
    }

    public boolean isDoubleClick() {
        return DOUBLE_CLICK;
    }
    
}
