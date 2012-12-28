package org.dyndns.fzoli.rccar.model.bridge;

import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostState;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * Egy konkrét vezerlő kliens adatait tartalmazó tároló.
 * @author zoli
 */
public class ControllerStorage extends Storage {

    /**
     * A kiválasztott jármű tárolója.
     */
    private HostStorage hostStorage;

    public ControllerStorage(MessageProcess messageProcess) {
        super(messageProcess);
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
     * A {@link HostStorage} is frissítésre kerül.
     * @param hostStorage a tároló vagy null, ha nincs jármű kiválasztva
     */
    public void setHostStorage(HostStorage hostStorage) {
        HostStorage old = this.hostStorage;
        if (old != null) old.getControllers().remove(this);
        if (hostStorage != null) hostStorage.addController(this);
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
