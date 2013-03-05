package chrriis.dj.nativeswing.test;

import chrriis.dj.nativeswing.swtimpl.components.core.NativeTray;
import chrriis.dj.nativeswing.swtimpl.components.JTray;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JTrayItem;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseEvent;
import chrriis.dj.nativeswing.swtimpl.components.TrayItemMouseListener;
import java.awt.image.RenderedImage;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.rccar.controller.resource.R;

public class Main {

    private static void autoExit() {
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        JTray.dispose();
                        NativeInterface.close();
                        System.exit(0);
                    }
                
                });
            }
            
        }, 5000);
    }
    
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

                    @Override
                    public void onClick(TrayItemMouseEvent e) {
                        System.out.println((e.isDoubleClick() ? "double" : "single") + " click " + (item1 == e.getComponent() ? 1 : 2));
                    }

                };
                item1.addMouseListener(l);
                item2.addMouseListener(l);
                
                item3.setVisible(true); // exception
            }
            
        });
        autoExit();
        NativeInterface.runEventPump();
    }

}
