import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
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

public class JavascriptExecution2 extends JPanel {

  protected static final String LS = System.getProperty("line.separator");
  private static JWebBrowser webBrowser;
  public JavascriptExecution2() {
    super(new BorderLayout());
    JPanel webBrowserPanel = new JPanel(new BorderLayout());
    webBrowserPanel.setBorder(BorderFactory.createTitledBorder("GMap test"));
    webBrowser = new JWebBrowser();
    webBrowser.setBarsVisible(false);
    webBrowser.setStatusBarVisible(true);
    final String htmlContent =
      "<!DOCTYPE html>" +
      "<html>" +
      "  <head>" +
      "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\" />" +
      "    <style type=\"text/css\">" +
      "      html { height: 100% }" +
      "      body { height: 100%; margin: 0; padding: 0; }" +
      "      div#map_canvas, #border { width: 400px; height: 300px }" +
      "      div#border { z-index: 10; position: fixed; top: 0px; left: 0px }" +
      "    </style>" +
      "    <script type=\"text/javascript\" src=\"http://maps.googleapis.com/maps/api/js?&sensor=false\"></script>" +
      "  </head>" +
      "  <body>" +
      "    <div id=\"map_canvas\"></div>" +
      "    <div id=\"border\"></div>" +
      "  </body>" +
      "</html>";
    webBrowser.setHTMLContent(htmlContent);
    webBrowser.addWebBrowserListener(new WebBrowserAdapter() {

          private boolean executed = false;
        
          @Override
          public void statusChanged(WebBrowserEvent e) {
              if (executed) return;
              executed = true;
              try {
                  // Google API betöltési ideje max. 1,5 másodperc
                  Thread.sleep(1500);
              }
              catch(Exception ex) {
                  ;
              }
              e.getWebBrowser().executeJavascript(
                "var latitude = 47.35121, longitude = 19.10265;" + LS +
                "var mapOptions = {" + LS +
                "  zoom: 17," + LS +
                "  center: new google.maps.LatLng(latitude, longitude)," + LS +
                "  disableDefaultUI: true," + LS +
                "  mapTypeId: google.maps.MapTypeId.HYBRID" + LS +
                "}" + LS +
                "var map = new google.maps.Map(document.getElementById(\"map_canvas\"), mapOptions);"
              );
          }
        
    });
    webBrowserPanel.add(webBrowser, BorderLayout.CENTER);
    add(webBrowserPanel, BorderLayout.CENTER);
    JPanel configurationPanel = new JPanel(new BorderLayout());
    configurationPanel.setBorder(BorderFactory.createTitledBorder("Configuration"));
    final JTextArea configurationTextArea = new JTextArea(
      "var latitude = 47.35121, longitude = 19.10265;" + LS +
      "map.setCenter(new google.maps.LatLng(latitude, longitude));"
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
        frame.getContentPane().add(new JavascriptExecution2(), BorderLayout.CENTER);
        frame.setSize(420, 480);
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
      }
      
    });
    NativeInterface.runEventPump();
  }

}