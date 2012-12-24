package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * Az online járművek és vezérlők adatait tartalmazó lista.
 * @author zoli
 */
public class StorageList {
    
    private static final List<HostStorage> HOSTS = Collections.synchronizedList(new ArrayList<HostStorage>());
    
    private static final List<ControllerStorage> CONTROLLERS = Collections.synchronizedList(new ArrayList<ControllerStorage>());

    public static List<ControllerStorage> getControllerList() {
        return CONTROLLERS;
    }
    
    public static List<HostStorage> getHostList() {
        return HOSTS;
    }
    
    public static HostStorage findHostStorageByName(String name) {
        return findStorageByName(name, HOSTS);
    }
    
    public static ControllerStorage findControllerStorageByName(String name) {
        return findStorageByName(name, CONTROLLERS);
    }
    
    /**
     * Név alapján megkeresi a tárolót a megadott listában.
     * @param name a név paraméter
     * @param list a lista, amiben keresni kell
     */
    private static <S extends Storage> S findStorageByName(String name, List<S> list) {
        if (name != null) {
            for (S s : list) {
                if (name.equals(s.getName())) return s;
            }
        }
        return null;
    }
    
}
