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
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

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
                if (getHostStorage() != null) {
                    if (ControllerStorage.this == getHostStorage().getOwner()) setWantControl(false, false);
                    else getHostStorage().getOwners().remove(ControllerStorage.this);
                }
                setHostStorage(null);
                getMessageProcess().sendMessage(ls);
            }
        }

        @Override
        public void setWantControl(Boolean wantControl) {
            setWantControl(wantControl, true);
        }

        private void setWantControl(Boolean wantControl, boolean fire) {
            if (getHostStorage() == null) return; // ha nincs jármű kiválasztva, nincs min kérni a vezérlést, vagy lemondani a vezérlésről (extra védelem)
            ControllerStorage oldOwner = getHostStorage().getOwner(); // a jelenlegi irányító, aki le lesz váltva, tehát ő a régi irányító
            if (wantControl && oldOwner != null && oldOwner == ControllerStorage.this) return; // ha a kérő vezérlést kért, de már vezérli, nincs teendő
            
            if (!wantControl && oldOwner != null && oldOwner != ControllerStorage.this) { // ha a vezérlés kérést szeretnék visszavonni ...
                getHostStorage().getOwners().remove(ControllerStorage.this); // ... akkor eltávolítás a listából ...
                getSender().setWantControl(false); // ... és jelzés, hogy megtörtént
                broadcastControllerState(new ControllerState(getName(), false, false), !fire); // frissítteti a vezérlő állapotát
                return; // jogtalan vezérlés elkerülése érdekében a metódus végetér
            }
            
            ControllerStorage newOwner = wantControl ? ControllerStorage.this : null; // a kérő az új irányító, ha az irányítást kérte, egyébként ...
            if (newOwner == null && getHostStorage().getOwners().size() > 1) { // ... ha van soron következő, akkor ...
                newOwner = getHostStorage().getOwners().get(1); // ... a soron következő lesz az új irányító
            }
            
            if (wantControl && oldOwner != null && newOwner != null) { // ha szükség van jogosultság-ellenőrzésre
                int[] indexes = Permissions.getOrders(getHostStorage(), oldOwner, newOwner);
                int oldIndex = indexes[0], newIndex = indexes[1]; // az új és a régi vezérlő rangja (-1 esetén rangtalan)
                if (
                        (oldIndex == -1 && newIndex == -1) // ha a régi és az új vezérlő is rangtalan (tehát egyenrangúak)
                        || // VAGY
                        (oldIndex != -1 && (newIndex == -1 || newIndex > oldIndex)) // ha a régi vezérlő nem rangtalan és az új vezérlő rangtalan vagy kisebb a rangja a réginél (tehát a régi vezérlő magasabb priorítású)
                   )
                { // ha nincs a kérőnek jogosultsága irányítást kérni, várólistára kerül és ehhez meg kell keresni a megfelelő pozíciót, hogy ...
                    List<ControllerStorage> owners = getHostStorage().getOwners(); // ... a lista állandóan rendezetten legyen tartva
                    int pos; // a kérő listában való helye
                    if (newIndex == -1) { // ha rangtalan, akkor ...
                        pos = owners.size(); // ... biztosan a lista végébe kerül, ...
                    }
                    else { // ... de ha van rangja, meg kell keresni a megfelelő pozíciót
                        pos = 0; // kezdetben az első hely van megadva
                        for (ControllerStorage cs : owners) { // a listában lévőkön végigmegy
                            int index = Permissions.getOrder(getHostStorage(), cs); // az aktuális vezérlő rangja
                            if (index == -1) { // ha az aktuális vezérlő rangtalan, ...
                                break; // ... meg van a megfelelő pozíció, kilépés (jelenleg a kérő a legkisebb ranggal rendelkező (vagy az egyetlen ranggal rendelkező) a listában, de mivel van rangja, így a rangtalanok elé kerül)
                            }
                            if (index < newIndex) { // ha az aktuális vezérlő rangja nagyobb, mint az új irányítóé, ...
                                pos++; // ... akkor még tovább kell menni a lista vége felé ...
                            }
                            else { // ... de ha az aktuális vezérlő rangja kisebb, ...
                                break; // ... meg van a megfelelő pozíció, kilépés (a kérőnél vannak nagyobb rangúak a listában, de kisebb rangúak is)
                            }
                        }
                    }
                    owners.add(pos, newOwner); // miután meg van a megfelelő pozíció, várólistára kerül a kérő, ...
                    newOwner.getSender().setWantControl(wantControl); // ... visszajelezi a szerver, hogy vége a feldolgozásnak, ...
                    broadcastControllerState(new ControllerState(getName(), false, true), !fire); // ... és frissítteti a vezérlő állapotát
                    return; // nem fut tovább a metódus, ezzel a jogtalan vezérlést elkerülve
                }
            }
            
            Control c = getHostStorage().getHostData().getControl(); // a jármű vezérlőjelének lekérése, és ...
            if (c != null && (c.getX() != 0 || c.getY() != 0)) setControl(new Control(0, 0)); // ... ha nem alapállapotban áll, alapállapotba helyezés
            
            if (oldOwner != null) { // ha van régi irányító:
                getHostStorage().getOwners().remove(oldOwner); // eltávolítás az irányítók listájából
            }
            
            if (newOwner != null) { // ha van új irányító:
                getHostStorage().getOwners().remove(newOwner); // ha már szerepel a listában, eltávolítás, hogy aztán ...
                getHostStorage().getOwners().add(0, newOwner); // ... a lista első helyére kerüljön, ezáltal irányítóvá válva
            }
            
            if (oldOwner != null) { // jelzés leadása, hogy a régi irányító már nem irányíthat és mivel lekerült a listáról, ha újra vezérelni akar, kérnie kell
                if (fire) { // ha van értelme üzenetet küldeni a változásról
                    oldOwner.getSender().setControlling(false); // mivel kikerül a vezérlők listájából, false küldése
                    oldOwner.getSender().setWantControl(false); // hogy újra kérhessen vezérlést, false küldése
                }
                broadcastControllerState(new ControllerState(oldOwner.getName(), false, false), !fire); // jelzés mindenkinek, hogy a régi irányító már nem irányíthat
            }
            if (newOwner != null) { // jelzés leadása, hogy ki az új irányító, valamint jelzés az új irányítónak, hogy most ő vezérel
                newOwner.getSender().setControlling(true); // true, mivel ő az irányító
                newOwner.getSender().setWantControl(true); // true, hogy lemondhasson a vezérlésről
                broadcastControllerState(new ControllerState(newOwner.getName(), true, true), !fire); // jelzés mindenkinek, hogy ki az új vezérlő
            }
        }
        
        private void broadcastControllerState(ControllerState s, boolean skipMe) {
            broadcastMessage(new ControllerData.ControllerChangePartialControllerData(new ControllerChange(s)), null, skipMe);
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
        d.setConnected(s.isConnected());
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
    private static List<ControllerState> createControllers(HostStorage s) {
        List<ControllerState> l = new ArrayList<ControllerState>();
        if (s == null) return l;
        for (ControllerStorage cs : s.getControllers()) {
            l.add(createControllerState(s, cs));
        }
        return l;
    }
    
    public static ControllerState createControllerState(HostStorage hs, ControllerStorage cs) {
        if (hs == null || cs == null) return null;
        return new ControllerState(cs.getName(), cs == hs.getOwner(), hs.getOwners().contains(cs));
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
