package org.dyndns.fzoli.rccar.controller.resource;

import java.awt.image.BufferedImage;
import java.net.URL;
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
        try {
            return ImageIO.read(R.class.getResourceAsStream("icon.png"));
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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
    
}
