package chrriis.dj.nativeswing.test;

import chrriis.dj.nativeswing.swtimpl.components.core.NativeTray;
import chrriis.dj.nativeswing.swtimpl.components.JTray;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseListener;
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
                
                // tray item event test
                TrayItemMouseListener l = new TrayItemMouseListener() {

                    int i = 0;
                    
                    @Override
                    public void onClick(TrayItemMouseEvent e) {
                        i++;
                        if (i == 4) JTray.dispose();
                        System.out.println((e.isDoubleClick() ? "double" : "single") + " click " + (item1 == e.getComponent() ? 1 : 2));
                    }

                };
                item1.addMouseListener(l);
                item2.addMouseListener(l);
                
                // exception test
                item3.setVisible(true);
            }
            
        });
        NativeInterface.runEventPump();
        System.out.println("Event pump has finished.");
    }

}
