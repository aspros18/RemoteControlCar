package org.dyndns.fzoli.rccar.model.bridge;

import java.util.Date;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostState;
import org.dyndns.fzoli.rccar.model.host.HostData;
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
        try {
            HostData d = getHostStorage().getHostData();
            return new HostState(d.getGpsPosition(), getSpeed(d), getBearing(d));
        }
        catch (NullPointerException ex) {
            return null;
        }
    }
    
    private static Integer getBearing(HostData d) {
        // TODO
        return null;
    }
    
    private static Integer getSpeed(HostData d) {
        Double v = null;
        if (d != null) {
            Point3D loc = d.getGpsPosition();
            Point3D loc2 = d.getPreviousGpsPosition();
            Date dat = d.getGpsChangeDate();
            Date dat2 = d.getPreviousGpsChangeDate();
            if (loc != null && loc2 != null && dat != null && dat2 != null) {
                float[] distance = new float[1];
                Location.distanceBetween(loc.X, loc.Y, loc2.X, loc2.Y, distance);
                float s = distance[0]; // [m]
                double t = dat.getTime() - dat2.getTime() / 1000; // [s]
                v = s / t; // [m/s]
                v *= 3.6; // [km/h]
            }
        }
        return v == null ? null : v.intValue();
    }
    
}
