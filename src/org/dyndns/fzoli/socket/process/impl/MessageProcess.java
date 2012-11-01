package org.dyndns.fzoli.socket.process.impl;

import java.io.Serializable;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Üzenetváltásra használandó szál implementálására.
 * @author zoli
 */
public interface MessageProcess extends SecureProcess {
    
    /**
     * A feldolgozó mostantól képes üzenetet küldeni.
     */
    public void onStart();
    
    /**
     * A másik oldal üzenetet küldött.
     * @param o az üzenet
     */
    public void onMessage(Object o);
    
    /**
     * Kivétel keletkezett az egyik üzenet elküldésekor / inicializálás közben / megszakadt a kapcsolat.
     */
    public void onException(Exception ex);
    
    /**
     * Üzenet küldése a másik oldalnak.
     * A metódus megvárja az üzenetküldés befejezését.
     * @param o az üzenet, szerializálható objektum
     */
    public void sendMessage(Serializable o);
    
    /**
     * Üzenet küldése a másik oldalnak.
     * @param o az üzenet, szerializálható objektum
     * @param wait várja-e meg a metódus a küldés befejezését
     */
    public void sendMessage(Serializable o, boolean wait);
    
}
