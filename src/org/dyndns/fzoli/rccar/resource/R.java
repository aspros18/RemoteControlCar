package org.dyndns.fzoli.rccar.resource;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.dyndns.fzoli.ui.LookAndFeelIcon;
import org.dyndns.fzoli.util.Folders;
import org.dyndns.fzoli.util.OSUtils;
import org.imgscalr.Scalr;

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
     * A megadott képet a megadott szélességűre méretezi át.
     * @param img az átméretezendő kép
     * @param width a szélesség pixelben megadva
     */
    public static BufferedImage resize(BufferedImage img, int width) {
        return Scalr.resize(img, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, width, Scalr.THRESHOLD_QUALITY_BALANCED);
    }
    
    /**
     * Megadja a Mobile-RC alkalmazás felhasználói könyvtárának a helyét.
     */
    public static String getUserDataFolderPath() {
        return OSUtils.getUserDataFolder("Mobile-RC");
    }
    
    /**
     * Megadja a konfig fájl helyét.
     * Megnézi a munkakönyvtárban (current dir) majd a forráskönyvtárban (ahol a jar van)
     * és ahol előbb megtalálja a fájlt, azt az útvonalat adja meg.
     * Ha a fájl nem létezik, akkor a munkakönyvtárba mutató fájlt adja vissza.
     * Mac-en mindig a "user data" könyvtárba fog kerülni a konfig fájl, mert az jobban illik
     * a rendszerhez, valamint a "working directory" is a "user data" könyvtár lesz, így a
     * konfigban megadott relatív útvonal jó helyre fog mutatni.
     * Ahhoz, hogy a log4j naplózása is ebbe a könyvtárba kerüljön a JarBundler-ben előre
     * kell definiálni a "working directory" helyét, mivel ez a beállítás arra nem vonatkozik.
     * @param cfgName a konfig fájl neve
     */
    public static File getConfigFile(String cfgName) {
        if (OSUtils.isOS(OSUtils.OS.MAC)) {
            boolean use = true;
            File dir = new File(R.getUserDataFolderPath());
            if (!dir.isDirectory()) use = dir.mkdirs();
            if (use) {
                Folders.setCurrentDirectory(R.getUserDataFolderPath());
                return new File(dir, cfgName);
            }
        }
        return Folders.createFile(cfgName);
    }
    
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
