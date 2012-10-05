package org.dyndns.fzoli.rccar.controller.resource;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

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
    
}
