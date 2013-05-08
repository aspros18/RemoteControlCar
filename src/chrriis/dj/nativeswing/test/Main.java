package chrriis.dj.nativeswing.test;

import chrriis.dj.nativeswing.swtimpl.components.JTray;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JMenuDropDownItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuSelectionItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuSeparator;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayMenu;
import chrriis.dj.nativeswing.swtimpl.components.JTraySubmenu;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemActionListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayActionEvent;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemSelectionListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;
import chrriis.dj.nativeswing.swtimpl.components.core.NativeTray;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * System Tray test.
 * @author Zolt&aacute;n Farkas
 */
public class Main {
    
    public static void main(String[] args) {
        final TestFrame f = new TestFrame();
        NativeInterface.getConfiguration().addNativeClassPathReferenceClasses(NativeTray.class);
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // test images
                BufferedImage testImage1 = SafeIcon.createImage(UIManager.getIcon("FileView.computerIcon"));
                RenderedImage testImage2 = SafeIcon.createImage(UIManager.getIcon("FileChooser.homeFolderIcon"));
                RenderedImage testImage3 = SafeIcon.createImage(UIManager.getIcon("FileView.hardDriveIcon"));
                
                final JTrayItem item1 = JTray.createTrayItem(testImage1, "First");
                final JTrayItem item2 = JTray.createTrayItem(testImage1, "Second");
                final JTrayItem item3 = JTray.createTrayItem();
                
                f.setVisible(true);
                
                // initial property test
                f.l("item1 visible: " + item1.isVisible());
                f.l("item3 visible: " + item3.isVisible());
                f.l("item1 tip: " + item1.getTooltip());
                
                // property change test
                item1.setTooltip("First changed");
                f.l("item1 tip: " + item1.getTooltip());
                f.l("item2 tip: " + item2.getTooltip());
                item2.setImage(testImage2);
                
                // menu init test
                final JTrayMenu menu1 = new JTrayMenu(item1);
                final JTrayMenu menu2 = new JTrayMenu();
                menu2.setTrayItem(item2);
                
                // menu item init test
                menu1.addMenuItem("Menu1", false);
                menu1.addMenuSeparator();
                final JMenuDropDownItem dropItem1 = menu1.addMenuDropDownItem("Submenu");
                final JTraySubmenu submenu1 = dropItem1.getSubmenu();
                dropItem1.setImage(testImage3);
                final JMenuSeparator separator1 = menu1.addMenuSeparator();
                final JMenuItem menuItem1 = menu1.addMenuItem("Does it work?");
                menuItem1.setImage(testImage3);
                menu2.addMenuItem("Menu2", false);
                final JMenuSeparator separator2 = menu2.addMenuSeparator();
                final JMenuSelectionItem menuItem2 = menu2.addMenuCheckItem("Is it OK?");
                menu2.addMenuSeparator();
                final JMenuSelectionItem radio1 = menu2.addMenuRadioItem("One", true);
                final JMenuSelectionItem radio2 = menu2.addMenuRadioItem("Two");
                final JMenuItem subitem1 = submenu1.addMenuItem("Subitem 1");
                final JMenuDropDownItem dropItem2 = submenu1.addMenuDropDownItem("Subitem 2");
                final JTraySubmenu submenu2 = dropItem2.getSubmenu();
                submenu2.addMenuItem("Nice submenu", false);
                submenu2.addMenuSeparator();
                final JMenuItem subitem2 = submenu2.addMenuItem("Dispose");
                
                // menu item event test
                MenuItemSelectionListener ml = new MenuItemSelectionListener() {

                    @Override
                    public void onSelectionChanged(TrayActionEvent<JMenuSelectionItem> e) {
                        f.l((e.getComponent() == menuItem2 ? "check item" : ("radio" + (e.getComponent() == radio1 ? '1' : '2'))) + " has been " + (e.getComponent().isSelected() ? "" : "un") + "selected.");
                    }
                    
                };
                radio1.addActionListener(ml);
                radio2.addActionListener(ml);
                menuItem2.addActionListener(ml);
                MenuItemActionListener al = new MenuItemActionListener() {

                    @Override
                    public void onAction(TrayActionEvent<JMenuItem> e) {
                        f.l(e.getComponent() == subitem1 ? "Subitem works too." : "Yes, it works!");
                    }

                };
                menuItem1.addActionListener(al);
                subitem1.addActionListener(al);
                subitem2.addActionListener(new MenuItemActionListener() {

                    @Override
                    public void onAction(TrayActionEvent<JMenuItem> e) {
                        f.dispose();
                        JTray.dispose();
                    }
                    
                });
                
                // tray item event test
                TrayItemMouseListener tl = new TrayItemMouseListener() {
                    
                    int i = 0;
                    
                    @Override
                    public void onClick(TrayItemMouseEvent e) {
                        // switch menus
                        JTrayItem i1 = menu1.getTrayItem();
                        JTrayItem i2 = menu2.getTrayItem();
                        menu1.setTrayItem(i2);
                        menu2.setTrayItem(i1);
                        
                        // double click test
                        if (e.isDoubleClick()) {
                            
                            // show message test
                            boolean switched = menu1.getTrayItem() != item1;
                            e.getComponent().showMessage("Exit", (switched ? "WARNING! " : "") + "Click to close the application.", switched ? TrayMessageType.WARNING : TrayMessageType.INFO, new Runnable() {

                                @Override
                                public void run() {
                                    f.dispose();
                                    JTray.dispose();
                                }

                            });
                            
                            // dispose test
                            separator1.dispose();
                            menuItem1.dispose();
                            separator2.dispose();
                            menuItem2.dispose();
                            
                            // enable-disable test
                            boolean enabled = ++i % 2 == 0;
                            dropItem1.setEnabled(enabled);
                            menu2.setActive(enabled);
                        }
                    }

                };
                item1.addMouseListener(tl);
                item2.addMouseListener(tl);
            }
            
        });
        NativeInterface.runEventPump();
        f.l("Event pump has finished.");
    }

}
