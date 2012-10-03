package org.dyndns.fzoli.socket.process;

import java.net.Socket;
import org.dyndns.fzoli.socket.handler.Handler;

/**
 * A socketen át adatfeldolgozást végző osztály alapja.
 * @author zoli
 */
public abstract class AbstractProcess implements Process {
    
    private final Handler handler;
    
    /**
     * Adatfeldolgozó inicializálása.
     * @param handler Kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public AbstractProcess(Handler handler) {
        this.handler = handler;
    }
    
    /**
     * @return Kapcsolatfeldolgozó, ami létrehozta ezt az adatfeldolgozót.
     */
    protected Handler getHandler() {
        return handler;
    }
    
    /**
     * @return Socket, amin keresztül folyik a kommunikáció.
     */
    @Override
    public Socket getSocket() {
        return getHandler().getSocket();
    }

    /**
     * @return Kapcsolatazonosító, ami segítségével megtudható a kapcsolatteremtés célja.
     */
    @Override
    public Integer getConnectionId() {
        return getHandler().getConnectionId();
    }

    /**
     * @return Eszközazonosító, ami segítségével megtudható a kliens típusa.
     */
    @Override
    public Integer getDeviceId() {
        return getHandler().getDeviceId();
    }
    
}
