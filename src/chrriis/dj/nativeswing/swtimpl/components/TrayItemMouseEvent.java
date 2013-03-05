package chrriis.dj.nativeswing.swtimpl.components;

public class TrayItemMouseEvent {
    
    private final JTrayItem COMPONENT;

    private final boolean DOUBLE_CLICK;
    
    public TrayItemMouseEvent(JTrayItem component, boolean doubleClick) {
        this.COMPONENT = component;
        this.DOUBLE_CLICK = doubleClick;
    }

    public JTrayItem getComponent() {
        return COMPONENT;
    }

    public boolean isDoubleClick() {
        return DOUBLE_CLICK;
    }
    
}
