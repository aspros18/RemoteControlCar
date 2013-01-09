package org.dyndns.fzoli.rccar.bridge.config;

/**
 * Tiltólista.
 * Azok a tanúsítványok, melyek a listában szerepelnek, nem használhatják a szervet.
 * Amint kapcsolódnak a szerverhez, a szerver elutasítja a kapcsolatot.
 * @see ListConfig
 * @author zoli
 */
class BlocklistConfig extends ListConfig {

    public BlocklistConfig() {
        super("blocklist.conf");
    }
    
}
