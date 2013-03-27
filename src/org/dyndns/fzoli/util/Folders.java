package org.dyndns.fzoli.util;

import java.io.File;
import org.dyndns.fzoli.ui.UIUtil;

/**
 * Könyvtárakkal kapcsolatos metódusok.
 * @author zoli
 */
public class Folders {
    
    /**
     * Rekurzívan törli a megadott fájlt.
     */
    public static void delete(File f) {
        if (f.isDirectory()) { // ha könyvtár ...
            File[] files = f.listFiles();
            if (files != null) {
                for (File c : files) { // ... a benne lévő összes fájl...
                    delete(c); // ... rekurzív törlése
                }
            }
        }
        if (f.exists()) { // ha a fájlnak már nincs gyermeke és még létezik...
            f.delete(); // ... a fájl törlése
        }
    }
    
    /**
     * Elkészíti a kért fájlnévre mutató fájl-objektumot.
     * Ha az aktuális könyvtárban nem található a megadott fájl, a forrás könyvtárban is megnézi.
     * @return a megtalált fájl vagy az aktuális könyvtárba mutató fájl ill. az alapértelmezés, ha meg van adva
     */
    public static File createFile(String fileName, File def) {
        File f = new File(System.getProperty("user.dir"), fileName);
        if (!f.exists()) {
            try {
                File oldFile = f;
                File srcFile = new File(UIUtil.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                f = new File(srcFile.getParentFile(), fileName);
                if (!f.exists()) f = def == null ? oldFile : def;
            }
            catch (Exception ex) {
                f = null;
            }
        }
        return f;
    }
    
    /**
     * Elkészíti a kért fájlnévre mutató fájl-objektumot.
     * Ha az aktuális könyvtárban nem található a megadott fájl, a forrás könyvtárban is megnézi.
     * @return a megtalált fájl vagy az aktuális könyvtárba mutató fájl
     */
    public static File createFile(String fileName) {
        return createFile(fileName, null);
    }
    
}
