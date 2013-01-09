package org.dyndns.fzoli.rccar.bridge.config;

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

    public GroupListConfig(String fileName) {
        super(fileName);
    }
    
}
