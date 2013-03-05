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
        NativeInterface.getConfiguration().addNativeClassPathReferenceClasses(NativeTray.class); // Can this line be automatized? (without calling something before NativeInterface#open) Where should I put this line?
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                RenderedImage testImage = R.getIconImage();
                final JTrayItem item1 = JTray.createTrayItem(testImage, "First");
                final JTrayItem item2 = JTray.createTrayItem(testImage, "Second");
                final JTrayItem item3 = JTray.createTrayItem();
                System.out.println("item1 visible: " + item1.isVisible());
                System.out.println("item3 visible: " + item3.isVisible());
                
                System.out.println("item1 tip: " + item1.getTooltip());
                item1.setTooltip("First changed");
                System.out.println("item1 tip: " + item1.getTooltip());
                System.out.println("item2 tip: " + item2.getTooltip());
                
                item2.setImage(org.dyndns.fzoli.rccar.bridge.resource.R.getBridgeImage());
                
                TrayItemMouseListener l = new TrayItemMouseListener() {

                    int i = 1;
                    
                    @Override
                    public void onClick(TrayItemMouseEvent e) {
                        if (i >= 5) System.exit(0);
                        System.out.println((e.isDoubleClick() ? "double" : "single") + " click " + (item1 == e.getComponent() ? 1 : 2));
                        i++;
                    }

                };
                item1.addMouseListener(l);
                item2.addMouseListener(l);
                
                item3.setVisible(true); // exception
            }
            
        });
        NativeInterface.runEventPump();
    }

}
