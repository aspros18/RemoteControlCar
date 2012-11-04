package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
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
public class ControllerData extends BaseData<ControllerData, PartialBaseData<ControllerData, ?>> {
    
    /**
     * A ControllerData részadata.
     * Egy ControllerPartialData objektumot átadva a ControllerData objektumnak, egyszerű frissítést lehet végrehajtani.
     */
    private static abstract class PartialControllerData<T extends Serializable> extends PartialBaseData<ControllerData, T> {
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data az adat
         */
        protected PartialControllerData(T data) {
            super(data);
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
            HOST_CONNECTED,
            CONTROLLING,
            WANT_CONTROLL
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
                    case HOST_CONNECTED:
                        d.setHostConnected(data);
                        break;
                    case CONTROLLING:
                        d.setControlling(data);
                        break;
                    case WANT_CONTROLL:
                        d.setWantControl(data);
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
     * Vezérelhető-e a jármű.
     */
    private Boolean controlling;
    
    /**
     * Van-e igény a jármű vezérlésére.
     */
    private Boolean wantControl;
    
    /**
     * A kiválasztott hoszthoz tartozó chatüzenetek tárolója.
     */
    private final List<ChatMessage> CHAT_MESSAGES;

    /**
     * A vezérlő adatainak inicializálása.
     * Vezérlő oldalnak.
     */
    public ControllerData() {
        CHAT_MESSAGES = Collections.synchronizedList(new ArrayList<ChatMessage>()); //TODO: a lista módosulása esetén lehessen majd üzenetet küldetni
    }

    /**
     * A vezérlő adatainak inicializálása.
     * Híd oldalnak.
     * @param chatMessages az üzeneteket tartalmazó lista
     * @throws NullPointerException ha az üzeneteket tartalmazó lista null
     */
    public ControllerData(List<ChatMessage> chatMessages) {
        if (chatMessages == null) throw new NullPointerException();
        CHAT_MESSAGES = Collections.synchronizedList(chatMessages);
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
     * Megadja azt, hogy a jármű kapcsolódva van-e.
     */
    public Boolean isHostConnected() {
        return hostConnected;
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
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(ControllerData d) {
        if (d != null) {
            getChatMessages().addAll(d.getChatMessages());
            setHostName(d.getHostName());
            setHostState(d.getHostState());
            setHostConnected(d.isHostConnected());
            setControlling(d.isControlling());
            setWantControl(d.isWantControl());
            super.update(d);
        }
    }
    
}
