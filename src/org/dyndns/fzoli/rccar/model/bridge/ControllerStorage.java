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
     * A {@link HostStorage#addController(ControllerStorage)} is frissítésre kerül.
     * @param hostStorage a tároló vagy null, ha nincs jármű kiválasztva
     */
    public void setHostStorage(HostStorage hostStorage) {
        HostStorage old = this.hostStorage;
        if (old != null) old.getControllers().remove(this);
        if (hostStorage != null) hostStorage.addController(this);
        this.hostStorage = hostStorage;
    }
    
    public ControllerData createControllerData() {
        HostStorage s = getHostStorage();
        if (s == null) return null;
        ControllerData d = new ControllerData(s.getChatMessages());
        d.setHostState(createHostState());
        d.setHostName(s.getName());
        d.setControlling(s.getOwner() == this);
        d.setWantControl(s.getOwners().contains(this));
        d.setBatteryLevel(s.getHostData().getBatteryLevel());
        d.setVehicleConnected(s.getHostData().isVehicleConnected());
        d.setHostConnected(isHostConnected(s));
        return d;
    }
    
    public static boolean isHostConnected(HostStorage s) {
        return s != null && s.getMessageProcess() != null && !s.getMessageProcess().getSocket().isClosed();
    }
    
    private HostState createHostState() {
        return null; //TODO
    }
    
}
