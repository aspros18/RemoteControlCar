package org.dyndns.fzoli.rccar.clients;

import java.io.File;

/**
 * A {@link ClientConfig} megvalósítóinak a közös metódusait definiáló osztály.
 * @author zoli
 */
public class ClientConfigUtil {
    
    /**
     * Megadja, hogy nyers szöveg alapú legyen-e a kommunikáció.
     * Ha a CA-fájl könyvtárában létezik egy {@code pure.txt} nevű fájl, akkor igazzal tér vissza.
     * @return true esetén {@code Object(I/O)Stream} helyett nyers szöveg és JSON lesz használva.
     */
    public static boolean isPure(ClientConfig cfg) {
        File f = cfg.getCAFile();
        if (f == null) return false;
        f = f.getParentFile();
        if (f == null) return false;
        f = new File(f, "pure.txt");
        return f.exists();
    }
    
}
