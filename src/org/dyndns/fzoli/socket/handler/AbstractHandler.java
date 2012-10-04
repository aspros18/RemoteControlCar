package org.dyndns.fzoli.socket.handler;

import java.net.Socket;
import org.dyndns.fzoli.socket.process.Process;

/**
 * Kapcsolatkezelő kliens és szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani és azt új szálban elindítja.
 * @author zoli
 */
public abstract class AbstractHandler implements Handler {
    
    private final Socket SOCKET;
    
    /**
     * @param socket Socket, amin keresztül folyik a kommunikáció.
     */
    public AbstractHandler(Socket socket) {
        SOCKET = socket;
    }

    /**
     * Kiválasztja a kapcsolatfeldolgozó objektumot az adatok alapján és elindítja.
     * A metódus csak akkor hívható meg, amikor már ismert a kapcsolatazonosító és eszközazonosító,
     */
    protected abstract Process selectProcess();

    /**
     * Miután az eszközazonosító és a kapcsolatazonosító közlése megtörtént,
     * lefut ez az inicializáló metódus, ami után a konkrét feldolgozás történik meg.
     * Ez a metódus az utód osztályoknak lett létrehozva inicializálás céljára.
     * Ebben a metódusban nem célszerű socketen át adatot küldeni vagy fogadni.
     */
    void init() {
        ;
    }

    /**
     * @return Socket, amin keresztül folyik a kommunikáció.
     */
    @Override
    public Socket getSocket() {
        return SOCKET;
    }
    
}
