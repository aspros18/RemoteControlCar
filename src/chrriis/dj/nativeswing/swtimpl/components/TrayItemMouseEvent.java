package chrriis.dj.nativeswing.swtimpl.components;

public class TrayItemMouseEvent {
    
    private final JTrayItem COMPONENT;

    public TrayItemMouseEvent(JTrayItem component) {
        this.COMPONENT = component;
    }

    public JTrayItem getComponent() {
        return COMPONENT;
    }
    
}
