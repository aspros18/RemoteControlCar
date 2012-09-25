import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class JavascriptExecution extends JPanel {

  protected static final String LS = System.getProperty("line.separator");
  private static JWebBrowser webBrowser;
  public JavascriptExecution() {
    super(new BorderLayout());
    JPanel webBrowserPanel = new JPanel(new BorderLayout());
    webBrowserPanel.setBorder(BorderFactory.createTitledBorder("GMap test"));
    webBrowser = new JWebBrowser();
    webBrowser.setBarsVisible(false);
    webBrowser.setStatusBarVisible(true);
    final String htmlContent =
      "<!DOCTYPE html \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
      "<html xmlns=\"http://www.w3.org/1999/xhtml\">" +
      "  <head>" +
      "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\" />" +
      "    <title>Google Maps JavaScript API Test</title>" +
      "    <script type=\"text/javascript\" src=\"http://maps.google.com/maps?file=api&amp;v=3&amp;sensor=false\"></script>" +
      "  </head>" +
      "  <body onunload=\"GUnload()\">" +
      "    <div id=\"map_canvas\" style=\"width: 500px; height: 300px\"></div>" +
      "  </body>" +
      "</html>";
    webBrowser.setHTMLContent(htmlContent);
    webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
    add(webBrowserPanel, BorderLayout.CENTER);
    JPanel configurationPanel = new JPanel(new BorderLayout());
    configurationPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
    final JTextArea configurationTextArea = new JTextArea(
        "if (GBrowserIsCompatible()) {" + LS +
        "  var map = new GMap2(document.getElementById(\"map_canvas\"));" + LS +
        "  var latitude = 47.35121, longitude = 19.10265, zoom = 17;"+ LS +
        "  map.setCenter(new GLatLng(latitude, longitude), zoom);" + LS +
        "  map.setMapType(G_HYBRID_MAP);" + LS +
        "  map.setUIToDefault();" + LS +
	"  setInterval(function() {" + LS +
        "    latitude += 0.00001;" + LS +
        "    map.setCenter(new GLatLng(latitude, longitude), zoom);" + LS +
        "  }, 100);" + LS +
        "}"
    );
    JScrollPane scrollPane = new JScrollPane(configurationTextArea);
    Dimension preferredSize = scrollPane.getPreferredSize();
    preferredSize.height += 20;
    scrollPane.setPreferredSize(preferredSize);
    configurationPanel.add(scrollPane, BorderLayout.CENTER);
    JPanel configurationButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
    JButton executeJavascriptButton = new JButton("Execute Javascript");
    executeJavascriptButton.addActionListener(new ActionListener() {
        
      @Override
      public void actionPerformed(ActionEvent e) {
        webBrowser.executeJavascript(configurationTextArea.getText());
      }
      
    });
    configurationButtonPanel.add(executeJavascriptButton);
    configurationPanel.add(configurationButtonPanel, BorderLayout.SOUTH);
    add(configurationPanel, BorderLayout.NORTH);
  }

  public static void main(String[] args) throws Exception {
    UIUtils.setPreferredLookAndFeel();
    NativeInterface.open();
    SwingUtilities.invokeLater(new Runnable() {
        
      @Override
      public void run() {
        JFrame frame = new JFrame("DJ Native Swing GMap Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new JavascriptExecution(), BorderLayout.CENTER);
        frame.setSize(800, 630);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
      
    });
    NativeInterface.runEventPump();
  }

}