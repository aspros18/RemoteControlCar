package chrriis.dj.nativeswing.swtimpl.components;

public class JMenuSelectionItem extends JMenuActiveItem<MenuItemSelectionListener> {

    private boolean selected;
    
    JMenuSelectionItem(JTrayMenu parent, int key, String text, boolean enabled, boolean selected) {
        super(parent, key, text, enabled);
        this.selected = selected;
        getTrayContainer().getMenuSelectionItems().add(this);
    }

    public boolean isSelected() {
        return selected;
    }

    @Override
    public boolean dispose() {
        return dispose(true);
    }
    
    boolean dispose(boolean outer) {
        if (!super.dispose()) return false;
        if (outer) getTrayContainer().getMenuSelectionItems().remove(this);
        return true;
    }
    
}
