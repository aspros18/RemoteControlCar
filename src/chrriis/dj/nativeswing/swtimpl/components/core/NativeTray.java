package chrriis.dj.nativeswing.swtimpl.components.core;

import chrriis.common.RunnableReturn;
import chrriis.dj.nativeswing.swtimpl.CommandMessage;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JMenuItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuSelectionItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayContainer;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemActionListener;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemSelectionListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayActionEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;
import static chrriis.dj.nativeswing.swtimpl.components.TrayMessageType.ERROR;
import static chrriis.dj.nativeswing.swtimpl.components.TrayMessageType.WARNING;
import chrriis.dj.nativeswing.swtimpl.components.internal.INativeTray;
import chrriis.dj.nativeswing.swtimpl.core.SWTNativeInterface;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public final class NativeTray implements INativeTray {
    
    private static abstract class BaseTrayCommandMessage extends CommandMessage {
        
        protected final Double PASSKEY;

        public BaseTrayCommandMessage() {
            this(null);
        }

        public BaseTrayCommandMessage(Double passkey) {
            PASSKEY = passkey;
        }
        
    }
    
    private static abstract class JTrayCommandMessage extends BaseTrayCommandMessage {
        
        public JTrayCommandMessage(double passkey) {
            super(passkey);
        }
        
        protected JTrayContainer getTrayContainer() {
            return JTrayContainer.getInstance(PASSKEY);
        }
        
    }
    
    private static abstract class TrayCommandMessage extends BaseTrayCommandMessage {

        public TrayCommandMessage() {
            super();
        }

        public TrayCommandMessage(double passkey) {
            super(passkey);
        }
        
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
        
        protected static void setTrayMenu(final NativeTrayMenu nativeMenu, final Integer itemKey) {
            final NativeTrayContainer ntc = getNativeTrayContainer();
            if (nativeMenu == null) return;
            if (NativeTrayContainer.equals(nativeMenu.getTrayItemKey(), itemKey)) return;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    if (itemKey != null) {
                        NativeTrayItem nativeItem = ntc.getNativeTrayItem(itemKey);
                        NativeTrayMenu replaced = nativeItem.getNativeTrayMenu();
                        if (replaced != null) replaced.setTrayItemKey(null);
                        nativeItem.setNativeTrayMenu(nativeMenu);
                    }
                    nativeMenu.setTrayItemKey(itemKey);
                }
                
            });
        }
        
    }

    private static class CMJ_trayItemOnClick extends JTrayCommandMessage {

        public CMJ_trayItemOnClick(double passkey) {
            super(passkey);
        }

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            boolean doubleClick = (Boolean) args[1];
            JTrayItem item = getTrayContainer().getTrayItem(key);
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
    
    private static class CMJ_trayMessageOnClick extends JTrayCommandMessage {

        public CMJ_trayMessageOnClick(double passkey) {
            super(passkey);
        }

        @Override
        public Object run(Object[] args) throws Exception {
            int msgKey = (Integer) args[0];
            JTrayContainer tc = getTrayContainer();
            Runnable r = tc.getMessageCallback(msgKey);
            if (r != null) SwingUtilities.invokeLater(r);
            tc.removeMessageCallback(msgKey);
            return null;
        }
        
    }
    
    private static class CMJ_menuItemSelected extends JTrayCommandMessage {

        public CMJ_menuItemSelected(double passkey) {
            super(passkey);
        }

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            Boolean selected = (Boolean) args[1];
            if (selected == null) {
                final JMenuItem item = getTrayContainer().getMenuItem(key);
                if (item == null) return null;
                for (final MenuItemActionListener l : item.getActionListeners()) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            l.onAction(new TrayActionEvent<JMenuItem>(item));
                        }
                        
                    });
                }
            }
            else {
                final JMenuSelectionItem item = getTrayContainer().getMenuSelectionItem(key);
                if (item == null) return null;
                item.setSelected(selected);
                for (final MenuItemSelectionListener l : item.getActionListeners()) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            l.onSelection(new TrayActionEvent<JMenuSelectionItem>(item));
                        }
                        
                    });
                }
            }
            return null;
        }
        
    }
    
    private static class CMN_trayItemCreate extends TrayCommandMessage {

        public CMN_trayItemCreate(double passkey) {
            super(passkey);
        }

        @Override
        public Object run(Object[] args) {
            byte[] imageData = (byte[]) args[0];
            String tooltip = (String) args[1];
            NativeTrayContainer ntc = getNativeTrayContainer();
            final Set<NativeTrayItem> items = ntc.getNativeTrayItems();
            synchronized (items) {
                int key = ntc.getNextTrayItemKey();
                
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

        private NativeTrayItem createTrayItem(final int key) {
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
                            asyncExec(new CMJ_trayItemOnClick(PASSKEY), key, true);
                        }
                        
                    });
                    item.addListener(SWT.Selection, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            asyncExec(new CMJ_trayItemOnClick(PASSKEY), key, false);
                        }
                        
                    });
                    return nativeItem;
                }

            });
        }
        
    }

    private static class CMN_trayItemSetImage extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            byte[] imageData = (byte[]) args[1];
            TrayItem item = getNativeTrayContainer().getTrayItem(key);
            if (item != null) setTrayItemImage(item, imageData);
            return null;
        }

    }

    private static class CMN_trayItemSetTooltip extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            String text = (String) args[1];
            TrayItem item = getNativeTrayContainer().getTrayItem(key);
            if (item != null) setTrayItemTooltip(item, text);
            return null;
        }

    }
    
    private static class CMN_trayItemSetVisible extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            int key = (Integer) args[0];
            boolean visible = (Boolean) args[1];
            TrayItem item = getNativeTrayContainer().getTrayItem(key);
            if (item != null) setTrayItemVisible(item, visible);
            return null;
        }

    }

    private static class CMN_trayItemShowMessage extends TrayCommandMessage {

        public CMN_trayItemShowMessage(double passkey) {
            super(passkey);
        }

        @Override
        public Object run(Object[] args) throws Exception {
            final int itemKey = (Integer) args[0];
            final int msgKey = (Integer) args[1];
            final String title = (String) args[2];
            final String message = (String) args[3];
            final int type = getTypeCode((TrayMessageType) args[4]);
            final NativeTrayContainer ntc = getNativeTrayContainer();
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    final ToolTip tip = new ToolTip(ntc.getShell(), SWT.BALLOON | type);
                    tip.setText(title);
                    tip.setMessage(message);
                    if (msgKey != -1) {
                        tip.addListener(SWT.Selection, new Listener() {

                            @Override
                            public void handleEvent(Event event) {
                                asyncExec(new CMJ_trayMessageOnClick(PASSKEY), msgKey);
                            }
                            
                        });
                    }
                    TrayItem item = ntc.getTrayItem(itemKey);
                    ToolTip oldTip = item.getToolTip();
                    if (oldTip != null) oldTip.dispose();
                    item.setToolTip(tip);
                    item.addListener(SWT.Dispose, new Listener() {

                        @Override
                        public void handleEvent(Event event) {
                            tip.dispose();
                        }
                        
                    });
                    tip.setVisible(true);
                }
                
            });
            return 0;
        }
        
        private static int getTypeCode(TrayMessageType type) {
            int typeCode = SWT.ICON_INFORMATION;
            switch (type) {
                case WARNING:
                    typeCode = SWT.ICON_WARNING;
                    break;
                case ERROR:
                    typeCode = SWT.ICON_ERROR;
            }
            return typeCode;
        }
        
    }
    
    private static class CMN_trayMenuCreate extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            Integer itemKey = (Integer) args[0];
            boolean active = (Boolean) args[1];
            NativeTrayContainer ntc = getNativeTrayContainer();
            final Set<NativeTrayMenu> menus = ntc.getNativeTrayMenus();
            synchronized (menus) {
                int menuKey = ntc.getNextTrayMenuKey();
                NativeTrayMenu menu = createNativeTrayMenu(ntc, menuKey, active);
                menus.add(menu);
                setTrayMenu(menu, itemKey);
                return menuKey;
            }
        }
        
        private static NativeTrayMenu createNativeTrayMenu(final NativeTrayContainer ntc, final int menuKey, final boolean active) {
            return syncReturn(new RunnableReturn<NativeTrayMenu>() {

                @Override
                protected NativeTrayMenu createReturn() throws Exception {
                    Menu menu = new Menu(ntc.getShell(), SWT.POP_UP);
                    // TODO: add type parameter in order to create SWT.DROP_DOWN menu too
                    return new NativeTrayMenu(menu, menuKey, active);
                }
                
            });
        }
        
    }
    
    private static class CMN_trayMenuSet extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int menuKey = (Integer) args[0];
            final Integer itemKey = (Integer) args[1];
            final NativeTrayMenu nativeMenu = getNativeTrayContainer().getNativeTrayMenu(menuKey);
            setTrayMenu(nativeMenu, itemKey);
            return null;
        }
        
    }
    
    private static class CMN_trayMenuSetActive extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            final boolean active = (Boolean) args[1];
            final NativeTrayMenu nativeMenu = getNativeTrayContainer().getNativeTrayMenu(key);
            if (nativeMenu == null) return null;
            if (nativeMenu.isActive() != active) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    nativeMenu.setActive(active);
                }
                
            });
            return null;
        }
        
    }
    
    private static class CMN_menuItemCreate extends TrayCommandMessage {

        public CMN_menuItemCreate(double passkey) {
            super(passkey);
        }

        @Override
        public Object run(Object[] args) throws Exception {
            int menuKey = (Integer) args[0];
            Integer index = (Integer) args[1];
            String text = (String) args[2];
            boolean enabled = (Boolean) args[3];
            boolean selected = (Boolean) args[4];
            MenuItemType type = (MenuItemType) args[5];
            NativeTrayContainer ntc = getNativeTrayContainer();
            final Set<NativeMenuItem> items = ntc.getNativeMenuItems();
            synchronized (items) {
                int key = ntc.getNextMenuItemKey();
                NativeMenuItem item = createMenuItem(ntc, key, menuKey, index, text, enabled, selected, type);
                items.add(item);
                return key;
            }
        }
        
        private NativeMenuItem createMenuItem(NativeTrayContainer ntc, final int key, int menuKey, final Integer index, final String text, final boolean enabled, final boolean selected, MenuItemType type) {
            final int typeCode = getTypeCode(type);
            final Menu menu = ntc.getTrayMenu(menuKey);
            return syncReturn(new RunnableReturn<NativeMenuItem>() {

                private MenuItem createMenuItem() {
                    MenuItem mi;
                    if (index == null ) {
                        mi = new MenuItem(menu, typeCode);
                    }
                    else try {
                        mi = new MenuItem(menu, typeCode, index);
                    }
                    catch (Exception ex) {
                        mi = new MenuItem(menu, typeCode);
                    }
                    return mi;
                }
                
                @Override
                protected NativeMenuItem createReturn() throws Exception {
                    final boolean active = typeCode != SWT.SEPARATOR;
                    final boolean selectable = typeCode == SWT.RADIO || typeCode == SWT.CHECK;
                    final MenuItem mi = createMenuItem();
                    if (active) {
                        if (text != null) mi.setText(text);
                        mi.setEnabled(enabled);
                    }
                    if (selectable) {
                        mi.setSelection(selected);
                    }
                    if (active) {
                        mi.addListener(SWT.Selection, new Listener() {

                            @Override
                            public void handleEvent(Event event) {
                                asyncExec(new CMJ_menuItemSelected(PASSKEY), key, selectable ? mi.getSelection() : null);
                            }

                        });
                    }
                    return new NativeMenuItem(key, mi);
                }
                
            });
        }
        
        private static int getTypeCode(MenuItemType type) {
            int typeCode = SWT.NONE;
            switch (type) {
                case CHECK:
                    typeCode = SWT.CHECK;
                    break;
                case RADIO:
                    typeCode = SWT.RADIO;
                    break;
                case SEPARATOR:
                    typeCode = SWT.SEPARATOR;
                    break;
//                case DROP_DOWN:
//                    typeCode = SWT.CASCADE;
            }
            return typeCode;
        }
        
    }
    
    private static class CMN_menuItemSetImage extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            final byte[] imageData = (byte[]) args[1];
            final NativeTrayContainer ntc = getNativeTrayContainer();
            final MenuItem item = ntc.getMenuItem(key);
            if (item == null) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    Image img = ntc.createImage(getDisplay(), imageData);
                    ntc.removeImage(item.getImage());
                    item.setImage(img);
                }
                
            });
            return null;
        }
        
    }
    
    private static class CMN_menuItemSetText extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            final String text = (String) args[1];
            final MenuItem item = getNativeTrayContainer().getMenuItem(key);
            if (item == null) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    item.setText(text);
                }
                
            });
            return null;
        }
        
    }
    
    private static class CMN_menuItemSetProperty extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            final MenuItemProperty property = (MenuItemProperty) args[1];
            final boolean value = (Boolean) args[2];
            final MenuItem item = getNativeTrayContainer().getMenuItem(key);
            if (item == null) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    switch (property) {
                        case ENABLED:
                            if (item.isEnabled() != value) item.setEnabled(value);
                            break;
                        case SELECTION:
                            if (item.getSelection() != value) item.setSelection(value);
                    }
                }
                
            });
            return null;
        }
        
    }
    
    private static class CMN_trayItemDispose extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    NativeTrayContainer ntc = getNativeTrayContainer();
                    NativeTrayItem nativeItem = ntc.getNativeTrayItem(key);
                    if (nativeItem != null) {
                        TrayItem item = nativeItem.getTrayItem();
                        Image img = item.getImage();
                        ToolTip tip = item.getToolTip();
                        if (tip != null) tip.dispose();
                        item.dispose();
                        ntc.getNativeTrayItems().remove(nativeItem);
                        ntc.removeImage(img);
                    }
                }
                
            });
            return null;
        }

    }
    
    private static class CMN_trayMenuDispose extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    NativeTrayContainer ntc = getNativeTrayContainer();
                    NativeTrayMenu menu = ntc.getNativeTrayMenu(key);
                    if (menu != null) {
                        menu.getMenu().dispose();
                        ntc.getNativeTrayMenus().remove(menu);
                    }
                }
                
            });
            return null;
        }

    }
    
    private static class CMN_menuItemDispose extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    NativeTrayContainer ntc = getNativeTrayContainer();
                    NativeMenuItem item = ntc.getNativeMenuItem(key);
                    if (item != null) {
                        item.getMenuItem().dispose();
                        ntc.getNativeMenuItems().remove(item);
                    }
                }
                
            });
            return null;
        }

    }
    
    private static class CMN_trayDispose extends TrayCommandMessage {

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

    private final double PASSKEY;
    
    NativeTray(Double passkey) {
        PASSKEY = passkey;
    }
    
    private static void asyncExec(CommandMessage msg, Object... args) {
        if (NativeInterface.isOpen()) msg.asyncExec(true, args);
    }

    private static Object syncExec(CommandMessage msg, Object... args) {
        if (NativeInterface.isOpen()) return msg.syncExec(true, args);
        return null;
    }

    @Override
    public int createTrayItem(byte[] imageData, String tooltip) {
        return (Integer) syncExec(new CMN_trayItemCreate(PASSKEY), imageData, tooltip);
    }

    @Override
    public void setTrayItemTooltip(int key, String text) {
        asyncExec(new CMN_trayItemSetTooltip(), key, text);
    }

    @Override
    public void setTrayItemImage(int key, byte[] imageData) {
        asyncExec(new CMN_trayItemSetImage(), key, imageData);
    }

    @Override
    public void setTrayItemVisible(int key, boolean visible) {
        asyncExec(new CMN_trayItemSetVisible(), key, visible);
    }

    @Override
    public void showMessage(int itemKey, int msgKey, String title, String message, TrayMessageType type) {
        asyncExec(new CMN_trayItemShowMessage(PASSKEY), itemKey, msgKey, title, message, type);
    }

    @Override
    public int createTrayMenu(Integer itemKey, boolean active) {
        return (Integer) syncExec(new CMN_trayMenuCreate(), itemKey, active);
    }

    @Override
    public void setTrayMenu(int menuKey, Integer itemKey) {
        asyncExec(new CMN_trayMenuSet(), menuKey, itemKey);
    }

    @Override
    public void setTrayMenuActive(int menuKey, boolean active) {
        asyncExec(new CMN_trayMenuSetActive(), menuKey, active);
    }

    @Override
    public int createMenuItem(int menuKey, Integer index, String text, boolean enabled, boolean selected, MenuItemType type) {
        return (Integer) syncExec(new CMN_menuItemCreate(PASSKEY), menuKey, index, text, enabled, selected, type);
    }

//    @Override
//    public void setMenuItem(int menuItemKey, Integer menuKey) {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    @Override
    public void setMenuItemImage(int menuItemKey, byte[] imageData) {
        asyncExec(new CMN_menuItemSetImage(), menuItemKey, imageData);
    }

    @Override
    public void setMenuItemText(int menuItemKey, String text) {
        asyncExec(new CMN_menuItemSetText(), menuItemKey, text);
    }

    @Override
    public void setMenuItemProperty(int menuItemKey, MenuItemProperty property, boolean value) {
        asyncExec(new CMN_menuItemSetProperty(), menuItemKey, property, value);
    }
    
    @Override
    public void disposeTrayItem(int key) {
        syncExec(new CMN_trayItemDispose(), key);
    }

    @Override
    public void disposeTrayMenu(int key) {
        syncExec(new CMN_trayMenuDispose(), key);
    }

    @Override
    public void disposeMenuItem(int menuItemKey) {
        syncExec(new CMN_menuItemDispose(), menuItemKey);
    }

    @Override
    public void dispose() {
        syncExec(new CMN_trayDispose());
    }

}
