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

    /**
     * Az alapértelmezett felsorolás view only adatai.
     */
    private final Map<String, Boolean> DEF_VOL;
    
    /**
     * A csoportok és azok felsorolásainak view only adatai.
     */
    private final Map<String, Map<String, Boolean>> GRP_VOL;
    
    /**
     * Konstruktor.
     * Miután az ősben betöltődött a konfiuráció és létrejöttek a csoportok,
     * létrehozza az eredeti felsoroláshoz valamint a csoportokhoz tartozó felsorolásokhoz a view only adatokat.
     * Miután elmentődtek a memóriába a view only adatok, az eredeti felsorolásokból kitörlődnek a view only jelzések: [V]
     * Tehát a konstruktor lefutása után minden felsorolás tiszta tanúsítványneveket fog tartalmazni és a {@link #isViewOnly(String, String)}
     * metódus segítségével lehet megtudni, hogy egy adott értékhez tartozott-e view only jelzés.
     * A csoportok értékei és az eredeti felsorolás értékei ugyan úgy használhatóak, mint pl. a feketelista esetén.
     */
    public WhitelistConfig() {
        super("whitelist.conf");
        GRP_VOL = new HashMap<String, Map<String, Boolean>>();
        DEF_VOL = createViewOnlyList(getValues()); // az alapértelmezett felsorolás view only értékei
        Iterator<Entry<String, List<String>>> it = getGroups().entrySet().iterator();
        while (it.hasNext()) { // végigmegy a csoportokon
            Entry<String, List<String>> e = it.next(); // az aktuális csoport ...
            Map<String, Boolean> vol = createViewOnlyList(e.getValue()); // ... és annak view only értékei
            for (String value : getValues()) { // az alapértelmezett felsorolás értékein megy végig
                // ha a csoport view only értékeiben még nincs definiálva az alapértelmezett felsorolás aktuális értéke,
                if (!vol.containsKey(value)) vol.put(value, DEF_VOL.get(value)); // akkor hozzáadja a hozzá tartozó értéket
            }
            GRP_VOL.put(e.getKey(), vol); // végül az aktuális csoporthoz tárolja a view only értékeket és jöhet a következő csoport
        }
    }
    
    /**
     * Létrehozza a view only adatokat és kiveszi a sorokból a jelöléseket, hogy csak a tanúsítvány neve maradjon benne.
     */
    private static Map<String, Boolean> createViewOnlyList(List<String> values) {
        String value;
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        for (int i = 0; i < values.size(); i++) { // számláló ciklussal megy végig a listán, hogy szerkeszthető maradjon
            value = values.get(i); // az aktuális sor
            if (value.endsWith("[V]") || value.endsWith("[v]")) { // ha a jelölés szerepel a sor végén
                value = value.substring(0, value.length() - 3).trim(); // a jelölés nélküli név
                values.set(i, value); // sor cseréje a jelölés nélküli névre, hogy csak a név maradjon
                map.put(value, true); // a jelölés szerepelt a sorban
            }
            else {
                map.put(value, false); // a jelölés nem szerepelt a sorban
            }
        }
        return map;
    }
    
    /**
     * Megadja, hogy egy adott csoporthoz a felsorolás egyik értéke tartalmazott-e view only jelzést.
     * Ha az érték nem tartozik a csoporthoz, akkor az alapértelmezett felsorolás dönti el.
     * Ha az alapértelmezett felsorolásban se szerepel az érték, akkor nincs view only jel.
     * @param group az adott csoport
     * @param value a felsorolás egyik értéke
     * @return true, ha a felsorolás értéke tartalmaz view only jelzést [V]
     */
    public boolean isViewOnly(String group, String value) {
        Boolean result;
        Map<String, Boolean> lm = GRP_VOL.get(group);
        if (lm == null) return (result = DEF_VOL.get(value)) == null ? false : result;
        else return (result = lm.get(value)) == null ? false : result;
    }
    
}
