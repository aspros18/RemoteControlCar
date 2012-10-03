package org.dyndns.fzoli.socket.process;

import java.net.Socket;
import org.dyndns.fzoli.socket.handler.Handler;

/**
 * Külön szálban, a socketen át adatfeldolgozást végző osztály alapja kliens és szerver oldalra.
 * @author zoli
 */
public abstract class AbstractProcess implements Process {
    
    //private final Handler handler;
    private final Socket socket;
    
    /**
     * @param handler Kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractProcess(/*Handler handler, */Socket socket) {
        this.socket = socket;
        //this.handler = handler;
    }
    
    /**
     * @return Kapcsolatfeldolgozó, ami létrehozta ezt az adatfeldolgozót.
     */
    //protected Handler getHandler() {
    //    return handler;
    //}
    
    /**
     * @return Socket, amin keresztül folyik a kommunikáció.
     */
    @Override
    public Socket getSocket() {
        return socket;
    }
    
}
