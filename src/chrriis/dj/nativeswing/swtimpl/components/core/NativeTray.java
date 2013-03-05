package chrriis.dj.nativeswing.swtimpl.components.core;

import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import java.awt.Component;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import chrriis.dj.nativeswing.swtimpl.core.ControlCommandMessage;
import chrriis.dj.nativeswing.swtimpl.core.SWTNativeComponent;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class NativeTray implements INativeTray {

    private static class TrayComponent extends SWTNativeComponent {

        protected static Control createControl(Composite parent, Object[] parameters) {
            return new Composite(parent, SWT.NONE);
        }

        protected Component createEmbeddableComponent() {
            return super.createEmbeddableComponent(new HashMap<Object, Object>());
        }

    };

    private static class CMN_test extends ControlCommandMessage {
        
        @Override
        public Object run(Object[] args) {
            byte[] iconData = (byte[]) args[0];
            String tooltip = (String) args[1];
            Display display = getControl().getDisplay();
            NativeTrayContainer ntc = NativeTrayContainer.getInstance();
            List<TrayItem> items = ntc.getTrayItems();
            int key = items.size();
            
            Tray tray = display.getSystemTray();
            TrayItem item = new TrayItem(tray, SWT.NONE);
            if (iconData != null) item.setImage(ntc.createImage(display, iconData));
            if (tooltip != null) item.setToolTipText(tooltip);
            if (iconData != null) item.setVisible(true);
            items.add(item);
            return key;
        }
        
    }

    private static final TrayComponent CMP;

    static {
        CMP = new TrayComponent();
        new JFrame().getContentPane().add(CMP.createEmbeddableComponent()); // Why is it necessary?
        CMP.initializeNativePeer();
    }

    private static Object syncExec(ControlCommandMessage msg, Object... args) {
        return msg.syncExec(CMP, args);
    }
    
    @Override
    public int createTrayItem(byte[] iconData, String tooltip) {
        return (Integer) syncExec(new CMN_test(), iconData, tooltip);
    }
    
}
