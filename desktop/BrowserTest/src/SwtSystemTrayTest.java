import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 *
 * @author zoli
 */
public class SwtSystemTrayTest {
    
    public static void main(String[] args) throws Exception {
        Display display = new Display();
        Shell shell = new Shell(display);
        
        Image image = new Image(display, Test.class.getResource("icon.png").toURI().getPath());
        
        final Tray tray = display.getSystemTray();
        if (tray == null) {
          System.out.println("The system tray is not available");
        } else {
          final ToolTip tip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_ERROR);
          tip.setText("Balloon Title goes here.");
          tip.setMessage("Balloon Message Goes Here!");
          final TrayItem item = new TrayItem(tray, SWT.NONE);
          item.setToolTipText("SWT TrayItem");
          item.setToolTip(tip);
          item.addListener(SWT.Show, new Listener() {
              @Override
              public void handleEvent(Event event) {
                  System.out.println("show");
              }
          });
          item.addListener(SWT.Hide, new Listener() {
              @Override
              public void handleEvent(Event event) {
                  System.out.println("hide");
              }
          });
          item.addListener(SWT.Selection, new Listener() {
              @Override
              public void handleEvent(Event event) {
                  System.out.println("selection");
              }
          });
          item.addListener(SWT.DefaultSelection, new Listener() {
              @Override
              public void handleEvent(Event event) {
                  System.out.println("default selection");
                  tip.setVisible(true);
              }
          });
          final Menu menu = new Menu(shell, SWT.POP_UP);
          for (int i = 0; i < 8; i++) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            if (i == 6)
              new MenuItem(menu, SWT.SEPARATOR);
            mi.setText("Item" + i);
            mi.addListener(SWT.Selection, new Listener() {
                @Override
                public void handleEvent(Event event) {
                    System.out.println("selection " + event.widget);
                }
            });
            if (i == 0)
              menu.setDefaultItem(mi);
          }
          item.addListener(SWT.MenuDetect, new Listener() {
              @Override
              public void handleEvent(Event event) {
                  menu.setVisible(true);
              }
          });
          item.setImage(image);
        }
        shell.setBounds(50, 50, 300, 200);
        shell.open();
        while (!shell.isDisposed()) {
          if (!display.readAndDispatch())
            display.sleep();
        }
        image.dispose();
        display.dispose();
    }
    
}
