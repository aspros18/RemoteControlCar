package org.dyndns.fzoli.rccar.bridge.config;

/**
 * Feketelista járműhasználathoz.
 * Azok a vezérlő kliens-tanúsítványok melyek a listában szerepelnek,
 * nem használhatják a járműve(ke)t, de csatlakozhatnak a szerverhez.
 * A feketelista és a fehérlista egymáshoz való viszonya:
 * - A magasabb rangú bejegyzés érvényesül akkor, ha egy név mindkét listában megtalálható.
 *   Akkor magasabb rangú egy bejegyzés, ha az csoporthoz tartozik, viszont
 *   a másik bejegyzés az alapértemezett felsorolás része, tehát nincs csoportja.
 *   Ebből következik, hogy a csoportba tartozó bejegyzések egyenrangúak.
 * - Egyenrangú bejegyzés esetén a feketelista kerül érvényesítésre.
 *   Két bejegyzés akkor egyenrangú, ha mindkettő ugyan abba a csoportba tartozik.
 *   Ebből következik, hogy az alapértlemezett felsorolásba tartozó bejegyzések egyenrangúak.
 * További információ a fehérlistáról: {@link WhitelistConfig}
 * @see GroupListConfig
 * @author zoli
 */
class BlacklistConfig extends GroupListConfig {

    public BlacklistConfig() {
        super("blacklist.conf");
    }
    
}
