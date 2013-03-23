package org.dyndns.fzoli.rccar.controller.resource;

import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * A vezérlő erőforráskezelő osztálya.
 * @author zoli
 */
public class R extends org.dyndns.fzoli.rccar.resource.R {
    
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
    public static Icon getIndicatorIcon() {
        try {
            return new ImageIcon(R.class.getResource("indicator.gif"));
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    /**
     * Fájlnév alapján betölti a képet és ikont ad vissza.
     * @param name a fájl neve
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static ImageIcon getImageIcon(String name) {
        return new ImageIcon(getImage(name));
    }
    
    /**
     * Beolvassa a képfájlt és cacheli a memóriában.
     * @param name a fájl neve
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getImage(String name) {
        return getImage(R.class, name);
    }
    
}
