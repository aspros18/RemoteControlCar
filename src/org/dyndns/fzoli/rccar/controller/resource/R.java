package org.dyndns.fzoli.rccar.controller.resource;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A vezérlő erőforráskezelő osztálya.
 * @author zoli
 */
public class R {
    
    /**
     * Az egyszer már betöltött képek referenciáit tárolja.
     */
    private static final Map<String, BufferedImage> IMAGES = new HashMap<String, BufferedImage>();
    
    /**
     * A program ikonjának képét adja vissza.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getIconImage() {
        return getImage("icon.png");
    }
    
    /**
     * Indikátor animációt készít.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static Icon getIndicatorImageIcon() {
        try {
            return new ImageIcon(R.class.getResource("indicator.gif"));
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Beolvassa a képfájlt és cacheli a memóriában.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    private static BufferedImage getImage(String name) {
        synchronized (IMAGES) {
            BufferedImage img = IMAGES.get(name);
            if (img != null) return img;
            try {
                img = ImageIO.read(R.class.getResourceAsStream(name));
                IMAGES.put(name, img);
                return img;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
}
