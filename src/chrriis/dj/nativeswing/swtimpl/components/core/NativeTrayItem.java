package chrriis.dj.nativeswing.swtimpl.components.core;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TrayItem;

class NativeTrayItem {
        
    private final TrayItem TRAY_ITEM;
        
    private final Listener LISTENER_MENU = new Listener() {

        @Override
        public void handleEvent(Event event) {
            if (menu != null) menu.setVisible(true);
        }

    };

    private Menu menu;

    public NativeTrayItem(TrayItem trayItem) {
        TRAY_ITEM = trayItem;
    }

    public TrayItem getTrayItem() {
        return TRAY_ITEM;
    }
    
}
