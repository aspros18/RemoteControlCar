package org.dyndns.fzoli.rccar.bridge.config;

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
public class ListConfig {
    
}
