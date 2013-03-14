/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
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
import org.eclipse.swt.widgets.TrayItem;

/**
 * Native system tray support implementation.
 * @author Zolt&aacute;n Farkas
 */
public final class NativeTray implements INativeTray {
    
    private static abstract class BaseTrayCommandMessage extends CommandMessage {
        
        /**
         * Passkey for the instance of the {@link JTrayContainer} class.
         * @see JTrayContainer#getInstance(double)
         */
        protected final Double PASSKEY;

        public BaseTrayCommandMessage() {
            this(null);
        }

        public BaseTrayCommandMessage(Double passkey) {
            PASSKEY = passkey;
        }
        
    }
    
    /**
     * Message that runs on Swing side.
     */
    private static abstract class JTrayCommandMessage extends BaseTrayCommandMessage {
        
        public JTrayCommandMessage(double passkey) {
            super(passkey);
        }
        
        /**
         * Returns the instance of the {@link JTrayContainer} class.
         * @see #PASSKEY
         */
        protected JTrayContainer getTrayContainer() {
            return JTrayContainer.getInstance(PASSKEY);
        }
        
    }
    
    /**
     * Message that runs on native side.
     */
    private static abstract class TrayCommandMessage extends BaseTrayCommandMessage {

        public TrayCommandMessage() {
            super();
        }

        public TrayCommandMessage(double passkey) {
            super(passkey);
        }
        
        /**
         * Executes the specified message asynchronously with the given arguments on Swing side.
         * @param msg the message to be executed
         * @param args the arguments, which must be serializable
         */
        protected static void asyncExec(CommandMessage msg, Object ... args) {
            msg.asyncExec(false, args);
        }
        
        /**
         * Executes the specified message synchronously with the given arguments on Swing side.
         * @param msg the message to be executed
         * @param args the arguments, which must be serializable
         */
        protected static Object syncExec(CommandMessage msg, Object ... args) {
            return msg.syncExec(false, args);
        }
        
        /**
         * Executes a method in the SWT UI-thread and returns a typed object.
         * @param r the method that returns a typed object
         */
        protected static <T> T syncReturn(RunnableReturn<T> r) {
            Display display = getDisplay();
            if (display == null) return null;
            getDisplay().syncExec(r);
            return r.getReturn();
        }
        
        /**
         * Returns the instance of the native tray container.
         */
        protected static NativeTrayContainer getNativeTrayContainer() {
            return NativeTrayContainer.getInstance();
        }

        /**
         * Returns the SWT Display.
         */
        protected static Display getDisplay() {
            return SWTNativeInterface.getInstance().getDisplay();
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
                final JMenuItem item = (JMenuItem) getTrayContainer().getMenuItem(key);
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
                final JMenuSelectionItem item = (JMenuSelectionItem) getTrayContainer().getMenuItem(key);
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
    
    private static class CMN_trayGetSupported extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            Object tray = syncReturn(new RunnableReturn<Object>() {

                @Override
                protected Object createReturn() throws Exception {
                    return getDisplay().getSystemTray();
                }

            });
            return tray != null;
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
                NativeTrayItem nativeItem = createTrayItem(ntc, key, tooltip, imageData);
                items.add(nativeItem);
                return key;
            }
        }

        private NativeTrayItem createTrayItem(final NativeTrayContainer ntc, final int key, final String tooltip, final byte[] imgData) {
            return syncReturn(new RunnableReturn<NativeTrayItem>() {

                @Override
                protected NativeTrayItem createReturn() throws Exception {
                    final Display display = getDisplay();
                    final TrayItem item = new TrayItem(display.getSystemTray(), SWT.NONE);
                    final Image img = ntc.createImage(display, imgData);
                    final NativeTrayItem nativeItem = new NativeTrayItem(item, img, key);
                    if (tooltip != null) item.setToolTipText(tooltip);
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
            final int key = (Integer) args[0];
            final byte[] imageData = (byte[]) args[1];
            final NativeTrayContainer ntc = getNativeTrayContainer();
            final NativeTrayItem nativeItem = ntc.getNativeTrayItem(key);
            if (imageData == null || nativeItem == null) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    TrayItem item = nativeItem.getTrayItem();
                    Image prevImg = item.getImage();
                    nativeItem.setImage(ntc.createImage(getDisplay(), imageData));
                    ntc.removeImage(prevImg);
                }

            });
            return null;
        }
        
    }

    private static class CMN_trayItemSetTooltip extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            final String text = (String) args[1];
            final TrayItem item = getNativeTrayContainer().getTrayItem(key);
            if (item == null) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    item.setToolTipText(text);
                }

            });
            return null;
        }

    }
    
    private static class CMN_trayItemSetVisible extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            final boolean visible = (Boolean) args[1];
            final NativeTrayItem item = getNativeTrayContainer().getNativeTrayItem(key);
            if (item == null) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    item.setVisible(visible);
                }

            });
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
            boolean submenu = (Boolean) args[1];
            Boolean active = (Boolean) args[2];
            NativeTrayContainer ntc = getNativeTrayContainer();
            final Set<NativeTrayBaseMenu> menus = ntc.getNativeTrayMenus();
            synchronized (menus) {
                int menuKey = ntc.getNextTrayMenuKey();
                NativeTrayBaseMenu menu;
                if (!submenu) menu = createNativeTrayMenu(ntc, menuKey, active, itemKey);
                else menu = createNativeTraySubMenu(ntc, menuKey, itemKey);
                menus.add(menu);
                return menuKey;
            }
        }
        
        private static NativeTrayMenu createNativeTrayMenu(final NativeTrayContainer ntc, final int menuKey, final boolean active, final Integer itemKey) {
            return syncReturn(new RunnableReturn<NativeTrayMenu>() {

                @Override
                protected NativeTrayMenu createReturn() throws Exception {
                    Menu menu = new Menu(ntc.getShell(), SWT.POP_UP);
                    NativeTrayMenu nativeMenu = new NativeTrayMenu(menu, menuKey, active);
                    if (itemKey != null) nativeMenu.setTrayItem(ntc.getNativeTrayItem(itemKey));
                    return nativeMenu;
                }
                
            });
        }
        
        private static NativeTraySubmenu createNativeTraySubMenu(final NativeTrayContainer ntc, final int menuKey, final int itemKey) {
            return syncReturn(new RunnableReturn<NativeTraySubmenu>() {

                @Override
                protected NativeTraySubmenu createReturn() throws Exception {
                    Menu menu = new Menu(ntc.getShell(), SWT.DROP_DOWN);
                    NativeMenuItem nativeItem = ntc.getNativeMenuItem(itemKey);
                    nativeItem.getMenuItem().setMenu(menu);
                    return new NativeTraySubmenu(menu, nativeItem, menuKey);
                }
                
            });
        }
        
    }
    
    private static class CMN_trayMenuSet extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int menuKey = (Integer) args[0];
            final Integer itemKey = (Integer) args[1];
            final NativeTrayBaseMenu nativeMenu = getNativeTrayContainer().getNativeTrayMenu(menuKey);
            if (nativeMenu == null) return null;
            final NativeTrayContainer ntc = getNativeTrayContainer();
            if (NativeTrayContainer.equals(nativeMenu.getParentKey(), itemKey)) return null;
            getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    ((NativeTrayMenu) nativeMenu).setTrayItem(ntc.getNativeTrayItem(itemKey));
                }
                
            });
            return null;
        }
        
    }
    
    private static class CMN_trayMenuSetActive extends TrayCommandMessage {

        @Override
        public Object run(Object[] args) throws Exception {
            final int key = (Integer) args[0];
            final boolean active = (Boolean) args[1];
            final NativeTrayBaseMenu nativeMenu = getNativeTrayContainer().getNativeTrayMenu(key);
            if (nativeMenu == null) return null;
            if (nativeMenu.isActive() == active) return null;
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
                    final boolean dropDown = typeCode == SWT.CASCADE;
                    final boolean selectable = typeCode == SWT.RADIO || typeCode == SWT.CHECK;
                    final MenuItem mi = createMenuItem();
                    if (active) {
                        if (text != null) mi.setText(text);
                        mi.setEnabled(enabled);
                    }
                    if (selectable) {
                        mi.setSelection(selected);
                    }
                    if (active && !dropDown) {
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
                case DROP_DOWN:
                    typeCode = SWT.CASCADE;
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
                    NativeTrayBaseMenu menu = ntc.getNativeTrayMenu(key);
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
                    NativeMenuItem nativeItem = ntc.getNativeMenuItem(key);
                    if (nativeItem != null) {
                        MenuItem item = nativeItem.getMenuItem();
                        Image img = item.getImage();
                        item.dispose();
                        ntc.removeImage(img);
                        ntc.getNativeMenuItems().remove(nativeItem);
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

    /**
     * Passkey for the instance of the {@link JTrayContainer} class.
     * @see JTrayContainer#getInstance(double)
     */
    private final double PASSKEY;
    
    /**
     * Initializer.
     * @param passkey the non-null passkey for the instance of the {@link JTrayContainer} class
     * @see JTrayContainer#getInstance(double)
     */
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
    public boolean isSupported() {
        try {
            return (Boolean) syncExec(new CMN_trayGetSupported());
        }
        catch (Exception ex) {
            return false;
        }
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
        return (Integer) syncExec(new CMN_trayMenuCreate(), itemKey, false, active);
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
    public int createTraySubmenu(int dropDownMenuItemKey) {
        return (Integer) syncExec(new CMN_trayMenuCreate(), dropDownMenuItemKey, true, null);
    }
    
    @Override
    public int createMenuItem(int menuKey, Integer index, String text, boolean enabled, boolean selected, MenuItemType type) {
        return (Integer) syncExec(new CMN_menuItemCreate(PASSKEY), menuKey, index, text, enabled, selected, type);
    }

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
