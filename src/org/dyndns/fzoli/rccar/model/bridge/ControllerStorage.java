package org.dyndns.fzoli.rccar.model.bridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.dyndns.fzoli.rccar.bridge.config.Permissions;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.ControllerState;
import org.dyndns.fzoli.rccar.model.controller.ForwardedList;
import org.dyndns.fzoli.rccar.model.controller.HostList;
import org.dyndns.fzoli.rccar.model.controller.HostState;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * Egy konkrét vezerlő kliens adatait tartalmazó tároló.
 * @author zoli
 */
public class ControllerStorage extends Storage<ControllerData> {

    /**
     * Üzenetküldő a vezérlő oldal irányába.
     * A {@link HostStorage} adatainak módosulása esetén van rá szükség.
     * Mivel a szerver oldalon nincs tárolva a {@link ControllerData},
     * csak részadat küldésre van szükség, helyi adatmentés nincs.
     */
    private final ControllerData SENDER = new ControllerData.ControllerDataSender() {

        /**
         * Elküldi az üzenetet a vezérlőnek.
         * @param msg az üzenet
         */
        @Override
        protected void sendMessage(Serializable msg) {
            ControllerStorage.this.getMessageProcess().sendMessage(msg);
        }
        
    };
    
    private final ControllerData RECEIVER = new ControllerData() {

        private final List<ChatMessage> LS_MSG = new ForwardedList<ChatMessage>(null) {

            @Override
            public boolean add(ChatMessage e) {
                if (!e.data.trim().isEmpty() && getHostStorage() != null) {
                    ChatMessage cm = new ChatMessage(getName(), e.data);
                    getHostStorage().getChatMessages().add(cm);
                    ControllerData.ChatMessagePartialControllerData msg = new ControllerData.ChatMessagePartialControllerData(cm);
                    broadcastMessage(msg, null, false);
                }
                return super.add(e);
            }
            
        };
        
        private void broadcastMessage(PartialBaseData<ControllerData, ?> msgc, PartialBaseData<HostData, ?> msgh, boolean skipMe) {
            if (msgc != null) {
                List<ControllerStorage> l = StorageList.getControllerStorageList();
                for (ControllerStorage cs : l) {
                    if (skipMe && cs == ControllerStorage.this) continue;
                    if (getHostStorage() == cs.getHostStorage()) {
                        cs.getMessageProcess().sendMessage(msgc);
                    }
                }
            }
            if (msgh != null && getHostStorage() != null) {
                getHostStorage().getMessageProcess().sendMessage(msgh);
            }
        }
        
        @Override
        public List<ChatMessage> getChatMessages() {
            return LS_MSG;
        }

        @Override
        public void setControl(Control control) {
            HostStorage hs = getHostStorage();
            if (hs != null && control != null && hs.getHostData().isVehicleConnected() != null && hs.getHostData().isVehicleConnected() && hs.getOwner() == ControllerStorage.this) {
                HostData.ControlPartialHostData msgh = new HostData.ControlPartialHostData(control);
                ControllerData.ControlPartialControllerData msgc = new ControllerData.ControlPartialControllerData(control);
                broadcastMessage(msgc, msgh, true);
            }
        }

        @Override
        public void setHostName(String hostName) {
            HostList ls = StorageList.createHostList(getName());
            if (hostName != null && ls.getHosts().contains(hostName)) {
                HostStorage storage = StorageList.findHostStorageByName(hostName);
                setHostStorage(storage);
                getMessageProcess().sendMessage(createControllerData());
            }
            if (hostName == null) {
                setHostStorage(null);
                getMessageProcess().sendMessage(ls);
            }
        }

        @Override
        public void setWantControl(Boolean wantControl) { // TODO: egyelőre teszt
            getHostStorage().setOwner(wantControl ? ControllerStorage.this : null);
            getSender().setControlling(wantControl);
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
    @Override
    public ControllerData getSender() {
        return SENDER;
    }

    /**
     * A vezérlő által küldött üzeneteket dolgozza fel úgy, hogy a jogkezelt adatmódosítónak üzen a setter metódusok hívásakor.
     */
    @Override
    public ControllerData getReceiver() {
        return RECEIVER;
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
    
    /**
     * Legenerálja a vezérlő kliens oldalára szánt adatmodelt a szerveren tárolt adatok alapján.
     * Ez a metódus akkor hívódik meg, amikor a vezérlő kiválaszt egy konkrét járművet.
     * Ekkor a járműhöz tartozó összes adat (amit ez a metódus gyárt le) átkerül a kliensre.
     */
    public ControllerData createControllerData() {
        HostStorage s = getHostStorage();
        if (s == null) return null;
        ControllerData d = new ControllerData(createControllers(s), new ArrayList<ChatMessage>(s.getChatMessages()));
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
    
    /**
     * A járműhöz tartozó vezérlők listáját generálja le, ami tartalmazza a vezérlők aktuális paramétereit.
     */
    private List<ControllerState> createControllers(HostStorage s) {
        List<ControllerState> l = new ArrayList<ControllerState>();
        if (s == null) return l;
        for (ControllerStorage cs : s.getControllers()) {
            l.add(new ControllerState(cs.getName(), s.getOwner() == this));
        }
        return l;
    }
    
    /**
     * Megadja, hogy a vezérlő korlátozva van-e a vezérlésben a fehérlista alapján.
     */
    public boolean isViewOnly() {
        if (getHostStorage() == null) return false;
        return Permissions.getConfig().isViewOnly(getHostStorage().getName(), getName());
    }
    
    /**
     * Létrehozza a kiválasztott járműhöz tartozó állapotleíró objektumot.
     */
    private HostState createHostState() {
        try {
            HostData d = getHostStorage().getHostData();
            return new HostState(d.getGpsPosition(), getSpeed(d), getAzimuth(d));
        }
        catch (NullPointerException ex) {
            return null;
        }
    }
    
    /**
     * Fokban kifejezve megadja a jármű északtól való eltérését.
     */
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
    
    /**
     * Km/h-ban kifejezve megadja a jármű pillanatnyi sebességét.
     */
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
