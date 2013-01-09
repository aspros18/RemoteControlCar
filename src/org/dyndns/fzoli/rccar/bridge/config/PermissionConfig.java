package org.dyndns.fzoli.rccar.bridge.config;

import java.util.ArrayList;
import java.util.List;

/**
 * A tiltólista, a fehérlista és a feketelista alapján működő jogosultság-konfiguráció.
 * @see BlocklistConfig
 * @see BlacklistConfig
 * @see WhitelistConfig
 * @author zoli
 */
public class PermissionConfig {
    
    private final BlocklistConfig blocklist = new BlocklistConfig();
    private final BlacklistConfig blacklist = new BlacklistConfig();
    private final WhitelistConfig whitelist = new WhitelistConfig();
    
    public boolean isOutdated() {
        return blocklist.isOutdated() || blacklist.isOutdated() || whitelist.isOutdated();
    }
    
    public boolean isBlocked(String name) {
        return blocklist.getValues().contains(name);
    }
    
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
