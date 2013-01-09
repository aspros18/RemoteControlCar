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
 * mely a csoportnyító sorban szerepel.
 * A csoport értelemszerűen a következő csoport kezdetéig tart
 * illetve az utolsó csoport a fájl végéig.
 * Ebből következik, hogy ha az első érvényes sor csoportnyító, akkor
 * az alapértelmezett felsorolás üres lesz.
 * @author zoli
 */
class GroupListConfig extends ListConfig {

    private final Map<String, List<String>> GROUPS;
    
    public GroupListConfig(String fileName) {
        super(fileName);
        GROUPS = new HashMap<String, List<String>>();
        List<String> values = null;
        Iterator<String> it = getValues().iterator();
        while (it.hasNext()) {
            String l = it.next();
            if (l.startsWith("[") && l.endsWith("]")) {
                values = new ArrayList<String>();
                GROUPS.put(l.substring(1, l.length() - 1).trim(), values);
                it.remove();
                continue;
            }
            if (values != null) {
                values.add(l);
                it.remove();
            }
        }
    }

    public Map<String, List<String>> getGroups() {
        return GROUPS;
    }
    
}
