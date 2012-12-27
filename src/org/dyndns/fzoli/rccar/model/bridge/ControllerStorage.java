package org.dyndns.fzoli.rccar.model.bridge;

import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostState;

/**
 * Egy konkrét vezerlő kliens adatait tartalmazó tároló.
 * @author zoli
 */
public class ControllerStorage implements Storage {
    
    /**
     * A vezérlő neve.
     */
    private final String CONTROLLER_NAME;

    /**
     * A kiválasztott jármű tárolója.
     */
    private HostStorage hostStorage;
    
    public ControllerStorage(String controllerName) {
        CONTROLLER_NAME = controllerName;
    }

    @Override
    public String getName() {
        return CONTROLLER_NAME;
    }

    /**
     * A kiválasztott jármű tárolójával tér vissza.
     * @return null ha nincs jármű kiválasztva
     */
    public HostStorage getHostStorage() {
        return hostStorage;
    }

    /**
     * A kiválasztott jármű tárolóját állítja be.
     * @param hostStorage a tároló vagy null, ha nincs jármű kiválasztva
     */
    public void setHostStorage(HostStorage hostStorage) {
        this.hostStorage = hostStorage;
    }
    
    public ControllerData createControllerData() {
        if (hostStorage == null) return null;
        ControllerData d = new ControllerData(hostStorage.getChatMessages());
        d.setHostState(createHostState());
        d.setHostName(hostStorage.getName());
        d.setBatteryLevel(hostStorage.getHostData().getBatteryLevel());
        //TODO: a többi setter befejezése
        return d;
    }
    
    private HostState createHostState() {
        return null; //TODO
    }
    
}
