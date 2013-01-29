package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 *
 * Az online járművek és vezérlők adatait tartalmazó lista.
 * @author zoli
 */
public class StorageList {
    
    /**
     * A jármű-adattárolók szálbiztos konténere.
     */
    private static final List<HostStorage> HOSTS = Collections.synchronizedList(new ArrayList<HostStorage>());
    
    /**
     * A vezérlő-adattárolók szálbiztos konténere.
     */
    private static final List<ControllerStorage> CONTROLLERS = Collections.synchronizedList(new ArrayList<ControllerStorage>());

    /**
     * A vezérlők tárolói.
     * Használata iteráláshoz pl. üzenet küldése több vezérlőnek
     */
    public static List<ControllerStorage> getControllerStorageList() {
        return CONTROLLERS;
    }
    
    /**
     * A járművek tárolói.
     * Használata iteráláshoz pl. üzenet küldése több járműnek
     */
    public static List<HostStorage> getHostStorageList() {
        return HOSTS;
    }
    
    /**
     * A jármű azonosítója alapján megkeresi a hozzá tartozó tárolót.
     * @return null, ha nem létezik a tároló
     */
    public static HostStorage findHostStorageByName(String name) {
        return findStorageByName(name, HOSTS);
    }
    
    /**
     * A vezérlő azonosítója alapján megkeresi a hozzá tartozó tárolót.
     * @return null, ha nem létezik a tároló
     */
    public static ControllerStorage findControllerStorageByName(String name) {
        return findStorageByName(name, CONTROLLERS);
    }
    
    /**
     * Létrehoz és tárol egy {@link ControllerStorage} objektumot, vagy beállítja a már létezőt és azzal tér vissza.
     * @param messageProcess a vezérlő jelenlegi üzenetküldője
     */
    public static ControllerStorage createControllerStorage(MessageProcess messageProcess) {
        ControllerStorage s = findControllerStorageByName(messageProcess.getLocalCommonName());
        if (s == null) {
            s = new ControllerStorage(messageProcess);
            getControllerStorageList().add(s);
        }
        else {
            s.setMessageProcess(messageProcess);
        }
        return s;
    }
    
    /**
     * Létrehoz és tárol egy {@link HostStorage} objektumot, vagy beállítja a már létezőt és azzal tér vissza.
     * @param messageProcess a host jelenlegi üzenetküldője
     * @param data a beállított adatmodel
     */
    public static HostStorage createHostStorage(MessageProcess messageProcess, HostData data) {
        HostStorage s = findHostStorageByName(messageProcess.getLocalCommonName());
        if (s == null) {
            s = new HostStorage(messageProcess);
            getHostStorageList().add(s);
        }
        else {
            s.setMessageProcess(messageProcess);
        }
        s.getHostData().update(data);
        return s;
    }
    
    /**
     * Az online járművek listáját generálja le.
     */
    public static HostList createHostList(String controllerName) {
        HostList l = new HostList();
        List<String> ls = l.getHosts();
        for (HostStorage s : getHostStorageList()) {
            if (HostStorage.isHostConnected(s) && Permissions.getConfig().isEnabled(s.getName(), controllerName)) {
                ls.add(s.getName());
            }
        }
        return l;
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
