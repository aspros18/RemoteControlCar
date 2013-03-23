package org.dyndns.fzoli.rccar.resource;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.dyndns.fzoli.ui.LookAndFeelIcon;

/**
 * A Híd és a vezérlő közös erőforráskezelő osztálya.
 * @author zoli
 */
public class R {
    
    /**
     * Az egyszer már betöltött képek referenciáit tárolja.
     */
    private static final Map<String, BufferedImage> IMAGES = new HashMap<String, BufferedImage>();
    
    /**
     * Fájlnév alapján betölti a képet és ikont ad vissza.
     * @param clazz az osztály, mely könyvtárában a kép található
     * @param name a fájl neve
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static ImageIcon getImageIcon(Class clazz, String name) {
        return new ImageIcon(getImage(clazz, name));
    }
    
    /**
     * Beolvassa a képfájlt és cacheli a memóriában.
     * @param clazz az osztály, mely könyvtárában a kép található
     * @param name a fájl neve
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getImage(Class clazz, String name) {
        synchronized (IMAGES) {
            if (clazz == null) clazz = R.class;
            BufferedImage img = IMAGES.get(name);
            if (img != null) return img;
            try {
                img = ImageIO.read(clazz.getResourceAsStream(name));
                IMAGES.put(name, img);
                return img;
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    /**
     * A kilépés ikon képet adja vissza.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getExitImage() {
        return getImage(R.class, "exit.png");
    }
    
    /**
     * A kérdés ikon képet adja vissza.
     * @throws RuntimeException ha a forrás fájl nem található
     */
    public static BufferedImage getQuestionImage() {
        return getImage(R.class, "question.png");
    }
    
    /**
     * A LookAndFeel hibaikonjával tér vissza.
     */
    public static Icon getErrorIcon() {
        return LookAndFeelIcon.createIcon(null, "OptionPane.errorIcon", null);
    }
    
    /**
     * A LookAndFeel hibaikonjával tér vissza.
     */
    public static Icon getWarningIcon() {
        return LookAndFeelIcon.createIcon(null, "OptionPane.warningIcon", null);
    }
    
}
