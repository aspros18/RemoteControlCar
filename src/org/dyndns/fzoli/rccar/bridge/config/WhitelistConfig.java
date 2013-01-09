package org.dyndns.fzoli.rccar.bridge.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Fehérlista járműhasználathoz.
 * Azok a vezérlő kliens-tanúsítványok melyek a listában szerepelnek,
 * engedélyt kapnak a jármű(vek) használatához.
 * A felsorolás sorrendje határozza meg a felhasználó rangját:
 * akik előrébb vannak a fájlban, azok nagyobb ranggal rendelkeznek, mint a lentebb lévők.
 * Alap esetben az összes vezérlő jogosult a jármű irányítására, a magasabb rangú felhasználó
 * bármikor átveheti a vezérlést az alacsonyabb rangútól.
 * Ebből következik, hogy az aktív irányítóval azonos rangú felhasználó nem kap addig vezérlést, míg
 * az aktuális felhasználó le nem mond az irányításról.
 * Ranggal nem rendelkező (fehér listában nem szereplő) felhasználó is kérheti a vezérlést.
 * A ranggal nem rendelkező felhasználók egyenrangúak és a ranggal rendelkezőek után következnek a rangsorban.
 * Az egyenrangú felhasználók esetén az kap vezérlést, aki előbb kérte azt, tehát ha a felhasználó lemond a vezérlésről,
 * az időben őt követő kapja meg a vezérlést, ha van ilyen (különben vezérlés nélkül marad).
 * Ha a járművet még senki nem figyeli (nincs csatlakozva rá senki), a szerver az első kliensnek aki a járművet válassza
 * aktív vezérlést ad, ha jogosult rá.
 * Ha a jármű nincs vezérlés alatt, bárki kérhet vezérlést, aki jogosult rá, de természetesen a magasabb rangú felhasználó
 * bármikor elveheti tőle a vezérlést.
 * A konfigurációban megadható az is, hogy a kliens ne vezérelhesse a járműve(ke)t, de ahhoz
 * megmarad a joga, hogy figyelhesse és chateljen a jármű csatornáján. (Ez a spectator mode.)
 * A fehérlista önmagában nem ad elegendő információt arról, hogy valójában kiknek van jogosultsága
 * egy jármű használatához, mert feketelista is létezik, mely elveheti a beállított jogosultságot.
 * További információ a feketelistáról: {@link BlacklistConfig}
 * @see GroupListConfig
 * @author zoli
 */
class WhitelistConfig extends GroupListConfig {

    private final Map<String, Boolean> DEF_LIMITS;
    private final Map<String, Map<String, Boolean>> GRP_LIMITS;
    
    public WhitelistConfig() {
        super("whitelist.conf");
        GRP_LIMITS = new HashMap<String, Map<String, Boolean>>();
        DEF_LIMITS = createViewOnlyList(getValues());
        Map<String, Boolean> tmpViews;
        Entry<String, List<String>> e;
        Iterator<Entry<String, List<String>>> it = getGroups().entrySet().iterator();
        while (it.hasNext()) {
            e = it.next();
            tmpViews = createViewOnlyList(e.getValue());
            for (String value : getValues()) {
                if (!tmpViews.containsKey(value)) tmpViews.put(value, DEF_LIMITS.get(value));
            }
            GRP_LIMITS.put(e.getKey(), tmpViews);
        }
    }
    
    private static Map<String, Boolean> createViewOnlyList(List<String> values) {
        String value;
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        for (int i = 0; i < values.size(); i++) {
            value = values.get(i);
            if (value.endsWith("[V]") || value.endsWith("[v]")) {
                value = value.substring(0, value.length() - 3).trim();
                values.set(i, value);
                map.put(value, true);
            }
            else {
                map.put(value, false);
            }
        }
        return map;
    }
    
    public boolean isViewOnly(String group, String value) {
        Boolean result;
        Map<String, Boolean> lm = GRP_LIMITS.get(group);
        if (lm == null) return (result = DEF_LIMITS.get(value)) == null ? false : result;
        else return (result = lm.get(value)) == null ? false : result;
    }
    
}
