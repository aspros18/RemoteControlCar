package org.dyndns.fzoli.rccar.model.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.rccar.model.Data;
import org.dyndns.fzoli.rccar.model.PartialData;

/**
 * A vezérlőnek küldendő lista.
 * Amikor egy vezérlő kapcsolódik a hídhoz és több autó érhető el,
 * a vezérlőnek ki kell választania, melyik autót szeretné irányítani.
 * Ehhez kapja ezt a listát, ami tartalmazza az elérhető autókat.
 * @author zoli
 */
public class HostList extends Data<HostList, HostList.PartialHostList> {
    
    /**
     * A lista változását tartalmazza.
     */
    public static class PartialHostList extends PartialData<HostList, String> {

        /**
         * A változás típusa.
         */
        public static enum ChangeType {
            ADD,
            REMOVE
        }
        
        /**
         * Megadja, hogy hozzáadás, vagy törlés történt.
         */
        private final ChangeType type;
        
        /**
         * Konstruktor.
         * @param data a jármű neve
         * @param type a változás típusa
         */
        public PartialHostList(String data, ChangeType type) {
            super(data);
            this.type = type;
        }
        
        /**
         * A részadatot alkalmazza a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(HostList d) {
            if (d != null && type != null) {
                switch (type) {
                    case ADD:
                        d.getHosts().add(data);
                        break;
                    case REMOVE:
                        d.getHosts().remove(data);
                }
            }
        }
        
    }
    
    /**
     * Az elérhető járművek listája.
     */
    private final List<String> HOSTS = Collections.synchronizedList(new ArrayList<String>());
    
    /**
     * Az elérhető járművekkel tér vissza.
     */
    public List<String> getHosts() {
        return HOSTS;
    }
    
    /**
     * Feltölti a listát a másik lista elemeivel.
     * @param d a másik lista
     */
    @Override
    public void update(HostList d) {
        if (d != null) {
            getHosts().clear();
            getHosts().addAll(d.getHosts());
        }
    }
    
    /**
     * Kinullázza az adatokat, így felszabadulhat a memória.
     */
    @Override
    public void clear() {
        HOSTS.clear();
    }
    
}
