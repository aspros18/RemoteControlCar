import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
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
    
    private static final BufferedImage IMG_TEST = new BufferedImage(300, 300, BufferedImage.TYPE_INT_ARGB) {
        {
            Graphics2D g = (Graphics2D) getGraphics();
            g.setColor(Color.BLACK);
            BasicStroke stroke = new BasicStroke(15);
            g.fill(stroke.createStrokedShape(new Ellipse2D.Float(0, 0, 300, 300)));
            g.setColor(Color.WHITE);
            g.fill(stroke.createStrokedShape(new Ellipse2D.Float(75, 75, 150, 150)));
        }
    };
    
    private static ImageData convertToSWT(BufferedImage bufferedImage) {
	if (bufferedImage.getColorModel() instanceof DirectColorModel) {
		DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
		PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
		ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				int rgb = bufferedImage.getRGB(x, y);
				int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF)); 
				data.setPixel(x, y, pixel);
				if (colorModel.hasAlpha()) {
					data.setAlpha(x, y, (rgb >> 24) & 0xFF);
				}
			}
		}
		return data;		
	} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
		IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
		int size = colorModel.getMapSize();
		byte[] reds = new byte[size];
		byte[] greens = new byte[size];
		byte[] blues = new byte[size];
		colorModel.getReds(reds);
		colorModel.getGreens(greens);
		colorModel.getBlues(blues);
		RGB[] rgbs = new RGB[size];
		for (int i = 0; i < rgbs.length; i++) {
			rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
		}
		PaletteData palette = new PaletteData(rgbs);
		ImageData data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
		data.transparentPixel = colorModel.getTransparentPixel();
		WritableRaster raster = bufferedImage.getRaster();
		int[] pixelArray = new int[1];
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				raster.getPixel(x, y, pixelArray);
				data.setPixel(x, y, pixelArray[0]);
			}
		}
		return data;
	}
	return null;
    }
    
    private static boolean isSwtTrayAvailable() {
        try {
            Class.forName("org.eclipse.swt.widgets.Tray", false, SwtSystemTrayTest.class.getClassLoader());
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
    public static void main(String[] args) throws Exception {
        if (isSwtTrayAvailable()) {
            final Display display = new Display();
            final Shell shell = new Shell(display);

            final Image image = new Image(display, convertToSWT(IMG_TEST));
            final Tray tray = display.getSystemTray();
            if (tray == null) {
              System.out.println("The system tray is not available");
            }
            else {
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
                final MenuItem mi = new MenuItem(menu, SWT.PUSH);
                if (i == 6) {
                  new MenuItem(menu, SWT.SEPARATOR);
                }
                mi.setText("Item" + i);
                mi.addListener(SWT.Selection, new Listener() {
                    @Override
                    public void handleEvent(Event event) {
                        System.out.println("selection " + event.widget);
                        if (mi.getText().equals("Item7")) {
                            shell.dispose();
                        }
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
//            shell.setBounds(50, 50, 300, 200);
//            shell.open();
            while (!shell.isDisposed()) {
              if (!display.readAndDispatch()) {
                display.sleep();
              }
            }
            image.dispose();
            display.dispose();
        }
        else {
            SystemTray tray = SystemTray.getSystemTray();
            TrayIcon icon = new TrayIcon(IMG_TEST);
            icon.setImageAutoSize(true);
            tray.add(icon);
        }
    }
    
}
