package chrriis.dj.nativeswing.swtimpl.components;

abstract class JMenuSelectableItem<T extends JMenuSelectableItem> extends JMenuItem<T> {

    private boolean selected;
    
    JMenuSelectableItem(JTrayMenu parent, int key, String text, boolean enabled, boolean selected) {
        super(parent, key, text, enabled);
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
    
}
