package chrriis.dj.nativeswing.swtimpl.components.core;

import chrriis.common.RunnableReturn;
import chrriis.dj.nativeswing.swtimpl.CommandMessage;
import chrriis.dj.nativeswing.swtimpl.components.JTrayContainer;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseListener;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import chrriis.dj.nativeswing.swtimpl.core.SWTNativeInterface;
import java.util.List;
import javax.swing.SwingUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class NativeTray implements INativeTray {

    private static abstract class TrayCommandMessage extends CommandMessage {

        protected static void asyncExec(CommandMessage msg, Object ... args) {
            msg.asyncExec(false, args);
        }
        
        protected static Object syncExec(CommandMessage msg, Object ... args) {
            return msg.syncExec(false, args);
        }
        
        protected static <T> T syncReturn(RunnableReturn<T> r) {
            getDisplay().syncExec(r);
            return r.getReturn();
        }
        
        protected static NativeTrayContainer getNativeTrayContainer() {
            return NativeTrayContainer.getInstance();
        }

        protected static Display getDisplay() {
            return SWTNativeInterface.getInstance().getDisplay();
        }

        protected static int getNextKey(List<? extends NativeTrayObject> ls) {
            int key = -1;
            boolean e;
            do {
                key++;
                e = false;
                for (NativeTrayObject obj : ls) {
                    if (obj.getKey() == key) {
                        e = true;
                        break;
                    }
                }
            } while (e);
            return key;
        }
        
        protected static NativeTrayItem getNativeTrayItem(int key) {
            return getNativeTrayObject(getNativeTrayContainer().getNativeTrayItems(), key);
        }
        
        protected static NativeTrayMenu getNativeTrayMenu(int key) {
            return getNativeTrayObject(getNativeTrayContainer().getNativeTrayMenus(), key);
        }
        
        private static <T extends NativeTrayObject> T getNativeTrayObject(List<T> ls, int key) {
            for (T obj : ls) {
                if (obj.getKey() == key) return obj;
            }
            return null;
        }
        
        protected static TrayItem getTrayItem(int key) {
            return getNativeTrayItem(key).getTrayItem();
        }
        
        protected static void setTrayItemImage(final TrayItem item, final byte[] imageData) {
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    Image prevImg = item.getImage();
                    NativeTrayContainer ntc = getNativeTrayContainer();
                    item.setImage(ntc.createImage(getDisplay(), imageData));
                    ntc.removeImage(prevImg);
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

    private static class CMJ_clicked extends CommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            boolean doubleClick = (Boolean) args[1];
            JTrayItem item = JTrayContainer.getTrayItem(key);
            if (item != null) {
                final TrayItemMouseEvent e = new TrayItemMouseEvent(item, doubleClick);
                final List<TrayItemMouseListener> ls = item.getMouseListeners();
                synchronized (ls) {
                    for (final TrayItemMouseListener l : ls) {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                l.onClick(e);
                            }
                            
                        });
                    }
                }
            }
            return null;
        }
        
    }
    
    private static class CMN_create extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) {
            byte[] imageData = (byte[]) args[0];
            String tooltip = (String) args[1];
            NativeTrayContainer ntc = getNativeTrayContainer();
            final List<NativeTrayItem> items = ntc.getNativeTrayItems();
            synchronized (items) {
                int key = getNextKey(items);
                
                NativeTrayItem nativeItem = createTrayItem(key);
                items.add(nativeItem);
                
                TrayItem item = nativeItem.getTrayItem();
                if (imageData != null) setTrayItemImage(item, imageData);
                setTrayItemTooltip(item, tooltip);
                if (imageData != null) setTrayItemVisible(item, true);
                
                return key;
            }
        }

        private static Tray getSystemTray() {
            return syncReturn(new RunnableReturn<Tray>() {

                @Override
                protected Tray createReturn() throws Exception {
                    return getDisplay().getSystemTray();
                }

            });
        }

        private static NativeTrayItem createTrayItem(final int key) {
            return syncReturn(new RunnableReturn<NativeTrayItem>() {

                @Override
                protected NativeTrayItem createReturn() throws Exception {
                    final TrayItem item = new TrayItem(getSystemTray(), SWT.NONE);
                    final NativeTrayItem nativeItem = new NativeTrayItem(item, key);
                    item.addListener(SWT.MenuDetect, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            NativeTrayMenu nativeMenu = nativeItem.getNativeTrayMenu();
                            if (nativeMenu != null && nativeMenu.isActive()) nativeMenu.getMenu().setVisible(true);
                        }
                        
                    });
                    item.addListener(SWT.DefaultSelection, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            asyncExec(new CMJ_clicked(), key, true);
                        }
                        
                    });
                    item.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            asyncExec(new CMJ_clicked(), key, false);
                        }
                        
                    });
                    return nativeItem;
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

    private static class CMN_disposeTrayItem extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    NativeTrayContainer ntc = getNativeTrayContainer();
                    NativeTrayItem item = getNativeTrayItem(key);
                    if (item != null) {
                        Image img = item.getTrayItem().getImage();
                        item.getTrayItem().dispose();
                        ntc.getNativeTrayItems().remove(item);
                        ntc.removeImage(img);
                    }
                }
                
            });
            return null;
        }

    }
    
    private static class CMN_disposeTray extends TrayCommandMessage {

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

    private static class CMN_menu extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            TrayMenuData data = (TrayMenuData) args[0];
            // TODO
//            if (true) {
//                NativeTrayMenu menu = createTrayMenu(data);
//                if (data.key != null) {
//                    NativeTrayItem item = getNativeTrayItem(data.key);
//                    item.setNativeTrayMenu(menu);
//                }
//            }
            return null;
        }
        
        private NativeTrayMenu createTrayMenu(final TrayMenuData data) {
            return syncReturn(new RunnableReturn<NativeTrayMenu>() {

                @Override
                protected NativeTrayMenu createReturn() throws Exception {
                    Menu menu = new Menu(getNativeTrayContainer().getShell(), SWT.POP_UP);
                    return new NativeTrayMenu(menu, data);
                }
                
            });
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
    public void dispose(int key) {
        syncExec(new CMN_disposeTrayItem(), key);
    }

    @Override
    public void dispose() {
        syncExec(new CMN_disposeTray());
    }

    @Override
    public void setTrayMenu(TrayMenuData data) {
        asyncExec(new CMN_menu(), data.clone());
    }

}
