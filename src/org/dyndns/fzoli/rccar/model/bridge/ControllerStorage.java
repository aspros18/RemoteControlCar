package org.dyndns.fzoli.rccar.model.bridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.ControllerState;
import org.dyndns.fzoli.rccar.model.controller.HostState;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * Egy konkrét vezerlő kliens adatait tartalmazó tároló.
 * @author zoli
 */
public class ControllerStorage extends Storage {

    /**
     * Üzenetküldő a vezérlő oldal irányába.
     */
    private final ControllerData sender = new ControllerData.ControllerDataSender() {

        @Override
        protected void sendMessage(Serializable msg) {
            ControllerStorage.this.getMessageProcess().sendMessage(msg);
        }
        
    };
    
    /**
     * A kiválasztott jármű tárolója.
     */
    private HostStorage hostStorage;

    /**
     * Konstruktor a kezdeti paraméterekkel.
     * @param messageProcess a vezérlő kliens üzenetküldésre alkalmas kapcsolatfeldolgozója
     */
    public ControllerStorage(MessageProcess messageProcess) {
        super(messageProcess);
    }

    /**
     * Olyan üzenetküldő, mely a vezérlő kliensnek küld üzenetet a setter metódusokban.
     */
    public ControllerData getSender() {
        return sender;
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
        ControllerData d = new ControllerData(createControllers(s), s.getChatMessages());
        d.setHostState(createHostState());
        d.setHostName(s.getName());
        d.setViewOnly(isViewOnly());
        d.setControlling(s.getOwner() == this);
        d.setWantControl(s.getOwners().contains(this));
        d.setBatteryLevel(s.getHostData().getBatteryLevel());
        d.setVehicleConnected(s.getHostData().isVehicleConnected());
        d.setHostUnderTimeout(s.isUnderTimeout());
        d.setControl(s.getHostData().getControl());
        d.setFullX(s.getHostData().isFullX());
        d.setFullY(s.getHostData().isFullY());
        d.setUp2Date(s.getHostData().isUp2Date());
        return d;
    }
    
//    TODO: szerver oldalon a vezérlő adat módosulását kezelő üzenetküldő és adatmódosító megírása, feltéve ha a HostStorage-ben lévő DataSender nem kezeli le
//    public ControllerData createControllerDataSender();
    
    private List<ControllerState> createControllers(HostStorage s) {
        List<ControllerState> l = new ArrayList<ControllerState>();
        if (s == null) return l;
        for (ControllerStorage cs : s.getControllers()) {
            l.add(new ControllerState(cs.getName(), s.getOwner() == this));
        }
        return l;
    }
    
    public static boolean isHostConnected(HostStorage s) {
        return s != null && s.getMessageProcess() != null && !s.getMessageProcess().getSocket().isClosed();
    }
    
    public boolean isViewOnly() {
        if (getHostStorage() == null) return false;
        return Permissions.getConfig().isViewOnly(getHostStorage().getName(), getName());
    }
    
    private HostState createHostState() {
        try {
            HostData d = getHostStorage().getHostData();
            return new HostState(d.getGpsPosition(), getSpeed(d), getAzimuth(d));
        }
        catch (NullPointerException ex) {
            return null;
        }
    }
    
    private static Integer getAzimuth(HostData d) {
        if (d != null) {
            Point3D acc = d.getGravitationalField();
            Point3D mag = d.getMagneticField();
            if (acc != null && mag != null) {
                float[] values = new float[3];
                float[] R = new float[9];
                float[] outR = new float[9];
                Location.getRotationMatrix(R, null, acc.toArray(), mag.toArray());
                Location.remapCoordinateSystem(R, Location.AXIS_X, Location.AXIS_Z, outR);
                Location.getOrientation(outR, values);
                return Double.valueOf(Math.toDegrees(values[0])).intValue();
            }
        }
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
