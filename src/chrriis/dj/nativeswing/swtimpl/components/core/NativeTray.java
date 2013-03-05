package chrriis.dj.nativeswing.swtimpl.components.core;

import chrriis.dj.nativeswing.swtimpl.CommandMessage;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import org.eclipse.swt.SWT;
import java.util.List;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class NativeTray implements INativeTray {

    private static class CMN_create extends CommandMessage {

        @Override
        public Object run(Object[] args) {
            byte[] imageData = (byte[]) args[0];
            String tooltip = (String) args[1];
            Display display = Display.getCurrent();
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

    private static class CMN_image extends CommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            byte[] imageData = (byte[]) args[1];
            NativeTrayContainer ntc = NativeTrayContainer.getInstance();
            TrayItem item = ntc.getTrayItems().get(key);
            if (item != null) item.setImage(ntc.createImage(Display.getCurrent(), imageData));
            return null;
        }

    }

    private static class CMN_tooltip extends CommandMessage {

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

    private static class CMN_dispose extends CommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            NativeTrayContainer.getInstance().dispose();
            return null;
        }

    }

    private static void asyncExec(CommandMessage msg, Object... args) {
        msg.asyncExec(true, args);
    }

    private static Object syncExec(CommandMessage msg, Object... args) {
        return msg.syncExec(true, args);
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
