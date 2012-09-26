import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.NativeComponentWrapper;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserAdapter;
import chrriis.dj.nativeswing.swtimpl.components.WebBrowserEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Test2 {

    private static final String LS = System.getProperty("line.separator");
    private static final int MAP_WIDTH = 400, MAP_HEIGHT = 300, RADAR_SIZE = 200, ARROW_SIZE = 30;
    
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
                final JWebBrowser webBrowser = new JWebBrowser(/*JWebBrowser.constrainVisibility(), NSComponentOptions.proxyComponentHierarchy()*/);
                //UIUtils.setComponentTransparencyHint(webBrowser, TransparencyType.TRANSPARENT_WITH_OPAQUE_CHILDREN);
                //new NativeComponentWrapper(webBrowser).createEmbeddableComponent(NSComponentOptions.proxyComponentHierarchy())
                //radarPane.add(webBrowser, BorderLayout.CENTER, JLayeredPane.DEFAULT_LAYER);
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
                    "<!DOCTYPE html>" +
                    "<html>" +
                    "  <head>" +
                    "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\" />" +
                    "    <style type=\"text/css\">" +
                    "      html, body { height: 100% }" +
                    "      body { margin: 0; padding: 0; }" +
                    "      div#map_canvas, #border { width: " + MAP_WIDTH + "px; height: " + MAP_HEIGHT + "px }" +
                    "      div#border, div#arrow { z-index: 10; position: fixed }" +
                    "      div#border { top: 0px; left: 0px }" +
                    "      div#arrow { top: " + ((MAP_HEIGHT / 2) - (ARROW_SIZE / 2)) + "px; left: " + ((MAP_WIDTH / 2) - (ARROW_SIZE / 2)) + "px; width: " + ARROW_SIZE + "px; height: " + ARROW_SIZE + "px }" +
                    "    </style>" +
                    "    <script type=\"text/javascript\" src=\"http://maps.googleapis.com/maps/api/js?&sensor=false\"></script>" +
                    "    <script type=\"text/javascript\">" +
                    "      var size = " + ARROW_SIZE + ";" +
                    "      function rotate(p, angle) {" +
                    "        var centre = {x: size/2, y: size/2};" +
                    "        var rad = (Math.PI / 180) * angle;" +
                    "        var xOld = p.x - centre.x;" +
                    "        var yOld = p.y - centre.y;" +
                    "        return {" +
                    "          x: centre.x + xOld * Math.cos(rad) - yOld * Math.sin(rad)," +
                    "          y: centre.y + xOld * Math.sin(rad) + yOld * Math.cos(rad)" +
                    "        };" +
                    "      }" +
                    "      function setArrow(angle) {" +
                    "        var points = [" +
                    "          {x: size * 0.2, y: size - size * 0.2}," +
                    "          {x: size / 2, y: size * 0.2}," +
                    "          {x: size - size * 0.2, y: size - size * 0.2}," +
                    "          {x: size / 2, y: size * 0.65}" +
                    "        ];" +
                    "        for (var i in points) {" +
                    "          points[i] = rotate(points[i], angle);" +
                    "        }" +
                    "        var text =" +
                    //"          '<embed height=\"' + size + '\" width=\"' + size + '\" type=\"image/svg+xml\">' +" +
                    "          '    <svg version=\"1.1\" baseProfile=\"tiny\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\"' +" +
                    "          '      id=\"svg-root\" height=\"' + size + '\" width=\"' + size + '\" viewBox=\"0 0 ' + size + ' ' + size + '\">' +" +
                    //"          '        <desc>Arrow</desc>' +" +
                    "          '        <polygon points=\"' + points[0].x + ',' + points[0].y + ' ' +" +
                    "                                         points[1].x + ',' + points[1].y + ' ' +" +
                    "                                         points[2].x + ',' + points[2].y + ' ' +" +
                    "                                         points[3].x + ',' + points[3].y + '\"' +" +
                    "          '      id=\"arrow\" fill=\"#33AACC\" stroke=\"#0000AA\" stroke-width=\"' + size / 40 + '\" />' +" +
                    "          '    </svg>' +" +
                    "          '    </svg>';" +
                    //"          '</embed>';" +
                    "        document.getElementById('arrow').innerHTML = text; /*alert(text);*/" +
                    "      }" +
                    "    </script>" +
                    "  </head>" +
                    "  <body>" +
                    "    <div id=\"map_canvas\"></div>" +
                    "    <div id=\"border\"></div>" +
                    "    <div id=\"arrow\"></div>" +
                    "  </body>" +
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
                                "}, 100);" + LS +
                                    
                                "var angle = 0;" + LS +
                                "setArrow(++angle);" + LS +
                                "setInterval(function() {" + LS +
                                "  setArrow(++angle);" + LS +
                                "}, 100);"
                            );
                            frame.setVisible(true);
                        }
                    }

                });
                
                JPanel border = new JPanel();
                radarPane.add(border, JLayeredPane.POPUP_LAYER);
                border.setBackground(Color.red);
                border.setBounds(0, 0, RADAR_SIZE, RADAR_SIZE);
                MouseAdapter ma = new MouseAdapter() {};
                border.addMouseListener(ma);
                border.addMouseMotionListener(ma);
                border.addMouseWheelListener(ma);
                
                frame.getContentPane().add(radarPane, BorderLayout.CENTER);
                frame.pack();
                frame.setLocationByPlatform(true);
            }

        });
        NativeInterface.runEventPump();
    }

}