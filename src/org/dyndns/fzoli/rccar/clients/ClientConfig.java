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
    
}
