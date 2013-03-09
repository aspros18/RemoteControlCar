package chrriis.dj.nativeswing.swtimpl.components;

import java.util.Collections;
import java.util.List;

public class JMenuItem<T extends JMenuItem> extends JMenuBaseItem {

    private final List<MenuItemActionListener<T>> ACTION_LISTENERS = Collections.synchronizedList(new JTrayListenerList<MenuItemActionListener<T>>(this));
    
    private String text;
    
    private boolean enabled;
    
    JMenuItem(JTrayMenu parent, int key, String text, boolean enabled) {
        super(parent, key);
        this.text = text;
        this.enabled = enabled;
    }

    public List<MenuItemActionListener<T>> getActionListeners() {
        return ACTION_LISTENERS;
    }
    
    public void addActionListener(MenuItemActionListener<T> l) {
        ACTION_LISTENERS.add(l);
    }
    
    public void removeActionListener(MenuItemActionListener<T> l) {
        ACTION_LISTENERS.remove(l);
    }
    
    public String getText() {
        return text;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
}
