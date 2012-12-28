package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 *
 * Az online járművek és vezérlők adatait tartalmazó lista.
 * @author zoli
 */
public class StorageList {
    
    private static final List<HostStorage> HOSTS = Collections.synchronizedList(new ArrayList<HostStorage>());
    
    private static final List<ControllerStorage> CONTROLLERS = Collections.synchronizedList(new ArrayList<ControllerStorage>());

    public static List<ControllerStorage> getControllerStorageList() {
        return CONTROLLERS;
    }
    
    public static List<HostStorage> getHostStorageList() {
        return HOSTS;
    }
    
    public static HostStorage findHostStorageByName(String name) {
        return findStorageByName(name, HOSTS);
    }
    
    public static ControllerStorage findControllerStorageByName(String name) {
        return findStorageByName(name, CONTROLLERS);
    }
    
    public static ControllerStorage createControllerStorage(MessageProcess messageProcess, String hostName) {
        return createControllerStorage(messageProcess, findHostStorageByName(hostName));
    }
    
    public static ControllerStorage createControllerStorage(MessageProcess messageProcess, HostStorage hostStorage) {
        ControllerStorage s = findControllerStorageByName(messageProcess.getLocalCommonName());
        if (s == null) {
            s = new ControllerStorage(messageProcess);
            getControllerStorageList().add(s);
        }
        else {
            s.setMessageProcess(messageProcess);
        }
        s.setHostStorage(hostStorage);
        return s;
    }
    
    public static HostStorage createHostStorage(MessageProcess messageProcess) {
        HostStorage s = findHostStorageByName(messageProcess.getLocalCommonName());
        if (s == null) {
            s = new HostStorage(messageProcess);
            getHostStorageList().add(s);
        }
        else {
            s.setMessageProcess(messageProcess);
        }
        return s;
    }
    
    public static HostList createHostList() {
        HostList l = new HostList();
        List<String> ls = l.getHosts();
        for (HostStorage s : getHostStorageList()) {
            if (ControllerStorage.isHostConnected(s)) {
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
