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

    private static class CMN_create extends ControlCommandMessage {

        @Override
        public Object run(Object[] args) {
            byte[] imageData = (byte[]) args[0];
            String tooltip = (String) args[1];
            Display display = getControl().getDisplay();
            NativeTrayContainer ntc = NativeTrayContainer.getInstance();
            List<TrayItem> items = ntc.getTrayItems();
            int key = items.size();
            
            Tray tray = display.getSystemTray();
            TrayItem item = new TrayItem(tray, SWT.NONE);
            if (imageData != null) item.setImage(ntc.createImage(display, imageData));
            if (tooltip != null) item.setToolTipText(tooltip);
            if (imageData != null) item.setVisible(true);
            items.add(item);
            return key;
        }

    }

    private static class CMN_image extends ControlCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            byte[] imageData = (byte[]) args[1];
            NativeTrayContainer ntc = NativeTrayContainer.getInstance();
            TrayItem item = ntc.getTrayItems().get(key);
            if (item != null) item.setImage(ntc.createImage(getControl().getDisplay(), imageData));
            return null;
        }

    }

    private static class CMN_tooltip extends ControlCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            String text = (String) args[1];
            NativeTrayContainer ntc = NativeTrayContainer.getInstance();
            TrayItem item = ntc.getTrayItems().get(key);
            if (item != null) item.setToolTipText(text);
            return null;
        }

    }

    private static class CMN_dispose extends ControlCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            NativeTrayContainer.getInstance().dispose();
            return null;
        }

    }

    private static final TrayComponent CMP;

    static {
        CMP = new TrayComponent();
        new JFrame().getContentPane().add(CMP.createEmbeddableComponent()); // Why is it necessary?
        CMP.initializeNativePeer();
    }

    private static void asyncExec(ControlCommandMessage msg, Object... args) {
        msg.asyncExec(CMP, args);
    }

    private static Object syncExec(ControlCommandMessage msg, Object... args) {
        return msg.syncExec(CMP, args);
    }

    @Override
    public int createTrayItem(byte[] imageData, String tooltip) {
        return (Integer) syncExec(new CMN_create(), imageData, tooltip);
    }

    @Override
    public void setTooltip(int key, String text) {
        asyncExec(new CMN_tooltip(), key, text);
    }

    @Override
    public void setImage(int key, byte[] imageData) {
        asyncExec(new CMN_image(), key, imageData);
    }

    @Override
    public void dispose() {
        syncExec(new CMN_dispose());
    }

}
