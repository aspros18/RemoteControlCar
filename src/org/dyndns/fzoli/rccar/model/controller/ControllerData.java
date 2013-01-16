package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
import org.dyndns.fzoli.rccar.model.DataSender;
import org.dyndns.fzoli.rccar.model.PartialBaseData;

/**
 * A híd a vezérlőnek ezen osztály objektumait küldi, amikor adatot közöl.
 * Tartalmazza a kiválasztott autó nevét,
 * hogy a járművel van-e kiépített kapcsolat,
 * hogy a vezérlő vezérli-e az autót,
 * hogy a vezérlő szeretné-e vezérelni az autót,
 * az autóhoz tartozó chatüzeneteket egy listában,
 * az autó gps helyzetét, pillanatnyi sebességét, északtól való eltérését és az akkuszintjét.
 * @author zoli
 */
public class ControllerData extends BaseData<ControllerData, PartialBaseData<ControllerData, ?>> implements DataSender<ControllerData> {
    
    /**
     * Egy vezérlő változását (kapcsolódás, lekapcsolódás) írja le.
     */
    public static class ControllerChange implements Serializable {
        
        /**
         * Igaz, ha kapcsolódás történt, egyébként hamis.
         */
        public final boolean connected;
        
        /**
         * A kapcsolódó/lekapcsolódó vezérlő neve.
         */
        public final String name;

        public ControllerChange(String name, boolean connected) {
            this.connected = connected;
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
         * Alkalmazza az állapotváltozást a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null && data != null) {
                List<String> l = d.getControllers();
                if (data.connected && !l.contains(data.name)) l.add(data.name);
                if (!data.connected && l.contains(data.name)) l.remove(data.name);
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
            HOST_CONNECTED,
            CONTROLLING,
            WANT_CONTROLL,
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
                    case HOST_CONNECTED:
                        d.setHostConnected(data);
                        break;
                    case CONTROLLING:
                        d.setControlling(data);
                        break;
                    case WANT_CONTROLL:
                        d.setWantControl(data);
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
     * Részadatküldő implementáció.
     * Arra lett kitalálva, hogy a különböző adatmódosulásokra más-más
     * adatküldési eljárást lehessen alkalmazni.
     * Nem minden esetben kell üzenetet küldeni, sőtt van olyan adatmódosulás,
     * ami esetén egyáltalán nem kell üzenni a híd szervernek.
     */
    private static class ControllerDataSender extends ControllerData {

        /**
         * A helyi adatmodel.
         */
        private final ControllerData data;
        
        /**
         * Az adatmódosulást okozó üzenet küldőjének a neve.
         */
        private final String senderName;
        
        /**
         * Konstruktor.
         * Az eszközazonosítóra nincs szükség, mert az adatmódosulást vagy a
         * vezérlő program okozza helyben (a felhasználó kérésére) vagy
         * a híd okozza azt (a szerver oldalán módosult az adat).
         * Mindkét esetben elég a név ahhoz, hogy tudni lehessen, ki a küldő:
         * A szervernek egyedi azonosítója van, senki nem bírtokolhatja azt.
         * Ha a küldő a szerver, akkor biztos, hogy nem kell üzenetet küldeni.
         * @param data a helyi adatmodel, amin a setter metódusok alkalmazódnak
         * @param senderName az üzenetküldő neve
         */
        public ControllerDataSender(ControllerData data, String senderName) {
            this.data = data;
            this.senderName = senderName;
        }

        /**
         * A {@link ControllerData#update(ControllerData)} metódus használja.
         */
        @Override
        public List<String> getControllers() {
            return data.getControllers();
        }

        /**
         * A {@link ControllerData#update(ControllerData)} metódus használja.
         */
        @Override
        public List<ChatMessage> getChatMessages() {
            return data.getChatMessages();
        }

        /**
         * Az akkumulátor-szint változását nem kell elküldeni a szervernek soha,
         * mivel a jármű küldi azt a hídnak és a híd informálja a vezérlőket.
         * A vezérlő oldalán ez az adat soha nem módosul, csak ha a híd módosítja.
         * Tehát a metódus nem küld üzenetet, csak beállítja a helyi adatmodelen azt.
         */
        @Override
        public void setBatteryLevel(Integer batteryLevel) {
            data.setBatteryLevel(batteryLevel);
        }

        @Override
        public void setControlling(Boolean controlling) {
            data.setControlling(controlling); // TODO
        }

        @Override
        public void setHostConnected(Boolean hostConnected) {
            data.setHostConnected(hostConnected); // TODO
        }

        @Override
        public void setHostName(String hostName) {
            data.setHostName(hostName); // TODO
        }

        @Override
        public void setHostState(HostState hostState) {
            data.setHostState(hostState); // TODO
        }

        @Override
        public void setVehicleConnected(Boolean vehicleConnected) {
            data.setVehicleConnected(vehicleConnected); // TODO
        }

        @Override
        public void setViewOnly(Boolean viewOnly) {
            data.setViewOnly(viewOnly); // TODO
        }

        @Override
        public void setWantControl(Boolean wantControl) {
            data.setWantControl(wantControl); // TODO
        }
        
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
     * A jármű kapcsolódva van-e a hídhoz.
     */
    private Boolean hostConnected;
    
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
    private final List<String> CONTROLLERS;
    
    /**
     * A vezérlő adatainak inicializálása.
     * Vezérlő oldalnak.
     */
    public ControllerData() {
        CHAT_MESSAGES = Collections.synchronizedList(new ArrayList<ChatMessage>()); //TODO: a lista módosulása esetén lehessen majd üzenetet küldetni
        CONTROLLERS = Collections.synchronizedList(new ArrayList<String>());
    }

    /**
     * A vezérlő adatainak inicializálása.
     * Híd oldalnak.
     * @param controllers a járműhöz kapcsolódottak listája
     * @param chatMessages az üzeneteket tartalmazó lista
     * @throws NullPointerException ha az üzeneteket tartalmazó lista null
     */
    public ControllerData(List<String> controllers, List<ChatMessage> chatMessages) {
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
     * A jármű pillanatnyi állapotát adja vissz.
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
    public List<String> getControllers() {
        return CONTROLLERS;
    }
    
    /**
     * Megadja azt, hogy a jármű kapcsolódva van-e.
     */
    public Boolean isHostConnected() {
        return hostConnected;
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
     * Beállítja azt, hogy a jármű kapcsolódva van-e.
     */
    public void setHostConnected(Boolean hostConnected) {
        this.hostConnected = hostConnected;
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
     * Létrehoz egy részadatküldő objektumot,
     * ami segítségével a háttérben történik meg a hídnak való üzenetküldés,
     * miközben a helyi adatmodellben is beállítódik az új érték.
     */
    @Override
    public ControllerData createSender(String senderName, Integer senderDevice) {
        return new ControllerDataSender(this, senderName);
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
            setHostConnected(d.isHostConnected());
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
        hostConnected = null;
        controlling = null;
        wantControl = null;
        viewOnly = null;
        super.clear();
    }
    
}
