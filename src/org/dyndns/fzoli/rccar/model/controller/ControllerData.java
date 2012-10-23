package org.dyndns.fzoli.rccar.model.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.model.BaseData;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.Point3D;

/**
 * A híd a vezérlőnek ezen osztály objektumait küldi, amikor adatot közöl.
 * Tartalmazza a kiválasztott autó nevét,
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
     * A ControllerData részadata, ami a GPS pozíció változását tartalmazza.
     */
    public static class GpsPartialControllerData extends PartialControllerData<Point3D> {

        /**
         * Részadat inicializálása és beállítása.
         * @param data a GPS koordináta
         */
        public GpsPartialControllerData(Point3D data) {
            super(data);
        }

        /**
         * Alkalmazza a GPS koordinátát a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null) {
                d.setGpsPosition(data);
            }
        }
        
    }
    
    /**
     * A HostData részadata, ami az akkumulátorszint változását tartalmazza.
     * @author zoli
     */
    public static class IntegerPartialControllerData extends PartialBaseData<ControllerData, Integer> {

        /**
         * A ControllerData Integer változóinak megfeleltetett felsorolás.
         */
        public static enum IntegerType {
            BATTERY_LEVEL,
            SPEED,
            WAY
        }
        
        /**
         * Megmondja, melyik adatról van szó.
         */
        public final IntegerType type;
        
        /**
         * Részadat inicializálása és beállítása.
         * @param data az akkumulátorszint
         */
        public IntegerPartialControllerData(Integer data, IntegerType type) {
            super(data);
            this.type = type;
        }

        /**
         * Alkalmazza a új részadatot a paraméterben megadott adaton.
         * @param d a teljes adat, amin a módosítást alkalmazni kell
         */
        @Override
        public void apply(ControllerData d) {
            if (d != null && type != null) {
                switch (type) {
                    case BATTERY_LEVEL:
                        d.setBatteryLevel(data);
                        break;
                    case SPEED:
                        d.setSpeed(data);
                        break;
                    case WAY:
                        d.setWay(data);
                }
            }
        }
        
    }
    
    /**
     * Északtól fokban való eltérés.
     */
    private Integer way;
    
    /**
     * Pillanatnyi sebesség km/h-ban.
     */
    private Integer speed;

    /**
     * Az aktuális jármű neve.
     */
    private String hostName;
    
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
     * Az északtól fokban megadott eltérést adja meg.
     */
    public Integer getWay() {
        return way;
    }

    /**
     * A pillanatnyi sebességet km/h-ban adja vissza.
     */
    public Integer getSpeed() {
        return speed;
    }

    /**
     * Az aktuális jármű nevét adja vissza.
     */
    public String getHostName() {
        return hostName;
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
     * Beállítja az északtól való eltérést.
     * @param way fokban megadott eltérés
     */
    public void setWay(Integer way) {
        this.way = way;
    }

    /**
     * Beállítja a pillanatnyi sebességet.
     * @param speed km/h-ban megadott érték
     */
    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    /**
     * Beállítja az aktuális jármű nevét.
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
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
            setSpeed(d.getSpeed());
            setWay(d.getWay());
            super.update(d);
        }
    }
    
}
