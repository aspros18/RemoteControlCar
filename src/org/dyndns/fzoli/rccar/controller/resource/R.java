package org.dyndns.fzoli.rccar.controller.resource;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A vezérlő erőforráskezelő osztálya.
 * @author zoli
 */
public class R {
    
    /**
     * A program ikonjának képét adja vissza.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getIconImage() {
        return getImage("icon.png");
    }
    
    /**
     * Hibaikont tartalmazó kép.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getErrorImage() {
        return getImage("error.png");
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
     * Beolvassa a képfájlt.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    private static BufferedImage getImage(String name) {
        try {
            return ImageIO.read(R.class.getResourceAsStream(name));
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
