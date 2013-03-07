package chrriis.dj.nativeswing.test;

import chrriis.dj.nativeswing.swtimpl.components.core.NativeTray;
import chrriis.dj.nativeswing.swtimpl.components.JTray;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.JTrayMenu;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseListener;
import chrriis.dj.nativeswing.swtimpl.components.TrayMessageType;
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
                
                // tray item event test
                TrayItemMouseListener l = new TrayItemMouseListener() {

                    @Override
                    public void onClick(TrayItemMouseEvent e) {
                        System.out.println("click " + (item1 == e.getComponent() ? 1 : 2));
                        JTrayItem i1 = menu1.getTrayItem();
                        JTrayItem i2 = menu2.getTrayItem();
                        System.out.println("s1: " + menu1.getTrayItem() + " " + menu2.getTrayItem());
                        menu1.setTrayItem(i2);
                        System.out.println("s2: " + menu1.getTrayItem() + " " + menu2.getTrayItem());
                        menu2.setTrayItem(i1);
                        System.out.println("s3: " + menu1.getTrayItem() + " " + menu2.getTrayItem());
                        System.out.println("switched: " + (menu1.getTrayItem() != item1));
                        // show message test
                        if (e.isDoubleClick()) item1.showMessage("Exit", "Click to close the application.", TrayMessageType.WARNING, new Runnable() {

                            @Override
                            public void run() {
                                JTray.dispose();
                            }

                        });
                    }

                };
                item1.addMouseListener(l);
                item2.addMouseListener(l);
            }
            
        });
        NativeInterface.runEventPump();
        System.out.println("Event pump has finished.");
    }

}
