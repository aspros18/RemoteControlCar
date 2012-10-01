package org.dyndns.fzoli.rccar.bridge.resource;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * A híd erőforráskezelő osztálya.
 * @author zoli
 */
public class R {
    
    /**
     * Egy hidat ábrázoló képet ad vissza, ami a híd program ikonja.
     */
    public static BufferedImage getBridgeImage() throws IOException {
        return ImageIO.read(R.class.getResourceAsStream("bridge.png"));
    }
    
}
