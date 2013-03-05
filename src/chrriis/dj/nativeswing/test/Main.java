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
                JTrayItem item1 = JTray.createTrayItem(testImage, "First");
                JTrayItem item2 = JTray.createTrayItem(testImage, "Second");
                System.out.println(item1.getTooltip());
                item1.setTooltip("First changed");
                System.out.println(item1.getTooltip());
                item2.setImage(org.dyndns.fzoli.rccar.bridge.resource.R.getBridgeImage());
                item1.addMouseListener(new TrayItemMouseListener() {

                    @Override
                    public void onClick(TrayItemMouseEvent e) {
                        System.out.println("single click");
                    }

                    @Override
                    public void onDoubleClick(TrayItemMouseEvent e) {
                        System.out.println("double click");
                    }
                    
                });
            }
            
        });
        autoExit();
        NativeInterface.runEventPump();
    }

}
