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
import java.awt.image.RenderedImage;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.controller.resource.R;

public class Main {
    
    public static void main(String[] args) {
        NativeInterface.getConfiguration().addNativeClassPathReferenceClasses(NativeTray.class);
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // test images
                RenderedImage testImage = R.getIconImage();
                RenderedImage testImage2 = org.dyndns.fzoli.rccar.bridge.resource.R.getBridgeImage();
                RenderedImage testImage3 = R.getImage("horn.png");
                
                final JTrayItem item1 = JTray.createTrayItem(testImage, "First");
                final JTrayItem item2 = JTray.createTrayItem(testImage, "Second");
                final JTrayItem item3 = JTray.createTrayItem();
                
                // initial property test
                System.out.println("item1 visible: " + item1.isVisible());
                System.out.println("item3 visible: " + item3.isVisible());
                System.out.println("item1 tip: " + item1.getTooltip());
                
                // property change test
                item1.setTooltip("First changed");
                System.out.println("item1 tip: " + item1.getTooltip());
                System.out.println("item2 tip: " + item2.getTooltip());
                item2.setImage(testImage2);
                
                // menu init test
                final JTrayMenu menu1 = new JTrayMenu(item1);
                final JTrayMenu menu2 = new JTrayMenu();
                menu2.setTrayItem(item2);
                
                // menu item init test
                menu1.addMenuItem("Menu1", false);
                final JMenuDropDownItem dropItem1 = menu1.addMenuDropDownItem("Submenu");
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
                
                final JTraySubmenu submenu1 = new JTraySubmenu(dropItem1);
                final JMenuItem subitem1 = submenu1.addMenuItem("Subitem 1");
                final JMenuDropDownItem dropItem2 = submenu1.addMenuDropDownItem("Subitem 2");
                final JTraySubmenu submenu2 = new JTraySubmenu(dropItem2);
                submenu2.addMenuItem("Nice submenu", false);
                submenu2.addMenuSeparator();
                final JMenuItem subitem2 = submenu2.addMenuItem("Dispose");
                
                // menu item event test
                MenuItemSelectionListener ml = new MenuItemSelectionListener() {

                    @Override
                    public void onSelection(TrayActionEvent<JMenuSelectionItem> e) {
                        System.out.println("radio" + (e.getComponent() == radio1 ? '1' : '2') + " has been " + (e.getComponent().isSelected() ? "" : "un") + "selected.");
                    }
                    
                };
                radio1.addActionListener(ml);
                radio2.addActionListener(ml);
                MenuItemActionListener al = new MenuItemActionListener() {

                    @Override
                    public void onAction(TrayActionEvent<JMenuItem> e) {
                        System.out.println(e.getComponent() == subitem1 ? "Subitem works too." : "Yes, it works!");
                    }

                };
                menuItem1.addActionListener(al);
                subitem1.addActionListener(al);
                subitem2.addActionListener(new MenuItemActionListener() {

                    @Override
                    public void onAction(TrayActionEvent<JMenuItem> e) {
                        JTray.dispose();
                    }
                    
                });
                
                // tray item event test
                TrayItemMouseListener tl = new TrayItemMouseListener() {

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
                                    JTray.dispose();
                                }

                            });
                            
                            // dispose test
                            separator1.dispose();
                            menuItem1.dispose();
                            separator2.dispose();
                            menuItem2.dispose();
                        }
                    }

                };
                item1.addMouseListener(tl);
                item2.addMouseListener(tl);
            }
            
        });
        NativeInterface.runEventPump();
        System.out.println("Event pump has finished.");
    }

}
