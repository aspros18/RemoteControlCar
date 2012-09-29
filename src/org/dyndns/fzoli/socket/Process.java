package org.dyndns.fzoli.socket;

import java.net.Socket;

/**
 * Interfész a külön szálban, a socketen át adatfeldolgozást végző osztály írására.
 * @author zoli
 */
public interface Process extends Runnable {
    
    /**
     * @return Socket, amin keresztül folyik a kommunikáció.
     */
    public Socket getSocket();
    
    /**
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    public Integer getConnectionId();
    
}
