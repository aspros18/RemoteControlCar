package chrriis.dj.nativeswing.swtimpl.components;

public class JMenuItem extends JMenuBaseItem {

    private String text;
    
    private boolean enabled;
    
    JMenuItem(JTrayMenu parent, int key, String text, boolean enabled) {
        super(parent, key);
        this.text = text;
        this.enabled = enabled;
    }

    public String getText() {
        return text;
    }

    public boolean isEnabled() {
        return enabled;
    }
    
}
