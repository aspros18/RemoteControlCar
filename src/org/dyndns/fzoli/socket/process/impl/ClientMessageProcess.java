package org.dyndns.fzoli.socket.process.impl;

import java.io.Serializable;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureClientProcess;

/**
 * Kliens oldalra írt, üzenetváltásra használandó szál.
 * Philip Isenhour által írt tömörítést használ az üzenetek továbbítására.
 * @author zoli
 */
public abstract class ClientMessageProcess extends AbstractSecureClientProcess implements MessageProcess {
    
    /**
     * Kliens és szerver oldalon is megegyező metódusok gyűjteménye.
     */
    private final MessageProcessUtil UTIL = new MessageProcessUtil(this);
    
    /**
     * Biztonságos üzenetváltásra képes adatfeldolgozó inicializálása.
     * @param handler Biztonságos kliens oldali kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public ClientMessageProcess(AbstractSecureClientHandler handler) {
        super(handler);
    }

    /**
     * Üzenet küldése a szervernek.
     * A metódus megvárja az üzenetküldés befejezését.
     * @param o az üzenet, szerializálható objektum
     */
    @Override
    public void sendMessage(Serializable o) {
        UTIL.sendMessage(o);
    }
    
    /**
     * Üzenet küldése a szervernek.
     * @param o az üzenet, szerializálható objektum
     * @param wait várja-e meg a metódus a küldés befejezését
     */
    @Override
    public void sendMessage(Serializable o, boolean wait) {
        UTIL.sendMessage(o, wait);
    }
    
    /**
     * A szerver üzenetet küldött.
     * @param o az üzenet
     */
    @Override
    public abstract void onMessage(Object o);
    
    /**
     * A feldolgozó mostantól képes üzenetet küldeni.
     */
    @Override
    public void onStart() {
        ;
    }
    
    /**
     * Kivétel keletkezett az egyik üzenet elküldésekor / inicializálás közben / megszakadt a kapcsolat.
     */
    @Override
    public void onException(Exception ex) {
        ;
    }
    
    /**
     * Inicializálás.
     * - Üzenetküldő szál létrehozása és indítása.
     * - Várakozás üzenetre a másik oldaltól, míg él a kapcsolat.
     */
    @Override
    public void run() {
        UTIL.run();
    }
    
}
