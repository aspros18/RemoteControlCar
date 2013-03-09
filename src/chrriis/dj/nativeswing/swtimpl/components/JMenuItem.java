package chrriis.dj.nativeswing.swtimpl.components;

public class JMenuItem extends JMenuActiveItem<MenuItemActionListener> {

    JMenuItem(JTrayMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
    }
    
}
