package chrriis.test;

import java.awt.Component;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import chrriis.dj.nativeswing.swtimpl.core.ControlCommandMessage;
import chrriis.dj.nativeswing.swtimpl.core.SWTNativeComponent;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import javax.swing.JFrame;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

public class NativeTest implements INativeTest {

    private static class TrayComponent extends SWTNativeComponent {
        
        protected static Control createControl(Composite parent, Object[] parameters) {
            return new Composite(parent, SWT.NONE);
        }

        protected Component createEmbeddableComponent() {
            return super.createEmbeddableComponent(new HashMap<Object, Object>());
        }

    };

    private static class CMN_test extends ControlCommandMessage {
        
        private static int id = 0;
        
        @Override
        public Object run(Object[] args) {
            byte[] iconData = (byte[]) args[0];
            Display display = getControl().getDisplay();
            BufferedInputStream inputStreamReader = new BufferedInputStream(new ByteArrayInputStream(iconData));
            ImageData imageData = new ImageData(inputStreamReader);
            Tray tray = display.getSystemTray();
            TrayItem item = new TrayItem(tray, SWT.NONE);
            item.setImage(new Image(display, imageData));
            item.setVisible(true);
            return id++;
        }
        
    }

    private static final TrayComponent CMP;

    static {
        CMP = new TrayComponent();
        new JFrame().getContentPane().add(CMP.createEmbeddableComponent()); // Why is it necessary?
        CMP.initializeNativePeer();
    }

    @Override
    public void test(byte[] icon) {
        System.out.format("test id: %d\n", new CMN_test().syncExec(CMP, icon));
    }

}
