package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.BatteryPartialBaseData;
import org.dyndns.fzoli.rccar.model.PartialBaseData;

/**
 * A híd a vezérlőnek ezen osztály objektumait küldi, amikor adatot közöl.
 * Tartalmazza a kiválasztott autó nevét,
 * hogy a járművel van-e kiépített kapcsolat, <- TODO --!>
 * hogy a vezérlő vezérli-e az autót, <- TODO --!>
 * hogy a vezérlő szeretné-e vezérelni az autót, <-- TODO --!>
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
     * A kiválasztott hoszthoz tartozó chatüzenetek tárolója.
     */
    private final List<ChatMessage> CHAT_MESSAGES;

    /**
     * A vezérlő adatainak inicializálása.
     * @param chatMessages az üzeneteket tartalmazó lista
     * @throws NullPointerException ha az üzeneteket tartalmazó lista null
     */
    public ControllerData(List<ChatMessage> chatMessages) {
        if (chatMessages == null) throw new NullPointerException();
        CHAT_MESSAGES = chatMessages;
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
        synchronized(CHAT_MESSAGES) {
            return new ArrayList<ChatMessage>(CHAT_MESSAGES);
        }
    }
    
    /**
     * Chatüzenetet ad hozzá a tárolóhoz.
     */
    public void addChatMessage(ChatMessage m) {
        if (m != null) synchronized(CHAT_MESSAGES) {
            CHAT_MESSAGES.add(m);
        }
    }
    
    /**
     * Több chatüzenetet ad hozzá a tárolóhoz.
     */
    public void addChatMessages(List<ChatMessage> l) {
        if (l != null) synchronized(CHAT_MESSAGES) {
            CHAT_MESSAGES.addAll(l);
        }
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
     * Frissíti az adatokat a megadott adatokra.
     * @param d az új adatok
     */
    @Override
    public void update(ControllerData d) {
        if (d != null) {
            addChatMessages(d.getChatMessages());
            setHostName(d.getHostName());
            setHostState(d.getHostState());
            super.update(d);
        }
    }
    
}
