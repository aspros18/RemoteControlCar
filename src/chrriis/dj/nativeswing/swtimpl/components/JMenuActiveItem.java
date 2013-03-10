package chrriis.dj.nativeswing.swtimpl.components;

import java.util.Collections;
import java.util.List;
import static chrriis.dj.nativeswing.swtimpl.components.JTray.NATIVE_TRAY;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray.MenuItemProperty;

class JMenuActiveItem<T> extends JMenuBaseItem {

    private final List<T> LISTENERS = Collections.synchronizedList(new JTrayListenerList<T>(this));
    
    private String text;
    
    private boolean enabled;
    
    public JMenuActiveItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key);
        this.text = text;
        this.enabled = enabled;
    }
    
    protected List<T> getActionListeners() {
        return LISTENERS;
    }
    
    protected void addActionListener(T l) {
        LISTENERS.add(l);
    }
    
    protected void removeActionListener(T l) {
        LISTENERS.remove(l);
    }
    
    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) throw new IllegalArgumentException("Menu item text can not be null");
        checkState();
        NATIVE_TRAY.setMenuItemText(getKey(), text);
        this.text = text;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        checkState();
        applyEnabled(enabled);
        this.enabled = enabled;
    }
    
    protected void applyEnabled(boolean enabled) {
        NATIVE_TRAY.setMenuItemProperty(getKey(), MenuItemProperty.ENABLED, enabled);
    }
    
}
