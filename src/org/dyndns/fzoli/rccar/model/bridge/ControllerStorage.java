package org.dyndns.fzoli.rccar.model.bridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
    
    /**
     * A vezérlő által küldött (illetve a szerver oldalon gyártott) üzeneteket dolgozza fel.
     */
    private final ControllerData RECEIVER = new ControllerData() {

        /**
         * Chatüzenet feldolgozó.
         */
        private final List<ChatMessage> LS_MSG = new ArrayList<ChatMessage>() {

            /**
             * Chatüzenet érkezés esetén üzenet tárolása és továbbítása a vezérlőknek.
             */
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

        /**
         * A vezérlők módosulása nem tartozik a munkamenethez, ezért a lista tesz semmit.
         */
        private final List<ControllerState> LS_CNT = new ArrayList<ControllerState>() {

            /**
             * Nincs hozzáadás.
             */
            @Override
            public boolean add(ControllerState e) {
                return false;
            }

            /**
             * Nincs törlés.
             */
            @Override
            public boolean remove(Object o) {
                return false;
            }

            /**
             * Nincs hozzáadás.
             */
            @Override
            public boolean addAll(Collection<? extends ControllerState> c) {
                return super.addAll(c);
            }

        };

        /**
         * A chatüzenet feldolgozó listával tér vissza.
         */
        @Override
        public List<ChatMessage> getChatMessages() {
            return LS_MSG;
        }

        /**
         * A vezérlőkkel kapcsolatos műveletek nem tartoznak ezen objektum hatáskörébe,
         * ezért egy olyan listával tér vissza, ami nem csinál semmit hozzáadáskor és törléskor.
         */
        @Override
        public List<ControllerState> getControllers() {
            return LS_CNT;
        }

        /**
         * Beállítja a vezérlőjelet, ha a kérő jogosult a vezérlésre és vezérli is a járművet, majd vezérlőjel küldése a klienseknek.
         */
        @Override
        public void setControl(Control control) {
            HostStorage hs = getHostStorage();
            if (hs != null && control != null && hs.getHostData().isVehicleConnected() != null && !ControllerStorage.this.isViewOnly() && hs.getHostData().isVehicleConnected() && hs.getOwner() == ControllerStorage.this) { // ha van rá jogosultság és irányítható a jármű
                hs.incControlCount(); // counter inkrementálása, hogy detektálni lehessen több szál esetén, hogy módosult-e a vezérlőjel
                hs.getHostData().setControl(control); // vezérlőjel beállítása
                HostData.ControlPartialHostData msgh = new HostData.ControlPartialHostData(control); // vezérlőjelet tartalmazó üzenet létrehozása a jármű számára
                ControllerData.ControlPartialControllerData msgc = new ControllerData.ControlPartialControllerData(control); // vezérlőjelet tartalmazó üzenet létrehozása a vezérlők számára
                broadcastMessage(msgc, msgh, true); // üzenet elküldése a vezérlőknek és a járműnek, de a vezérlőjelet küldő vezérlő kihagyásával
            }
        }

        /**
         * Beállítja a járműválasztóban kiválasztott járművet a munkamenethez vagy kilép a járműből.
         * Jármű választásakor ha a vezérlő jogosult a jármű használatához, beállítódik a jármű munkamenete és a kliens megkapja a jármű összes adatát.
         * Jármű elhagyásakor vezérlés lemondatása, majd jármű leválasztása a munkamenetről és járműlista küldése, hogy lehessen újra járművet választani.
         */
        @Override
        public void setHostName(String hostName) {
            HostList ls = StorageList.createHostList(getName());
            if (hostName != null && ls.getHosts().contains(hostName)) { // kapcsolódás a járműhöz: jogosultság ellenőrzés (extra védelem, ha nem támadás történik, nem fordul elő)
                HostStorage storage = StorageList.findHostStorageByName(hostName);
                setHostStorage(storage); // jármű munkamenetének beállítása
                getMessageProcess().sendMessage(createControllerData()); // a kezdeti adatmodel generálása és küldése
            }
            if (hostName == null) { // kilépés a járműből
                if (getHostStorage() != null) { // ha van miből (extra védelem)
                    if (ControllerStorage.this == getHostStorage().getOwner()) setWantControl(false, false); // ha vezérli a járművet, vezérlés lemondása
                    else getHostStorage().getOwners().remove(ControllerStorage.this); // egyébként egyszerű törlés a várólistáról
                }
                setHostStorage(null); // nincs többi jármű kiválasztva a munkamenetben
                getMessageProcess().sendMessage(ls); // a járműlista küldése, hogy a járműválasztó megjelenhessen
            }
        }

        /**
         * Beállítja a jármű vezérlőjét.
         * @see #setWantControl(java.lang.Boolean, boolean)
         */
        @Override
        public void setWantControl(Boolean wantControl) {
            setWantControl(wantControl, true);
        }

        /**
         * Beállítja a jármű vezérlőjét.
         * Ezen metódus segítségével lehet a vezérlést kérni, a kérést visszavonni illetve lemondani a vezérlésről.
         * A vezérlés kérésekor a kérő megkapja a jármű vezérlését, ha van rá jogosultsága.
         * Akkor jogosult egy vezérlő az irányítás megszerzésére, ha:
         * - nincs tíltva a vezérlés
         * - nem vezérlik már a járművet, vagy a jelenlegi vezérlő rangja alacsonyabb
         * Ha az irányítás átvételére jelenleg nem jogosult a vezérlő és nincs neki tiltva a vezérlés, várólistára kerül.
         * Amint a jelenlegi vezérlő lemond a vezérlésről és a listában ő következik, megkapja a vezérlést.
         * Ha a felhasználó idő közben meggondolja magát, lekerülhet a várólistáról, ha visszavonja a kérést.
         * Ha a járművet vezérli a felhasználó, bármikor lemondhat a vezérlésről.
         * A chat ablakban mindig látható, hogy ki az aktuális irányító és rendszerüzenet jelenik meg, ha
         * valaki várólistára került és szeretne vezérelni illetve visszavonta a kérelmet.
         */
        private void setWantControl(Boolean wantControl, boolean fire) {
            if (getHostStorage() == null) return; // ha nincs jármű kiválasztva, nincs min kérni a vezérlést, vagy lemondani a vezérlésről (extra védelem)
            ControllerStorage oldOwner = getHostStorage().getOwner(); // a jelenlegi irányító, aki le lesz váltva, tehát ő a régi irányító
            if (wantControl && oldOwner != null && oldOwner == ControllerStorage.this) return; // ha a kérő vezérlést kért, de már vezérli, nincs teendő
            
            if (!wantControl && oldOwner != null && oldOwner != ControllerStorage.this) { // ha a vezérlés kérést szeretnék visszavonni ...
                getHostStorage().getOwners().remove(ControllerStorage.this); // ... akkor eltávolítás a listából ...
                getSender().setWantControl(false); // ... és jelzés, hogy megtörtént (a vezérléskérő gomb legyen újra aktív)
                broadcastControllerState(new ControllerState(getName(), false, false), !fire); // frissítteti a vezérlő állapotát
                return; // jogtalan vezérlés elkerülése érdekében a metódus végetér
            }
            
            if (wantControl) { // ha a vezérlő kéri az irányítást, engedély ellenőrzés
                // csak azt kell nézni, kérhet-e vezérlést, mert ha nem lenne jogosult a jármű kiválasztására, a járműválasztóban nem történne semmi,
                // így nem választhatná ki a járművet és a munkamenethez nem tartozna jármű (metódus első utasításánál kilépés történik ez esetben)
                if (ControllerStorage.this.isViewOnly()/* || !Permissions.getConfig().isEnabled(getHostStorage().getName(), getName())*/)
                    return; // ha jogosulatlan az irányításra, nincs mit tenni (extra védelem, mert valójában az eredeti kliens programból nem lehet jogosulatlanul vezérlést kérni)
            }
            
            ControllerStorage newOwner = wantControl ? ControllerStorage.this : null; // a kérő lesz az új irányító, ha az irányítást kérte, egyébként ...
            if (newOwner == null && getHostStorage().getOwners().size() > 1) { // ... ha van soron következő, akkor ...
                newOwner = getHostStorage().getOwners().get(1); // ... a soron következő lesz az új irányító
            }
            
            if (wantControl && oldOwner != null && newOwner != null) { // ha szükség van sorrendi jogosultság-ellenőrzésre
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
                    newOwner.getSender().setWantControl(wantControl); // ... visszajelezi a szerver, hogy vége a feldolgozásnak (és újra aktív lehet a gomb), ...
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
        
        /**
         * Egy vezérlő állapotot küld el a vezérlő klienseknek.
         * @param skipMe true esetén a metódus nem küld üzenetet annak a vezérlőnek, akihez a munkamenet tartozik
         */
        private void broadcastControllerState(ControllerState s, boolean skipMe) {
            broadcastMessage(new ControllerData.ControllerChangePartialControllerData(new ControllerChange(s)), null, skipMe);
        }
        
        /**
         * Üzenetet küld az összes vezérlő kliensnek és a jármű kliensnek.
         * @param msgc a vezérlőknek küldendő üzenet
         * @param msgh a jármű kliensnek küldendő üzenet
         * @param skipMe true esetén a metódus nem küld üzenetet annak a vezérlőnek, akihez a munkamenet tartozik
         */
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
     * Megadja a pillanatnyi sebességet km/h-ban.
     */
    private static Double getSpeed(HostData d) {
        return d.getSpeed() == null ? null : d.getSpeed() * 3.6;
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
    
}
