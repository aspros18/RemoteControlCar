package chrriis.dj.nativeswing.swtimpl.components;

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
