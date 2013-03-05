package chrriis.dj.nativeswing.swtimpl.components.core;

import chrriis.common.RunnableReturn;
import chrriis.dj.nativeswing.swtimpl.CommandMessage;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import chrriis.dj.nativeswing.swtimpl.core.SWTNativeInterface;
import org.eclipse.swt.SWT;
import java.util.List;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class NativeTray implements INativeTray {

    private static abstract class TrayCommandMessage extends CommandMessage {

        protected static NativeTrayContainer getNativeTrayContainer() {
            return NativeTrayContainer.getInstance();
        }

        protected static Display getDisplay() {
            return SWTNativeInterface.getInstance().getDisplay();
        }

        protected static <T> T syncReturn(RunnableReturn<T> r) {
            getDisplay().syncExec(r);
            return r.getReturn();
        }

        protected TrayItem getTrayItem(int key) {
            return getNativeTrayContainer().getTrayItems().get(key);
        }
        
        protected static void setTrayItemImage(final TrayItem item, final byte[] imageData) {
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    item.setImage(getNativeTrayContainer().createImage(getDisplay(), imageData));
                }

            });
        }

        protected static void setTrayItemTooltip(final TrayItem item, final String text) {
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    item.setToolTipText(text);
                }

            });
        }
        
        protected static void setTrayItemVisible(final TrayItem item, final boolean visible) {
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    item.setVisible(visible);
                }

            });
        }
        
    }

    private static class CMN_create extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) {
            byte[] imageData = (byte[]) args[0];
            String tooltip = (String) args[1];
            List<TrayItem> items = getNativeTrayContainer().getTrayItems();
            int key = items.size();

            TrayItem item = createTrayItem();
            if (imageData != null) setTrayItemImage(item, imageData);
            setTrayItemTooltip(item, tooltip);
            if (imageData != null) setTrayItemVisible(item, true);
            
            items.add(item);
            return key;
        }

        private static Tray getSystemTray() {
            return syncReturn(new RunnableReturn<Tray>() {

                @Override
                protected Tray createReturn() throws Exception {
                    return getDisplay().getSystemTray();
                }

            });
        }

        private static TrayItem createTrayItem() {
            return syncReturn(new RunnableReturn<TrayItem>() {

                @Override
                protected TrayItem createReturn() throws Exception {
                    return new TrayItem(getSystemTray(), SWT.NONE);
                }

            });
        }
        
    }

    private static class CMN_image extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            byte[] imageData = (byte[]) args[1];
            TrayItem item = getTrayItem(key);
            if (item != null) setTrayItemImage(item, imageData);
            return null;
        }

    }

    private static class CMN_tooltip extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            String text = (String) args[1];
            TrayItem item = getTrayItem(key);
            if (item != null) setTrayItemTooltip(item, text);
            return null;
        }

    }

    private static class CMN_visible extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            boolean visible = (Boolean) args[1];
            TrayItem item = getTrayItem(key);
            if (item != null) setTrayItemVisible(item, visible);
            return null;
        }

    }
    
    private static class CMN_dispose extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    getNativeTrayContainer().dispose();
                }
                
            });
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
    public void setVisible(int key, boolean visible) {
        asyncExec(new CMN_visible(), key, visible);
    }

    @Override
    public void dispose() {
        syncExec(new CMN_dispose());
    }

}
