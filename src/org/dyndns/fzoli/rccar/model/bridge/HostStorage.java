package org.dyndns.fzoli.rccar.model.bridge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
     * A járműhöz tartozó chatüzenetek.
     */
    private final List<ChatMessage> CHAT_MESSAGES = Collections.synchronizedList(new ArrayList<ChatMessage>());
    
    /**
     * A járművet irányítani akarók.
     */
    private final List<ControllerStorage> OWNERS = Collections.synchronizedList(new ArrayList<ControllerStorage>());
    
    /**
     * Azok a vezérlők, melyek a járművet kiválasztották.
     */
    private final List<ControllerStorage> CONTROLLERS = Collections.synchronizedList(new ArrayList<ControllerStorage>());
    
    public HostStorage(MessageProcess messageProcess) {
        super(messageProcess);
    }

    public ControllerStorage getOwner() {
        return OWNERS.get(0);
    }

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

    public void setOwner(ControllerStorage owner) {
        OWNERS.add(0, owner);
    }

    public List<ChatMessage> getChatMessages() {
        return CHAT_MESSAGES;
    }

    public HostData getHostData() {
        return HOST_DATA;
    }

}
