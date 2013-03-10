package chrriis.dj.nativeswing.swtimpl.components;

import java.util.List;

public class JMenuItem extends JMenuCommonItem {

    JMenuItem(JTrayBaseMenu parent, int key, String text, boolean enabled) {
        super(parent, key, text, enabled);
    }
    
    @Override
    public List<MenuItemActionListener> getActionListeners() {
        return super.getActionListeners();
    }
    
    @Override
    public void addActionListener(MenuItemActionListener l) {
        super.addActionListener(l);
    }
    
    @Override
    public void removeActionListener(MenuItemActionListener l) {
        super.removeActionListener(l);
    }
    
}
