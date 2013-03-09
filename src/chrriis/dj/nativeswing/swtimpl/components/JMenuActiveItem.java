package chrriis.dj.nativeswing.swtimpl.components;

import java.util.Collections;
import java.util.List;

class JMenuActiveItem<T> extends JMenuBaseItem {

    private final List<T> LISTENERS = Collections.synchronizedList(new JTrayListenerList<T>(this));
    
    private String text;
    
    private boolean enabled;
    
    public JMenuActiveItem(JTrayMenu parent, int key, String text, boolean enabled) {
        super(parent, key);
        this.text = text;
        this.enabled = enabled;
    }
    
    public List<T> getActionListeners() {
        return LISTENERS;
    }
    
    public void addActionListener(T l) {
        LISTENERS.add(l);
    }
    
    public void removeActionListener(T l) {
        LISTENERS.remove(l);
    }
    
    public String getText() {
        return text;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
}
