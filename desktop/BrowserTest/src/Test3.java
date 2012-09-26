import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.NativeComponentWrapper;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import org.dyndns.fzoli.mobilerc.RadarArrow;

public class Test3 {

    private static final String LS = System.getProperty("line.separator");
    private static final int MAP_WIDTH = 400, MAP_HEIGHT = 300, RADAR_SIZE = 200, ARROW_SIZE = 30;
    
    private static double angle = 0;
    private static final RadarArrow arrow = new RadarArrow(ARROW_SIZE);
    private static final File arrowFile = new File("/home/zoli/arrow.png");
    
    public static void main(String[] args) {
        UIUtils.setPreferredLookAndFeel();
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                final JFrame frame = new JFrame("Radar");
                frame.setResizable(false);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                final JLayeredPane radarPane = new JLayeredPane();
                radarPane.setPreferredSize(new Dimension(RADAR_SIZE, RADAR_SIZE));
                final JWebBrowser webBrowser = new JWebBrowser();
                Component tmp = new NativeComponentWrapper(webBrowser).createEmbeddableComponent();
                radarPane.add(tmp, JLayeredPane.DEFAULT_LAYER);
                tmp.setBounds((-1 * (MAP_WIDTH / 2)) + (RADAR_SIZE / 2), (-1 * (MAP_HEIGHT / 2)) + (RADAR_SIZE / 2), MAP_WIDTH, MAP_HEIGHT);
                webBrowser.setBarsVisible(false);
                webBrowser.setButtonBarVisible(false);
                webBrowser.setLocationBarVisible(false);
                webBrowser.setMenuBarVisible(false);
                webBrowser.setStatusBarVisible(false);
                webBrowser.setJavascriptEnabled(true);
                webBrowser.setHTMLContent(
                    "<!DOCTYPE html>" + LS +
                    "<html>" + LS +
                    "  <head>" + LS +
                    "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\" />" + LS +
                    "    <style type=\"text/css\">" + LS +
                    "      html, body { height: 100% }" + LS +
                    "      body { margin: 0; padding: 0; }" + LS +
                    "      div#map_canvas, #border { width: " + MAP_WIDTH + "px; height: " + MAP_HEIGHT + "px }" + LS +
                    "      div#border, div#arrow { z-index: 10; position: fixed }" + LS +
                    "      div#border { top: 0px; left: 0px }" + LS +
                    "      div#arrow { top: " + ((MAP_HEIGHT / 2) - (ARROW_SIZE / 2)) + "px; left: " + ((MAP_WIDTH / 2) - (ARROW_SIZE / 2)) + "px; width: " + ARROW_SIZE + "px; height: " + ARROW_SIZE + "px }" + LS +
                    "    </style>" + LS +
                    "    <script type=\"text/javascript\" src=\"http://maps.googleapis.com/maps/api/js?&sensor=false\"></script>" + LS +
                    "  </head>" + LS +
                    "  <body>" + LS +
                    "    <div id=\"map_canvas\"></div>" + LS +
                    "    <div id=\"border\"></div>" + LS +
                    "    <div id=\"arrow\"></div>" + LS +
                    "  </body>" + LS +
                    "</html>"
                );
                
                webBrowser.addWebBrowserListener(new WebBrowserAdapter() {

                    @Override
                    public void loadingProgressChanged(WebBrowserEvent e) {
                        if (e.getWebBrowser().getLoadingProgress() == 100) {
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
                                "var map = new google.maps.Map(document.getElementById(\"map_canvas\"), mapOptions);" + LS +

                                "setInterval(function() {" + LS +
                                "  latitude += 0.00001;" + LS +
                                "  map.setCenter(new google.maps.LatLng(latitude, longitude));" + LS +
                                "}, 100);"
                            );
                            frame.setVisible(true);
                        }
                    }

                });
                
                frame.getContentPane().add(radarPane, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationByPlatform(true);
                new Timer().schedule(new TimerTask() {

                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    angle += 2;
                                    arrow.rotate(angle);
                                    ImageIO.write(arrow, "png", arrowFile);
                                    webBrowser.executeJavascript("document.getElementById('arrow').innerHTML = '<img src=\"" + arrowFile.getPath() + "?nocache=" + Math.random() + "\" />';");
                                }
                                catch(Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                            
                        });
                    }
                    
                }, 0, 1000);
            }

        });
        NativeInterface.runEventPump();
    }

}