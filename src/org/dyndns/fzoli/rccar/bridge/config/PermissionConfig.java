package org.dyndns.fzoli.rccar.bridge.config;

import java.util.ArrayList;
import java.util.List;

/**
 * A tiltólista, a fehérlista és a feketelista alapján működő jogosultság-konfiguráció.
 * A három lista összesítése, ami konfig frissítéshez, rangsor létrehozáshoz
 * és jogosultság olvasáshoz használható fel.
 * @see BlocklistConfig
 * @see BlacklistConfig
 * @see WhitelistConfig
 * @author zoli
 */
public class PermissionConfig {
    
    /**
     * Tiltólista.
     */
    private BlocklistConfig blocklist;
    
    /**
     * Feketelista.
     */
    private BlacklistConfig blacklist;
    
    /**
     * Fehérlista.
     */
    private WhitelistConfig whitelist;

    /**
     * Konstruktor, amit a {@link Permissions} osztály is használ.
     * Friss konfiguráció létrehozására használható fel.
     */
    protected PermissionConfig() {
        this(new BlocklistConfig(), new BlacklistConfig(), new WhitelistConfig());
    }
    
    /**
     * Konstruktor, amit csak ez az osztály használ.
     * Paraméterben megadható, milyen konfigurációkat használjon, így új és régi konfig is megadható.
     * @param blocklist a tiltólista
     * @param blacklist a feketelista
     * @param whitelist a fehérlista
     */
    private PermissionConfig(BlocklistConfig blocklist, BlacklistConfig blacklist, WhitelistConfig whitelist) {
        this.blocklist = blocklist;
        this.blacklist = blacklist;
        this.whitelist = whitelist;
    }
    
    /**
     * Frissíti a konfigurációt, ha módosult és a régi konfigurációval tér vissza.
     * @return null, ha a konfiguráció nem módosult, egyébként a frissítés előtti konfiguráció
     */
    protected PermissionConfig refresh() {
        PermissionConfig old = null;
        if (blocklist.isOutdated() || blacklist.isOutdated() || whitelist.isOutdated()) {
            old = new PermissionConfig(blocklist, blacklist, whitelist);
            if (blocklist.isOutdated()) blocklist = new BlocklistConfig();
            if (blacklist.isOutdated()) blacklist = new BlacklistConfig();
            if (whitelist.isOutdated()) whitelist = new WhitelistConfig();
        }
        return old;
    }
    
    /**
     * Megadja, hogy a tanúsítvány-név szerepel-e a tiltólistán.
     * @param name a tanúsítványnév
     */
    public boolean isBlocked(String name) {
        return blocklist.getValues().contains(name);
    }
    
    /**
     * Megadja, hogy az adott járművet az adott vezérlő tudja-e vezérelni, vagy csak figyelni képes.
     * @param vehicleName a jármű tanúsítványneve
     * @param controllerName a vezérlő tanúsítványneve
     * @return true ha csak figyelni képes, egyébként false
     */
    public boolean isViewOnly(String vehicleName, String controllerName) {
        return whitelist.isViewOnly(vehicleName, controllerName);
    }
    
    /**
     * Elkészíti a rangsor listát a fehérlista alapján odafigyelve arra, hogy a feketelista elveheti a jogosultságot.
     * A rangsor listában csakis olyan nevek szerepelnek, melyek nincsenek tiltva a feketelista által vagy a fehérlista felüldefiniálja a tiltást.
     * A metódus soha nem tér vissza null referenciával, legrosszabb esetben is üres lista az eredmény.
     */
    public List<String> getOrderList(String vehicleName) {
        final List<String> l = new ArrayList<String>();
        final List<String> whitevals = whitelist.getGroups().get(vehicleName);
        final List<String> blackvals = blacklist.getGroups().get(vehicleName);
        if (whitevals != null) {
            if (blackvals == null || blackvals.isEmpty()) l.addAll(whitevals);
            else for (String value : whitevals) {
                if (!blackvals.contains(value)) l.add(value);
            }
        }
        for (String value : whitelist.getValues()) {
            if (!blacklist.getValues().contains(value) && (blackvals == null || !blackvals.contains(value)) && !l.contains(value)) l.add(value);
        }
        return l;
    }
    
    /**
     * Akkor van engedélyezve a vezérlő a járműhöz, ha a feketelistában nem szerepel a vezérlő neve, vagy
     * ha a feketelistában szerepel és ennek ellenére a rangsor listában benne van.
     */
    public boolean isEnabled(String vehicleName, String controllerName) {
        if (!blacklist.mergeGroup(vehicleName).contains(controllerName)) return true;
        return getOrderList(vehicleName).contains(controllerName);
    }
    
}
