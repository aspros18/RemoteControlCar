package org.dyndns.fzoli.rccar.bridge.config;

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
 * @author zoli
 */
public class WhitelistConfig extends GroupListConfig {
    
}
