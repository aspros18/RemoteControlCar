package org.dyndns.fzoli.rccar.bridge.resource;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * A híd erőforráskezelő osztálya.
 * @author zoli
 */
public class R {
    
    /**
     * Egy hidat ábrázoló képet ad vissza, ami a híd program ikonja.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getBridgeImage() {
        try {
            return ImageIO.read(getBridgeImageStream());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Egy hidat ábrázoló kép folyamát adja vissza.
     */
    private static InputStream getBridgeImageStream() {
        return R.class.getResourceAsStream("bridge.png");
    }
    
}
