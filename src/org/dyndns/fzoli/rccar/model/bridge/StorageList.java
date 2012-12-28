package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

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
    
    public static ControllerStorage createControllerStorage(MessageProcess messageProcess, HostStorage hostStorage) {
        ControllerStorage s = findControllerStorageByName(messageProcess.getLocalCommonName());
        if (s == null) s = new ControllerStorage(messageProcess);
        else s.setMessageProcess(messageProcess);
        s.setHostStorage(hostStorage);
        StorageList.getControllerList().add(s);
        return s;
    }
    
    public static HostStorage createHostStorage(MessageProcess messageProcess) {
        HostStorage s = findHostStorageByName(messageProcess.getLocalCommonName());
        if (s == null) s = new HostStorage(messageProcess);
        else s.setMessageProcess(messageProcess);
        StorageList.getHostList().add(s);
        return s;
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
