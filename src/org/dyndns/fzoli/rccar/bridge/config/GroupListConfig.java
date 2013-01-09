package org.dyndns.fzoli.rccar.bridge.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Fehérlista és feketelista közös metódusai.
 * Ezzel a listával lehetőség van csoportosításra is,
 * amikoris egy vagy több név összepárosítható egy másik névvel (1-N kapcsolat).
 * A fájl az alapértelmezett felsorolással kezdődik, tehát csoport/blokk nélkül.
 * Csoport nyitásához a tanúsítványnevet [ és ] jelek közé kell tenni.
 * Azok a nevek, melyek ez alá kerülnek, ugyan ahhoz a névhez lesznek párosítva,
 * mely a csoportnyitó sorban szerepel.
 * A csoport értelemszerűen a következő csoport kezdetéig tart
 * illetve az utolsó csoport a fájl végéig.
 * Ebből következik, hogy ha az első érvényes sor csoportnyitó, akkor
 * az alapértelmezett felsorolás üres lesz.
 * @see ListConfig
 * @author zoli
 */
class GroupListConfig extends ListConfig {

    /**
     * A csoportok és azoknak a felsorolásai.
     */
    private final Map<String, List<String>> GROUPS;
    
    /**
     * Konstruktor.
     * Miután az ősben betöltődött a konfiguráció, végignézi a sorokat és
     * létrehozza majd feltölti a csoportokat.
     * Az eredeti felsorolásból kitörli a csoportnyitó sorokat és a csoportba tartozókat,
     * hogy csak az alapértelmezett felsorolás maradjon meg benne.
     */
    public GroupListConfig(String fileName) {
        super(fileName);
        GROUPS = new HashMap<String, List<String>>();
        String l;
        List<String> values = null;
        final Iterator<String> it = getValues().iterator();
        while (it.hasNext()) { // soronként végigmegy
            l = it.next();
            if (l.startsWith("[") && l.endsWith("]")) { // ha a sor csoportnyitó
                values = new ArrayList<String>(); // csoport létrehozása
                GROUPS.put(l.substring(1, l.length() - 1).trim(), values); // csoport tárolása a csoport nevével
                it.remove(); // az eredeti felsorolásból a csoportnyitó sor törlése
                continue; // ugrás a következő sorra
            }
            // innentől a sor biztos, hogy nem csoportnyitó
            if (values != null) { // ha létre van hozva csoport, akkor ez a sor már nem az alapértelmezett felsorolás része
                values.add(l); // ez esetben hozzá kell adni a csoporthoz ...
                it.remove(); // és törölni kell az alapértelmezett felsorolásból
            }
        }
    }

    /**
     * A csoportokat és azoknak a felsorolásait adja vissza.
     */
    public Map<String, List<String>> getGroups() {
        return GROUPS;
    }
    
    /**
     * Készít egy összesített csoportot.
     * Elsőként a csoport felsorolásai kerülnek a listába, ha létezik a csoport.
     * Végül belekerülnek az alapértelmezett felsorolás tagjai is,
     * de csak azok a tagok, melyek nem szerepelnek már a listában.
     */
    protected List<String> mergeGroup(String groupName) {
        final List<String> l = new ArrayList<String>();
        final List<String> values = getGroups().get(groupName);
        if (values != null) l.addAll(values);
        for (String value : getValues()) {
            if (!l.contains(value)) l.add(value);
        }
        return l;
    }
    
}
