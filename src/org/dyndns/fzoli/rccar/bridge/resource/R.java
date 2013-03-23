package org.dyndns.fzoli.rccar.bridge.resource;

import java.awt.image.BufferedImage;

/**
 * A híd erőforráskezelő osztálya.
 * @author zoli
 */
public class R extends org.dyndns.fzoli.rccar.resource.R {
    
    /**
     * Egy hidat ábrázoló képet ad vissza, ami a híd program ikonja.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getBridgeImage() {
        return getImage(R.class, "bridge.png");
    }
    
}
