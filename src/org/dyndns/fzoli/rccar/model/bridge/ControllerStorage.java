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

        private final List<ChatMessage> LS_MSG = new ArrayList<ChatMessage>() {

            @Override
            public boolean add(ChatMessage e) {
                if (!e.data.trim().isEmpty() && getHostStorage() != null) {
                    ChatMessage cm = new ChatMessage(getName(), e.data);
                    getHostStorage().getChatMessages().add(cm);
                    broadcastMessage(new ControllerData.ChatMessagePartialControllerData(cm), null, false);
                }
                return false;
            }
            
        };

        private final List<ControllerState> LS_CNT = new ArrayList<ControllerState>() {

            @Override
            public boolean add(ControllerState e) {
//                sendControllerChange(new ControllerChange(e));
                return false;
            }

            @Override
            public boolean remove(Object o) {
//                sendControllerChange(new ControllerChange(o.toString()));
                return false;
            }
            
//            private void sendControllerChange(ControllerChange change) {
//                broadcastMessage(new ControllerData.ControllerChangePartialControllerData(change), null, false);
//            }
            
        };
        
        @Override
        public List<ChatMessage> getChatMessages() {
            return LS_MSG;
        }

        @Override
        public List<ControllerState> getControllers() {
            return LS_CNT;
        }

        @Override
        public void setControl(Control control) {
            HostStorage hs = getHostStorage();
            if (hs != null && control != null && hs.getHostData().isVehicleConnected() != null && !ControllerStorage.this.isViewOnly() && hs.getHostData().isVehicleConnected() && hs.getOwner() == ControllerStorage.this) {
                hs.getHostData().setControl(control);
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
                if (getHostStorage() != null && ControllerStorage.this == getHostStorage().getOwner()) setWantControl(false, false);
                setHostStorage(null);
                getMessageProcess().sendMessage(ls);
            }
        }

        @Override
        public void setWantControl(Boolean wantControl) {
            setWantControl(wantControl, true);
        }

        private void setWantControl(Boolean wantControl, boolean fire) { // TODO: egyelőre teszt, bárki kérhet vezérlést és azonnal meg is kapja
            ControllerStorage oldOwner = getHostStorage().getOwner();
            if (oldOwner != null && fire) {
                oldOwner.getSender().setControlling(false);
                oldOwner.getSender().setWantControl(false);
                broadcastControllerState(new ControllerState(oldOwner.getName(), false));
            }
            getHostStorage().setOwner(wantControl ? ControllerStorage.this : null);
            if (fire) getSender().setControlling(wantControl);
            if (wantControl) broadcastControllerState(new ControllerState(getName(), true));
            Control c = getHostStorage().getHostData().getControl();
            if (c != null && (c.getX() != 0 || c.getY() != 0)) setControl(new Control(0, 0));
        }
        
        private void broadcastControllerState(ControllerState s) {
            broadcastMessage(new ControllerData.ControllerChangePartialControllerData(new ControllerChange(s)), null, false);
        }
        
        private void broadcastMessage(PartialBaseData<ControllerData, ?> msgc, PartialBaseData<HostData, ?> msgh, boolean skipMe) {
            HostStorage hs = getHostStorage();
            if (msgc != null && hs != null) {
                List<ControllerStorage> l = StorageList.getControllerStorageList();
                for (ControllerStorage cs : l) {
                    if (skipMe && cs == ControllerStorage.this) continue;
                    if (hs == cs.getHostStorage()) {
                        cs.getMessageProcess().sendMessage(msgc);
                    }
                }
            }
            if (msgh != null && hs != null) {
                hs.getMessageProcess().sendMessage(msgh);
            }
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
        HostStorage oldStorage = this.hostStorage;
        if (oldStorage != null) oldStorage.removeController(this);
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
        d.setHostState(createHostState(s));
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
            l.add(new ControllerState(cs.getName(), cs == s.getOwner()));
        }
        return l;
    }
    
    /**
     * Megadja, hogy a vezérlő korlátozva van-e a vezérlésben a fehérlista alapján.
     */
    private boolean isViewOnly() {
        if (getHostStorage() == null) return false;
        return Permissions.getConfig().isViewOnly(getHostStorage().getName(), getName());
    }
    
    /**
     * Létrehozza a paraméterben megadott járműhöz tartozó állapotleíró objektumot.
     */
    public static HostState createHostState(HostStorage hs) {
        try {
            HostData d = hs.getHostData();
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
                int degree = Double.valueOf(Math.toDegrees(values[0])).intValue();
                if (d.getAdditionalDegree() != null) degree += d.getAdditionalDegree();
                return degree;
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
