package org.dyndns.fzoli.rccar.clients;

import org.dyndns.fzoli.rccar.Config;

/**
 * Konfiguráció SSL Socket létrehozására kliens oldalon.
 * @author zoli
 */
public interface ClientConfig extends Config {
    
    /**
     * A szerver címét adja meg.
     * @return null, ha nincs beállítva
     */
    public String getAddress();
    
    /**
     * Megadja, hogy nyers szöveg alapú legyen-e a kommunikáció.
     * Ha a CA-fájl könyvtárában létezik egy {@code pure.txt} nevű fájl, akkor igazzal tér vissza.
     * @return true esetén {@code Object(I/O)Stream} helyett nyers szöveg és JSON lesz használva.
     */
    public boolean isPure();
    
}
