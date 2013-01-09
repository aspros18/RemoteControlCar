package org.dyndns.fzoli.rccar.bridge.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.bridge.Config;

/**
 * Fehérlista, feketelista és tiltólista közös metódusai.
 * Mindhárom listának külön állománya van és a konfiguráció soronként értelmezendő.
 * A # jel után a program a következő sorra ugrik, így a konfig fájl kommentezhető.
 * Az objektum által visszaadott lista megtartja a fájlban lévő sorrendet, tehát
 * a nulladik indexű bejegyzés a fájlban az első érvényes sor.
 * Egy sor akkor érvényes, ha annak információ tartalma nem üres, tehát
 * nincs az egész sor kikommentezve, és nem csak szóközből áll.
 * A konfiguráció tanúsítványnevek felsorolását tartalmazza.
 * Ha a konfigurációs fájl nem létezik, a visszaadott lista üres lesz.
 * A fájlokat kézzel kell létrehozni, mert nem szükségesek a szerver működéséhez.
 * @author zoli
 */
class ListConfig {

    private final File FILE_CONFIG;
    private final Long LAST_MODIFIED;
    
    private final List<String> VALUES;
    
    public ListConfig(String fileName) {
        FILE_CONFIG = new File(Config.UD, fileName);
        if (FILE_CONFIG.isFile() && FILE_CONFIG.canRead()) {
            LAST_MODIFIED = FILE_CONFIG.lastModified();
            VALUES = Config.read(FILE_CONFIG, null);
        }
        else {
            LAST_MODIFIED = null;
            VALUES = new ArrayList<String>();
        }
    }

    public List<String> getValues() {
        return VALUES;
    }

    public Long getLastModified() {
        return LAST_MODIFIED;
    }
    
}
