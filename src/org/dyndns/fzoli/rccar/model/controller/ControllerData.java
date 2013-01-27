package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.ControlPartialBaseData;
import org.dyndns.fzoli.rccar.model.PartialBaseData;

/**
 * A híd a vezérlőnek ezen osztály objektumait küldi, amikor adatot közöl.
 * Tartalmazza a kiválasztott autó nevét,
 * hogy a járművel van-e kiépített kapcsolat,
 * hogy a vezérlő vezérli-e az autót,
 * hogy a vezérlő szeretné-e vezérelni az autót,
 * az autóhoz tartozó vezérlő klienseket egy listában,
 * az autóhoz tartozó chatüzeneteket egy listában,
 * az autó gps helyzetét, pillanatnyi sebességét, északtól való eltérését és az akkuszintjét.
 * @author zoli
 */
public class ControllerData extends BaseData<ControllerData, PartialBaseData<ControllerData, ?>> {
    
    /**
     * Egy vezérlő változását (kapcsolódás, lekapcsolódás, állapotváltozás) írja le.
     */
    public static class ControllerChange implements Serializable {
        
        /**
         * A lekapcsolódó vezérlő neve.
         */
        public final String name;

        /**
         * A kapcsolódó/megváltozott vezérlő állapota.
         */
        public final ControllerState state;
        
        /**
         * Kapcsolódás illetve adatváltozás esete.
         */
        public ControllerChange(ControllerState state) {
            this(null, state);
        }
        
        /**
         * Lekapcsolódás esete.
         */
        public ControllerChange(String name) {
            this(name, null);
        }
        
        private ControllerChange(String name, ControllerState state) {
            this.state = state;
            this.name = name;
        }
        
    }
    
    /**
     * A ControllerData részadata.
     * Egy ControllerPartialData objektumot átadva a ControllerData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    public static abstract class PartialControllerData<T extends Serializable> extends PartialBaseData<ControllerData, T> {
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         */
        protected PartialControllerData(T data) {
            super(data);
        }
        
    }
    
    /**
     * A ControllerData részadata, ami egy vezérlő állapotváltozását tartalmazza (kapcsolódott, lekapcsolódott).
     */
    public static class ControllerChangePartialControllerData extends PartialControllerData<ControllerData.ControllerChange> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az állapotváltozást leíró objektum
         */
        public ControllerChangePartialControllerData(ControllerChange data) {
            super(data);
        }

        /**
         * Megkeresi a vezérlő állapot referenciáját.
         * @return null, ha nincs a listában
         */
        private static ControllerState findController(List<ControllerState> l, String name) {
            for (ControllerState s : l) {
                if (s.getName().equals(name)) return s;
            }
            return null;
        }
        
        /**
         * Alkalmazza az állapotváltozást a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null && data != null) {
                List<ControllerState> l = d.getControllers();
                if (data.name != null) { // lekapcsolódás
                    ControllerState s = findController(l, data.name);
                    if (s != null) l.remove(s);
                }
                if (data.state != null) { // kapcsolódás vagy adatváltozás
                    ControllerState old = findController(l, data.state.getName());
                    if (old != null) old.apply(data.state, d);
                    else l.add(data.state);
                }
            }
        }
        
    }
    
    /**
     * A ControllerData részadata, ami egy új chatüzenetet tartalmaz.
     */
    public static class ChatMessagePartialControllerData extends PartialControllerData<ChatMessage> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az új chatüzenet
         */
        public ChatMessagePartialControllerData(ChatMessage data) {
            super(data);
        }

        /**
         * Az új chatüzenetet hozzáadja a paraméterben megadott adatnak az üzenetlistájához.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null && data != null) {
                d.getChatMessages().add(data);
            }
        }
        
    }
    
    /**
     * A ControllerData részadata, ami a hosztnév változását tartalmazza.
     */
    public static class HostNamePartialControllerData extends PartialControllerData<String> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a hosztnév
         */
        public HostNamePartialControllerData(String data) {
            super(data);
        }

        /**
         * Alkalmazza a hosztnevet a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null) {
                d.setHostName(data);
            }
        }
        
    }
    
    /**
     * A ControllerData részadata, ami a jármű helyzetváltozását tartalmazza.
     */
    public static class HostStatePartialControllerData extends PartialControllerData<HostState> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az aktuális helyzet
         */
        public HostStatePartialControllerData(HostState data) {
            super(data);
        }

        /**
         * Alkalmazza az új állapotot a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null) {
                d.setHostState(data);
            }
        }
        
    }
    
    /**
     * A ControllerData részadata, ami egy boolean érték változását tartalmazza.
     */
    public static class BoolenPartialControllerData extends PartialControllerData<Boolean> {

        /**
         * A ControllerData Boolean változóinak megfeleltetett felsorolás.
         */
        public static enum BooleanType {
            VEHICLE_CONNECTED,
            HOST_UNDER_TIMEOUT,
            CONTROLLING,
            WANT_CONTROLL,
            UP_2_DATE,
            VIEW_ONLY
        }
        
        /**
         * Megmondja, melyik adatról van szó.
         */
        private final BooleanType type;
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data a boolean érték
         * @param type melyik változó
         */
        public BoolenPartialControllerData(Boolean data, BooleanType type) {
            super(data);
            this.type = type;
        }

        /**
         * Alkalmazza az új logikai értéket.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null && type != null) {
                switch (type) {
                    case VEHICLE_CONNECTED:
                        d.setVehicleConnected(data);
                        break;
                    case HOST_UNDER_TIMEOUT:
                        d.setHostUnderTimeout(data);
                        break;
                    case CONTROLLING:
                        d.setControlling(data);
                        break;
                    case WANT_CONTROLL:
                        d.setWantControl(data);
                        break;
                    case UP_2_DATE:
                        d.setUp2Date(data);
                        break;
                    case VIEW_ONLY:
                        d.setViewOnly(data);
                }
            }
        }
        
    }
    
    /**
     * A ControllerData részadata, ami az akkumulátorszint változását tartalmazza.
     */
    public static class BatteryPartialControllerData extends BatteryPartialBaseData<ControllerData> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data az akkumulátorszint
         */
        public BatteryPartialControllerData(Integer data) {
            super(data);
        }
        
    }

    /**
     * A ControllerData vezérlő részadata, ami az autó irányításában játszik szerepet.
     */
    public static class ControlPartialControllerData extends ControlPartialBaseData<ControllerData> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a vezérlőjel
         */
        public ControlPartialControllerData(Control data) {
            super(data);
        }
        
    }
    
    /**
     * Részadatküldő implementáció.
     * Arra lett kitalálva, hogy a különböző adatmódosulásokra más-más
     * adatküldési eljárást lehessen alkalmazni.
     * Az osztály kliens és szerver oldalon is használva van.
     */
    public static abstract class ControllerDataSender extends ControllerData {

        /**
         * Részadatküldő implementáció a listákhoz.
         * Az add és a remove metódus kerül felüldefiniálásra.
         */
        private static class SenderList<T> extends ArrayList<T> {

            /**
             * Az eredeti lista.
             */
            private final List<T> ls;
            
            /**
             * Az üzenetküldő.
             */
            private final ControllerDataSender sender;
            
            /**
             * Konstruktor.
             * @param sender a részadat-küldő objektum, ami elküldi az üzeneteket
             * @param ls az eredeti lista, melynek az add és remove metódusa hívódik meg, ha a paraméter nem null
             */
            public SenderList(ControllerDataSender sender, List<T> ls) {
                this.ls = ls;
                this.sender = sender;
            }
            
            /**
             * Objektum hozzáadása a listához.
             * Az üzenetküldés implementálása az utódban történik meg.
             */
            @Override
            public boolean add(T e) {
                if (ls == null) return false;
                return ls.add(e);
            }
            
            /**
             * Objektum törlése a listából.
             * Az üzenetküldés implementálása az utódban történik meg.
             */
            @Override
            public boolean remove(Object o) {
                if (ls == null) return false;
                return ls.remove(o);
            }

            /**
             * Az eredeti lista iterátora foreach ciklushoz.
             */
            @Override
            public Iterator<T> iterator() {
                return ls.iterator();
            }
            
            /**
             * Üzenet küldése a másik oldalnak.
             */
            protected void sendMessage(Serializable msg) {
                sender.sendMessage(msg);
            }
            
        }
        
        /**
         * Chatüzenet küldő osztály.
         */
        private static class ChatMessageSenderList extends SenderList<ChatMessage> {

            /**
             * Megadja, hogy a helyi adatmodelbe bekerüljenek-e az elküldött chatüzenetek.
             * Alapértelmezésként engedélyezve van.
             */
            private boolean addEnabled = true;
            
            /**
             * Konstruktor.
             * @param sender a részadat-küldő objektum, ami elküldi az üzeneteket
             * @param ls az eredeti lista, ami a chatüzeneteket tartalmazza
             */
            public ChatMessageSenderList(ControllerDataSender sender, List<ChatMessage> ls) {
                super(sender, ls);
            }
            
            /**
             * Chatüzenet hozzáadása a listához és üzenetküldés a másik oldalnak.
             */
            @Override
            public boolean add(ChatMessage e) {
                sendMessage(new ControllerData.ChatMessagePartialControllerData(e));
                return addEnabled ? super.add(e) : false;
            }
            
            /**
             * Chatüzenet eltávolítása a listából.
             * Nincs üzenetküldés, mert erre a metódusra soha nem lesz szükség,
             * mivel az egyszer már elküldött chatüzenet nem törölhető.
             */
            @Override
            public boolean remove(Object o) {
                return super.remove(o);
            }
            
        }
        
        /**
         * Vezérlőlista-módosulás küldő.
         */
        private static class ControllerChangeSenderList extends SenderList<ControllerState> {

            /**
             * Konstruktor.
             * @param sender a részadat-küldő objektum, ami elküldi az üzeneteket
             * @param ls az eredeti lista, ami a vezérlők neveit tartalmazza
             */
            public ControllerChangeSenderList(ControllerDataSender sender, List<ControllerState> ls) {
                super(sender, ls);
            }

            /**
             * Vezérlő hozzáadása a listához (ha még nincs benne) és üzenetküldés a másik oldalnak.
             * Ha egy vezérlő állapota megváltozik, akkor is az add metódust kell használni az üzenetküldésre,
             * miután az objektum tulajdonságai módosultak.
             * Ebben az esetben nincs szükség a helyi modelben való módosításra, mert az már megtörtént az objektum módosításával.
             */
            @Override
            public boolean add(ControllerState e) {
                sendControllerMessage(new ControllerChange(e));
                for (ControllerState s : this) {
                    if (s.getName().equals(e.getName())) {
                        return false;
                    }
                }
                return super.add(e);
            }

            /**
             * Vezérlő eltávolítása a listából és üzenetküldés a másik oldalnak.
             */
            @Override
            public boolean remove(Object o) {
                sendControllerMessage(new ControllerChange(o.toString()));
                return super.remove(o);
            }
            
            /**
             * Üzenetet küld a másik oldalnak.
             * @param change az üzenet tartalma
             */
            private void sendControllerMessage(ControllerChange change) {
                sendMessage(new ControllerData.ControllerChangePartialControllerData(change));
            }
            
        }
        
        /**
         * A helyi adatmodel.
         */
        private final ControllerData data;

        /**
         * A chatüzenet-küldő.
         */
        private final ChatMessageSenderList SENDER_MSG;
        
        /**
         * A vezérlő-változás küldő.
         */
        private final ControllerChangeSenderList SENDER_CON;
        
        /**
         * Konstruktor.
         * A setter metódusok csak üzenetküldést végeznek, mivel nincs megadva adatmodel.
         */
        public ControllerDataSender() {
            this(null);
        }
        
        /**
         * Konstruktor.
         * @param data a helyi adatmodel, amin a setter metódusok alkalmazódnak
         */
        public ControllerDataSender(ControllerData data) {
            this.data = data;
            SENDER_MSG = new ChatMessageSenderList(this, data == null ? null : data.CHAT_MESSAGES);
            SENDER_CON = new ControllerChangeSenderList(this, data == null ? null : data.CONTROLLERS);
        }

        /**
         * A kiválasztott hoszthoz tartozó chatüzenetek listáját adja meg, ha van adat.
         * Az {@code add} metódusa elküldi a chat üzenetet tartalmazó részadatot a másik oldalnak.
         */
        @Override
        public List<ChatMessage> getChatMessages() {
            return SENDER_MSG;
        }

        /**
         * A kiválasztott hoszthoz tartozó vezérlők listáját adja vissza, ha van adat.
         * Az {@code add} és {@code remove} metódusa elküldi a változást tartalmazó részadatot a másik oldalnak.
         */
        @Override
        public List<ControllerState> getControllers() {
            return SENDER_CON;
        }
        
        /**
         * Ha tudja, beállítja az akkumulátorszintet.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setBatteryLevel(Integer batteryLevel) {
            sendMessage(new ControllerData.BatteryPartialControllerData(batteryLevel));
            super.setBatteryLevel(batteryLevel);
        }

        /**
         * Ha tudja, beállítja a jármű vezérlőjelét.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setControl(Control control) {
            sendMessage(new ControllerData.ControlPartialControllerData(control));
            super.setControl(control);
        }

        /**
         * Ha tudja, beállítja, hogy a GPS adat naprakész-e.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setUp2Date(Boolean up2date) {
            sendMessage(new ControllerData.BoolenPartialControllerData(up2date, BoolenPartialControllerData.BooleanType.UP_2_DATE));
            super.setUp2Date(up2date);
        }

        /**
         * Ha tudja, beállítja azt, hogy a jármű vezérlése elérhető-e.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setControlling(Boolean controlling) {
            sendMessage(new ControllerData.BoolenPartialControllerData(controlling, BoolenPartialControllerData.BooleanType.CONTROLLING));
            if (data != null) data.setControlling(controlling);
        }

        /**
         * Ha tudja, beállítja azt, hogy a jármű kapssolata időtúllépés alatt van-e.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setHostUnderTimeout(Boolean hostConnected) {
            sendMessage(new ControllerData.BoolenPartialControllerData(hostConnected, BoolenPartialControllerData.BooleanType.HOST_UNDER_TIMEOUT));
            if (data != null) data.setHostUnderTimeout(hostConnected);
        }

        /**
         * Ha tudja, beállítja az aktuális jármű nevét.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setHostName(String hostName) {
            sendMessage(new ControllerData.HostNamePartialControllerData(hostName));
            if (data != null) data.setHostName(hostName);
        }

        /**
         * Ha tudja, beállítja a jármű pillanatnyi állapotát.
         * Az adat változását jelzi a másik oldalnak.
         * @param hostState a jármű pillanatnyi állapota
         */
        @Override
        public void setHostState(HostState hostState) {
            sendMessage(new ControllerData.HostStatePartialControllerData(hostState));
            if (data != null) data.setHostState(hostState);
        }

        /**
         * Ha tudja, beállítja azt, hogy a jármű kapcsolódva van-e a telefonhoz.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setVehicleConnected(Boolean vehicleConnected) {
            sendMessage(new ControllerData.BoolenPartialControllerData(vehicleConnected, BoolenPartialControllerData.BooleanType.VEHICLE_CONNECTED));
            if (data != null) data.setVehicleConnected(vehicleConnected);
        }

        /**
         * Ha tudja, beállítja azt, hogy a jármű irányítását lehet-e kérni.
         * Az adat változását jelzi a másik oldalnak.
         * @param viewOnly true esetén nem kérhető az irányítás soha
         */
        @Override
        public void setViewOnly(Boolean viewOnly) {
            sendMessage(new ControllerData.BoolenPartialControllerData(viewOnly, BoolenPartialControllerData.BooleanType.VIEW_ONLY));
            if (data != null) data.setViewOnly(viewOnly);
        }

        /**
         * Ha lehet, beállítja azt, hogy szeretné-e a felhasználó vezérelni az autót.
         * Az adat változását jelzi a másik oldalnak.
         */
        @Override
        public void setWantControl(Boolean wantControl) {
            sendMessage(new ControllerData.BoolenPartialControllerData(wantControl, BoolenPartialControllerData.BooleanType.WANT_CONTROLL));
            if (data != null) data.setWantControl(wantControl);
        }
        
        /**
         * Engedélyezi vagy tiltja a helyi adatmodelbe való hozzáadást.
         * Akkor kerül tiltásra a funkció, ha az elküldött chatüzenet visszaérkezik a másik oldalról.
         * Ebben az esetben ha engedélyezve lenne a funkció, duplán adódna hozzá a listához az üzenet.
         */
        protected void setAddEnabled(boolean addEnabled) {
            SENDER_MSG.addEnabled = addEnabled;
        }
        
        /**
         * Üzenet küldése a másik oldalnak.
         * Szerver és kliens oldalon eltérő implementáció.
         */
        protected abstract void sendMessage(Serializable msg);

    }
    
    /**
     * Az aktuális jármű neve.
     */
    private String hostName;
    
    /**
     * A jármű pillanatnyi állapota.
     */
    private HostState hostState;
    
    /**
     * A jármű kapcsolata időtúllépés alatt van-e.
     */
    private Boolean hostUnderTimeout;
    
    /**
     * A jármű mikrovezérlője kapcsolódva van-e a telefonhoz.
     */
    private Boolean vehicleConnected;
    
    /**
     * Vezérelhető-e a jármű.
     */
    private Boolean controlling;
    
    /**
     * Van-e igény a jármű vezérlésére.
     */
    private Boolean wantControl;
    
    /**
     * Megadja azt, hogy a jármű irányítását lehet-e kérni.
     * True esetén nem kérhető a jármű vezérlése soha, tehát csak figyelhető a jármű.
     */
    private Boolean viewOnly;
    
    /**
     * A kiválasztott hoszthoz tartozó chatüzenetek tárolója.
     */
    private final List<ChatMessage> CHAT_MESSAGES;

    /**
     * A kiválasztott járműhöz kapcsolódott vezérlők listája.
     */
    private final List<ControllerState> CONTROLLERS;
    
    /**
     * A vezérlő adatainak inicializálása.
     * Vezérlő oldalnak.
     */
    public ControllerData() {
        CHAT_MESSAGES = Collections.synchronizedList(new ArrayList<ChatMessage>());
        CONTROLLERS = Collections.synchronizedList(new ArrayList<ControllerState>());
    }

    /**
     * A vezérlő adatainak inicializálása.
     * Híd oldalnak.
     * @param controllers a járműhöz kapcsolódottak listája
     * @param chatMessages az üzeneteket tartalmazó lista
     * @throws NullPointerException ha az üzeneteket tartalmazó lista null
     */
    public ControllerData(List<ControllerState> controllers, List<ChatMessage> chatMessages) {
        if (chatMessages == null || controllers == null) throw new NullPointerException();
        CHAT_MESSAGES = Collections.synchronizedList(chatMessages);
        CONTROLLERS = Collections.synchronizedList(controllers);
    }

    /**
     * Az aktuális jármű nevét adja vissza.
     */
    public String getHostName() {
        return hostName;
    }
    
    /**
     * A jármű pillanatnyi állapotát adja vissza.
     */
    public HostState getHostState() {
        return hostState;
    }
    
    /**
     * A kiválasztott hoszthoz tartozó chatüzenetek listáját adja meg.
     */
    public List<ChatMessage> getChatMessages() {
        return CHAT_MESSAGES;
    }

    /**
     * A kiválasztott hoszthoz tartozó vezérlők listáját adja vissza.
     */
    public List<ControllerState> getControllers() {
        return CONTROLLERS;
    }
    
    /**
     * Megadja azt, hogy a jármű kapcsolata időtúllépés alatt van-e.
     */
    public Boolean isHostUnderTimeout() {
        return hostUnderTimeout;
    }

    /**
     * Megadja azt, hogy a jármű kapcsolódva van-e a telefonhoz.
     */
    public Boolean isVehicleConnected() {
        return vehicleConnected;
    }
    
    /**
     * Megadja azt, hogy a jármű vezérlése elérhető-e.
     */
    public Boolean isControlling() {
        return controlling;
    }
    
    /**
     * Megadja azt, hogy szeretné-e a felhasználó vezérelni az autót.
     */
    public Boolean isWantControl() {
        return wantControl;
    }

    /**
     * Megadja azt, hogy a jármű irányítását lehet-e kérni.
     * @return true esetén nem kérhető az irányítás soha
     */
    public Boolean isViewOnly() {
        return viewOnly;
    }

    /**
     * Beállítja az aktuális jármű nevét.
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Beállítja a jármű pillanatnyi állapotát.
     * @param hostState a jármű pillanatnyi állapota
     */
    public void setHostState(HostState hostState) {
        this.hostState = hostState;
    }

    /**
     * Beállítja azt, hogy a jármű kapssolata időtúllépés alatt van-e.
     */
    public void setHostUnderTimeout(Boolean hostConnected) {
        this.hostUnderTimeout = hostConnected;
    }

    /**
     * Beállítja azt, hogy a jármű kapcsolódva van-e a telefonhoz.
     */
    public void setVehicleConnected(Boolean vehicleConnected) {
        this.vehicleConnected = vehicleConnected;
    }
    
    /**
     * Beállítja azt, hogy a jármű vezérlése elérhető-e.
     */
    public void setControlling(Boolean controlling) {
        this.controlling = controlling;
    }

    /**
     * Beállítja azt, hogy szeretné-e a felhasználó vezérelni az autót.
     */
    public void setWantControl(Boolean wantControl) {
        this.wantControl = wantControl;
    }

    /**
     * Beállítja azt, hogy a jármű irányítását lehet-e kérni.
     * @param viewOnly true esetén nem kérhető az irányítás soha
     */
    public void setViewOnly(Boolean viewOnly) {
        this.viewOnly = viewOnly;
    }
    
    /**
     * Akkor hívódik meg, ha az egyik vezérlő állapota megváltozott.
     * Alapértelmezetten nem csinál semmit.
     */
    public void onControllerStateChanged(ControllerState cs) {
        ;
    }
    
    /**
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(ControllerData d) {
        if (d != null) {
            getControllers().clear();
            getChatMessages().clear();
            getControllers().addAll(d.getControllers());
            getChatMessages().addAll(d.getChatMessages());
            setHostName(d.getHostName());
            setHostState(d.getHostState());
            setHostUnderTimeout(d.isHostUnderTimeout());
            setVehicleConnected(d.isVehicleConnected());
            setControlling(d.isControlling());
            setWantControl(d.isWantControl());
            setViewOnly(d.isViewOnly());
            super.update(d);
        }
    }
    
    /**
     * Kinullázza az adatokat.
     */
    @Override
    public void clear() {
        getControllers().clear();
        getChatMessages().clear();
        hostName = null;
        hostState = null;
        hostUnderTimeout = null;
        controlling = null;
        wantControl = null;
        viewOnly = null;
        super.clear();
    }
    
}
