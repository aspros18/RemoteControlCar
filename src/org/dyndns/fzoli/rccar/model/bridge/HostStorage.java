package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.rccar.model.controller.ChatMessage;
import org.dyndns.fzoli.rccar.model.controller.ControllerData;
import org.dyndns.fzoli.rccar.model.controller.HostState;
import org.dyndns.fzoli.rccar.model.host.HostData;

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
public class HostStorage {
    
    /**
     * A jármű neve.
     */
    private final String HOST_NAME;
    
    /**
     * A jármű adatai.
     */
    private final HostData HOST_DATA = new HostData();
    
    /**
     * Vezérlők, melyek ezt a járművet választották ki.
     */
    private final List<String> CONTROLLERS = new ArrayList<String>();
    
    /**
     * A járműhöz tartozó chatüzenetek.
     */
    private final List<ChatMessage> CHAT_MESSAGES = new ArrayList<ChatMessage>();

    /**
     * A jelenlegi jármű irányító.
     */
    private String owner;
    
    /**
     * A járművet irányítani akarók.
     */
    private final List<String> OWNERS = new ArrayList<String>();
    
    public HostStorage(String hostName) {
        HOST_NAME = hostName;
    }

    public String getHostName() {
        return HOST_NAME;
    }

    public String getOwner() {
        return owner;
    }

    public List<String> getOwners() {
        synchronized(OWNERS) {
            return new ArrayList<String>(OWNERS);
        }
    }
    
    public List<String> getControllers() {
        synchronized(CONTROLLERS) {
            return new ArrayList<String>(CONTROLLERS);
        }
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public void addOwner(String owner) {
        if (owner != null) synchronized(OWNERS) {
            OWNERS.add(owner);
        }
    }
    
    public void removeOwner(String owner) {
        if (owner != null) synchronized(OWNERS) {
            OWNERS.remove(owner);
        }
    }
    
    public void addController(String c) {
        if (c != null) synchronized(CONTROLLERS) {
            CONTROLLERS.add(c);
        }
    }
    
    public void removeController(String c) {
        if (c != null) synchronized(CONTROLLERS) {
            CONTROLLERS.remove(c);
        }
    }
    
    public List<ChatMessage> getChatMessages() {
        synchronized(CHAT_MESSAGES) {
            return new ArrayList<ChatMessage>(CHAT_MESSAGES);
        }
    }
    
    public void addChatMessage(ChatMessage m) {
        if (m != null) synchronized(CHAT_MESSAGES) {
            CHAT_MESSAGES.add(m);
        }
    }
    
    public ControllerData createControllerData() {
        ControllerData d = new ControllerData(getChatMessages());
        d.setHostState(createHostState());
        d.setHostName(HOST_NAME);
        d.setBatteryLevel(HOST_DATA.getBatteryLevel());
        //TODO: a többi setter befejezése
        return d;
    }
    
    private HostState createHostState() {
        return null; //TODO
    }
    
}
