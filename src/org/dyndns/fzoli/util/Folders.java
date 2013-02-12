package org.dyndns.fzoli.util;

import java.io.File;

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
    
}
