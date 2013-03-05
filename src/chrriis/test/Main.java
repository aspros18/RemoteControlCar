package chrriis.test;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
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
                System.exit(0);
            }
            
        }, 5000);
    }
    
    public static void main(String[] args) {
        NativeInterface.getConfiguration().addNativeClassPathReferenceClasses(NativeTest.class); // Can this line be automatized? (without calling something before NativeInterface#open) Where should I put this line?
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                RenderedImage testImage = R.getIconImage();
                JTest.test(testImage);
            }
            
        });
        autoExit();
        NativeInterface.runEventPump();
    }

}
