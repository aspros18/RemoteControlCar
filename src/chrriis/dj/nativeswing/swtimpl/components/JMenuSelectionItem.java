package chrriis.dj.nativeswing.swtimpl.components;

public class JMenuSelectionItem extends JMenuActiveItem<MenuItemSelectionListener> {

    private boolean selected;
    
    JMenuSelectionItem(JTrayMenu parent, int key, String text, boolean enabled, boolean selected) {
        super(parent, key, text, enabled);
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
    
}
