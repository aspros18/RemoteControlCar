package org.dyndns.fzoli.rccar.model.bridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.rccar.bridge.socket.ControllerSideDisconnectProcess;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

/**
 * Egy konkrét jármű összes adatát tartalmazó osztály.
 * Amikor egy jármű kapcsolódik a hídhoz, létrejön ezen osztály egy példánya,
 * és a létrejött példány bekerül az elérhető járművek listájába.
 * Amikor a vezérlő kapcsolódik a hídhoz, a híd megnézi, hány jármű érhető el.
 * Ha csak 1 jármű, akkor automatikusan kiválasztja azt az egyet,
 * ha több érhető el, listát kap róla a vezérlő és választhat járművet.
 * Miután ki lett választva a jármű, ezen osztály adatai alapján generálódik
 * egy munkamenet a vezérlő számára, ami tartalmazza a kiválasztott járműről az
 * összes adatot, ami csak kellhet.
 * Ha a járművel megszakad a kapcsolat, figyelmeztetve lesz az összes vezérlő.
 * Ha a jármű visszatér, minden folytatódik tovább.
 * Ha a jármű kilép, a híd megszünteti a HostStorge objektumot és
 * a hozzá tartozó vezérlőknek elküldi újra az elérhető járművek listáját.
 * Amíg a vezérlő kapcsolódva van a hídhoz, de még nem tartozik egy járműhöz sem
 * - tehát a jármű választás van folyamatban -, addig a jármű lista változásáról
 * folyamatosan tályékoztatva van a vezérlő.
 * @author zoli
 */
public class HostStorage extends Storage<HostData> {
    
    /**
     * A jármű adatai.
     */
    private final HostData HOST_DATA = new HostData();
    
    /**
     * Üzenetküldő implementáció, ami a járműnek küld üzenetet.
     * A helyi adatot nem módosítja, mert nem minden esetben van arra szükség.
     * Csak azon setter metódusok vannak megírva, melyek üzenetküldésre használatosak.
     */
    private final HostData SENDER = new HostData() {
        
        /**
         * Beállítja és elküldi a paraméterben megadott vezérlőjelet.
         */
        @Override
        public void setControl(Control controll) {
            sendMessage(new HostData.ControlPartialHostData(controll));
            getHostData().setControl(controll);
        }

        /**
         * Beállítja és elküldi, hogy a streamelés folyamatban van-e.
         */
        @Override
        public void setStreaming(Boolean streaming) {
            sendMessage(new HostData.BooleanPartialHostData(streaming, HostData.BooleanPartialHostData.BooleanType.STREAMING));
            getHostData().setStreaming(streaming);
        }
        
        /**
         * Elküldi az üzenetet a járműnek.
         */
        private void sendMessage(Serializable msg) {
            HostStorage.this.getMessageProcess().sendMessage(msg);
        }
        
    };
    
    /**
     * A jármű által küldött (illetve a szerver oldalon gyártott) üzeneteket dolgozza fel.
     */
    private final HostData RECEIVER = new HostData() {

        /**
         * Beállítja a pillanatnyi sebességet.
         * Nem kell elküldeni a klienseknek, mert a GPS-pozíció változásakor küldődik ki.
         */
        @Override
        public void setSpeed(Double speed) {
            getHostData().setSpeed(speed);
        }

        /**
         * Beállítja és elküldi a vezérlőjelet a járműhöz tartozó vezérlő-klienseknek.
         */
        @Override
        public void setControl(Control controll) {
            getHostData().setControl(controll);
            broadcastMessage(new ControllerData.ControlPartialControllerData(controll));
        }

        /**
         * Beállítja, hogy kapcsolódva van-e a jármű és jelez a járműhöz tartozó vezérlő-klienseknek.
         */
        @Override
        public void setVehicleConnected(Boolean vehicleConnected) {
            getHostData().setVehicleConnected(vehicleConnected);
            broadcastMessage(new ControllerData.BoolenPartialControllerData(vehicleConnected, ControllerData.BoolenPartialControllerData.BooleanType.VEHICLE_CONNECTED));
        }

        /**
         * Beállítja, hogy a GPS-jel naprakész-e és jelez a járműhöz tartozó vezérlő-klienseknek.
         */
        @Override
        public void setUp2Date(Boolean up2date) {
            getHostData().setUp2Date(up2date);
//            System.out.println("Vehicle " + getName() + " is" + (up2date ? " " : "n't ") + "up to date.");
            broadcastMessage(new ControllerData.BoolenPartialControllerData(up2date, ControllerData.BoolenPartialControllerData.BooleanType.UP_2_DATE));
        }

        /**
         * Beállítja az akkumulátor-szintet és jelez a járműhöz tartozó vezérlő-klienseknek.
         */
        @Override
        public void setBatteryLevel(Integer batteryLevel) {
            getHostData().setBatteryLevel(batteryLevel);
            broadcastMessage(new ControllerData.BatteryPartialControllerData(batteryLevel));
        }

        /**
         * Beállítja a GPS-pozíciót és jelez a járműhöz tartozó vezérlő-klienseknek, ha nem módosul több adat.
         */
        @Override
        public void setGpsPosition(Point3D gpsPosition) {
            getHostData().setGpsPosition(gpsPosition);
            broadcastHostState();
        }

        /**
         * Beállítja a gravitációs térerősséget és jelez a járműhöz tartozó vezérlő-klienseknek, ha nem módosul több adat.
         */
        @Override
        public void setGravitationalField(Point3D gravitationalField) {
            getHostData().setGravitationalField(gravitationalField);
            broadcastHostState();
        }

        /**
         * Beállítja a mágneses térerősséget és jelez a járműhöz tartozó vezérlő-klienseknek, ha nem módosul több adat.
         */
        @Override
        public void setMagneticField(Point3D magneticField) {
            getHostData().setMagneticField(magneticField);
            broadcastHostState();
        }

        /**
         * A gravitációs térerrősség, mágneses térerősség és GPS pozíció beállítása alapján gyárt egy helyzeti állapotot, amit elküld a vezérlő-klienseknek.
         * Ha még nem állítódott be mindhárom adat, nem küld fölöslegesen adatot.
         */
        private void broadcastHostState() {
            if (!isPointChanging()) {
                broadcastMessage(new ControllerData.HostStatePartialControllerData(ControllerStorage.createHostState(HostStorage.this)));
            }
        }

    };
    
    /**
     * A járműhöz tartozó chatüzenetek.
     */
    private final List<ChatMessage> CHAT_MESSAGES = Collections.synchronizedList(new ArrayList<ChatMessage>() {

        /**
         * Maximum ennyi üzenetet tárol a szerver.
         */
        private static final int MAX_SIZE = 50;
        
        /**
         * Chatüzenet hozzáadása és a legrégebbi üzenet törlése, ha az üzenetek száma elérte a korlátot.
         */
        @Override
        public boolean add(ChatMessage e) {
            boolean result = super.add(e);
            if (size() > MAX_SIZE) removeRange(0, size() - (MAX_SIZE + 1));
            return result;
        }
        
    });
    
    /**
     * A járművet irányítani akarók.
     */
    private final List<ControllerStorage> OWNERS = Collections.synchronizedList(new ArrayList<ControllerStorage>());
    
    /**
     * Azok a vezérlők, melyek a járművet kiválasztották.
     */
    private final List<ControllerStorage> CONTROLLERS = Collections.synchronizedList(new ArrayList<ControllerStorage>());
    
    /**
     * Megadja, hogy a tárolóhoz tartozik-e élő kapcsolat.
     */
    private boolean connected = false;
    
    /**
     * A jármű kapcsolata időtúllépés alatt van-e.
     */
    private boolean underTimeout = false;
    
    /**
     * Azt adja meg, hány alkalommal lett a vezérlőjel beállítva az üzenetküldés során.
     */
    private int controlCount = 0;
    
    /**
     * Konstruktor a kezdeti paraméterek megadásával.
     */
    public HostStorage(MessageProcess messageProcess) {
        super(messageProcess);
    }

    /**
     * A járműnek lehet üzenetet küldeni ezzel az objektummal a setter metódusok használatával.
     */
    @Override
    public HostData getSender() {
        return SENDER;
    }

    /**
     * A jármű által küldött üzeneteket dolgozza fel úgy, hogy a jogkezelt adatmódosítónak üzen a setter metódusok hívásakor.
     */
    @Override
    public HostData getReceiver() {
        return RECEIVER;
    }

    /**
     * A jármű jelenlegi irányítója.
     * (Az irányításra rangsorolt vezérlők közül az első.)
     */
    public ControllerStorage getOwner() {
        if (OWNERS.isEmpty()) return null;
        return OWNERS.get(0);
    }

    /**
     * A jármű vezérlésére jelentkezett kliensek sorba rendezve.
     * A lista legelején lévő irányíthatja az autót.
     * Ha lemond a vezérlésről, kikerül a listából,
     * így az őt követő veszi át az irányítást.
     */
    public List<ControllerStorage> getOwners() {
        return OWNERS;
    }

    /**
     * A vezérlők listája, melyek a járművet kiválasztották.
     * A listához nem adható hozzá vezérlő, mert azt a {@link ControllerStorage} végzi el.
     */
    public List<? extends ControllerStorage> getControllers() {
        return CONTROLLERS;
    }

    /**
     * Azt adja meg, hány alkalommal lett a vezérlőjel beállítva az üzenetküldés során.
     * segítségével megtudható, hogy változott-e a vezérlőjel.
     * A {@link ControllerSideDisconnectProcess#afterTimeout()} metódus használja.
     */
    public int getControlCount() {
        return controlCount;
    }

    /**
     * Növeli a vezérlőjel-beállítás számlálót.
     * A {@link ControllerData#setControl(org.dyndns.fzoli.rccar.model.Control)} metódus használja (receiver objektum).
     */
    void incControlCount() {
        this.controlCount++;
    }

    /**
     * Nullázza a vezérlőjel-beállítás számlálót.
     * A {@link StorageList#createHostStorage(org.dyndns.fzoli.socket.process.impl.MessageProcess, org.dyndns.fzoli.rccar.model.host.HostData)} metódus használja.
     */
    void resetControlCount() {
        this.controlCount = 0;
    }
    
    /**
     * Vezérlő hozzáadása.
     * Ezt a metódust a {@code ControllerStorage.setHostStorage} metódus hívja meg.
     */
    void addController(ControllerStorage controller) {
        CONTROLLERS.add(controller);
        if (CONTROLLERS.size() == 1) { // ha ő az első kapcsolódó a járműhöz
            getSender().setStreaming(true); // MJPEG-stream folytatása, mert az első vezérlő kapcsolódott
//            controller.getReceiver().setWantControl(true); // egyből kapna vezérlést is
        }
        broadcastControllerChange(new ControllerData.ControllerChange(ControllerStorage.createControllerState(this, controller)));
    }

    /**
     * Vezérlő eltávolítása.
     * Ezt a metódust a {@code ControllerStorage.setHostStorage} metódus hívja meg.
     */
    void removeController(ControllerStorage controller) {
        if (getOwner() != null && getOwner() == controller) controller.getReceiver().setHostName(null);
        else OWNERS.remove(controller);
        CONTROLLERS.remove(controller);
        if (CONTROLLERS.isEmpty()) getSender().setStreaming(false); // MJPEG-stream szüneteltése, mivel nincs senki, aki látná
        broadcastControllerChange(new ControllerData.ControllerChange(controller.getName()));
    }

    /**
     * Elküldi a klienseknek a vezérlő állapotváltozását.
     */
    private void broadcastControllerChange(ControllerData.ControllerChange change) {
        broadcastMessage(new ControllerData.ControllerChangePartialControllerData(change));
    }

    /**
     * A járműhöz tartozó chatüzenetek listája.
     */
    public List<ChatMessage> getChatMessages() {
        return CHAT_MESSAGES;
    }

    /**
     * A járműre vonatkozó adatok tárolója.
     * Kliens és szerver oldalon is létező adatok.
     */
    public HostData getHostData() {
        return HOST_DATA;
    }

    /**
     * Megadja, hogy a tárolóhoz tartozik-e élő kapcsolat.
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Beállítja, hogy a tárolóhoz tartozik-e élő kapcsolat.
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    /**
     * Megadja, hogy a jármű kapcsolatában van-e időtúllépés.
     * A vezérlők oldalára generált adatmodel generálásához használt metódus.
     */
    public boolean isUnderTimeout() {
        return underTimeout;
    }

    /**
     * Beállítja, hogy a jármű kapcsolatában van-e időtúllépés és jelzi a változást a vezérlő klienseknek.
     */
    public void setUnderTimeout(boolean underTimeout) {
        this.underTimeout = underTimeout;
        broadcastMessage(new ControllerData.BoolenPartialControllerData(underTimeout, ControllerData.BoolenPartialControllerData.BooleanType.HOST_UNDER_TIMEOUT));
    }

    /**
     * Elküldi az üzenetet azoknak a vezérlőknek, akikhez ez a jármű tartozik.
     */
    private void broadcastMessage(PartialBaseData<ControllerData, ?> msg) {
        if (msg != null) {
            List<ControllerStorage> l = StorageList.getControllerStorageList();
            for (ControllerStorage cs : l) {
                if (HostStorage.this == cs.getHostStorage()) {
                    cs.getMessageProcess().sendMessage(msg);
                }
            }
        }
    }

}
