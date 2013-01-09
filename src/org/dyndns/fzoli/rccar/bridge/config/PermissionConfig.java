package org.dyndns.fzoli.rccar.bridge.config;

import java.util.List;

/**
 * A tiltólista, a fehérlista és a feketelista alapján működő jogosultság-konfiguráció.
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
    
    public List<String> getOrderList(String vehicleName) {
        return null; //TODO
    }
    
    public boolean isEnabled(String vehicleName, String controllerName) {
        return true; //TODO
    }
    
}
