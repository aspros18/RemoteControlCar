package org.dyndns.fzoli.rccar.model.bridge;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
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
public class HostStorage extends Storage {
    
    /**
     * A jármű adatai.
     */
    private final HostData HOST_DATA = new HostData();
    
    /**
     * Üzenetküldő implementáció, ami a járműnek küld üzenetet.
     * A helyi adatot nem módosítja, mert nem minden esetben van arra szükség.
     * Csak azon setter metódusok vannak megírva, melyek üzenetküldésre használatosak.
     */
    private class HostDataSender extends HostData {

        /**
         * Elküldi a paraméterben megadott vezérlőjelet.
         */
        @Override
        public void setControl(Control controll) {
            sendMessage(new HostData.ControlPartialHostData(controll));
        }

        /**
         * Elküldi, hogy a streamelés folyamatban van-e.
         */
        @Override
        public void setStreaming(Boolean streaming) {
            sendMessage(new HostData.BooleanPartialHostData(streaming, BooleanPartialHostData.BooleanType.STREAMING));
        }
        
        /**
         * Elküldi az üzenetet a járműnek.
         */
        private void sendMessage(Serializable msg) {
            HostStorage.this.getMessageProcess().sendMessage(msg);
        }
        
    }
    
    /**
     * Olyan üzenetküldő, ami a járműnek küld üzenetet.
     */
    private final HostData sender = new HostDataSender();
    
    // TODO: DataSender interfészre nem lesz szükség, helyette ide is egy forwarder kell egy univerzális adatmódosulás feldolgozóhoz. Ezt használja majd a ControllerDataForwarder és a beérkező üzeneteket is ez dolgozza majd fel (helyi adat frissítése, üzenetküldés a megfelelő klienseknek).
    
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
     * A jármű kapcsolata időtúllépés alatt van-e.
     */
    private boolean underTimeout = false;
    
    /**
     * Konstruktor a kezdeti paraméterek megadásával.
     */
    public HostStorage(MessageProcess messageProcess) {
        super(messageProcess);
    }

    /**
     * A járműnek lehet üzenetet küldeni ezzel az objektummal a setter metódusok használatával.
     */
    public HostData getSender() {
        return sender;
    }

    /**
     * A jármű jelenlegi irányítója.
     * (Az irányításra rangsorolt vezérlők közül az első.)
     */
    public ControllerStorage getOwner() {
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
     * Vezérlő hozzáadása.
     * A {@code ControllerStorage.setHostStorage} metódus használja.
     */
    void addController(ControllerStorage controller) {
        CONTROLLERS.add(controller);
    }

    /**
     * A jármű vezérlésének átadása a paraméterben megadott kliensnek.
     * A vezérlő-lista első helyére kerül a megadott kliens.
     */
    public void setOwner(ControllerStorage owner) {
        OWNERS.add(0, owner);
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
        this.underTimeout = underTimeout; // TODO: a vezérlőknek is kell jelezni
    }

    /**
     * Megadja, hogy a jármű kapcsolódva van-e a hídhoz.
     */
    public static boolean isHostConnected(HostStorage s) {
        return s != null && s.getMessageProcess() != null && !s.getMessageProcess().getSocket().isClosed();
    }
    
}
