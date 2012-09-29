package org.dyndns.fzoli.socket;

import java.net.Socket;

/**
 * Külön szálban, a socketen át adatfeldolgozást végző osztály alapja kliens és szerver oldalra.
 * @author zoli
 */
public abstract class AbstractProcess implements Process {
    
    private final Socket socket;
    
    /**
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractProcess(Socket socket) {
        this.socket = socket;
    }
    
    /**
     * @return Socket, amin keresztül folyik a kommunikáció.
     */
    @Override
    public Socket getSocket() {
        return socket;
    }
    
}
