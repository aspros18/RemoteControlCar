package chrriis.dj.nativeswing.test;

import chrriis.dj.nativeswing.swtimpl.components.JTray;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JMenuItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuRadioItem;
import chrriis.dj.nativeswing.swtimpl.components.JMenuSeparator;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayMenu;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemActionEvent;
import chrriis.dj.nativeswing.swtimpl.components.MenuItemActionListener;
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
                RenderedImage testImage = R.getIconImage();
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
                item2.setImage(org.dyndns.fzoli.rccar.bridge.resource.R.getBridgeImage());
                
                // menu init test
                final JTrayMenu menu1 = new JTrayMenu(item1);
                final JTrayMenu menu2 = new JTrayMenu();
                menu2.setTrayItem(item2);
                
                // menu item init test
                menu1.addMenuItem("Menu1", false);
                final JMenuSeparator separator1 = menu1.addMenuSeparator();
                final JMenuItem menuItem1 = menu1.addMenuItem("It works!");
                menu2.addMenuItem("Menu2", false);
                final JMenuSeparator separator2 = menu2.addMenuSeparator();
                final JMenuItem menuItem2 = menu2.addMenuCheckItem("Is it OK?");
                menu2.addMenuSeparator();
                final JMenuRadioItem radio1 = menu2.addMenuRadioItem("One", true);
                final JMenuRadioItem radio2 = menu2.addMenuRadioItem("Two");
                
                // menu item event test
                MenuItemActionListener<JMenuRadioItem> ml = new MenuItemActionListener<JMenuRadioItem>() {

                    @Override
                    public void onSelected(MenuItemActionEvent<JMenuRadioItem> e) {
                        System.out.println("radio" + (e.getComponent() == radio1 ? '1' : '2') + " has been " + (e.getComponent().isSelected() ? "" : "un") + "selected.");
                    }
                    
                };
                
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
                            e.getComponent().showMessage("Exit", "Click to close the application.", switched ? TrayMessageType.WARNING : TrayMessageType.INFO, new Runnable() {

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
