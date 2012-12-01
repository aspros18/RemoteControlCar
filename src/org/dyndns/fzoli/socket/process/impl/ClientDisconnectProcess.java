package org.dyndns.fzoli.socket.process.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 * Az osztály a szerverrel kiépített kapcsolatot arra használja, hogy
 * másodpercenként ellenőrizze, hogy megszakadt-e a kapcsolat.
 * @author zoli
 */
public class ClientDisconnectProcess extends DisconnectProcess {

    /**
     * Kliens oldalra időtúllépés detektáló.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @param timeout1 az első időtúllépés ideje ezredmásodpercben (nem végzetes korlát)
     * @param timeout2 a második időtúllépés ideje ezredmásodpercben (végzetes korlát)
     * @param waiting két ellenőrzés között eltelt idő
     * @throws NullPointerException ha handler null
     */
    public ClientDisconnectProcess(SecureHandler handler, int timeout1, int timeout2, int waiting) {
        super(handler, timeout1, timeout2, waiting);
    }
    
    /**
     * A socket bementének olvasására be lehet állítani időtúllépést.
     * Erre alapozva megtudható, hogy él-e még a kapcsolat a szerverrel.
     */
    @Override
    public void run() {
        onConnect(); // onConnect eseménykezelő hívása, hogy a kapcsolat létrejött
        try {
            InputStream in = getSocket().getInputStream(); // kliens oldali bemenet
            OutputStream out = getSocket().getOutputStream(); // kliens oldali kimenet
            getSocket().setSoTimeout(getFirstTimeout()); // in.read() metódusnak az 1. időtúllépés beállítása
            while(true) { // végtelen ciklus, amit SocketException zár be a kapcsolat végén
                try {
                    beforeAnswer(); // olvasás előtti eseménykezelő hívása
                    in.read(); // válasz a szervertől
                    setTimeoutActive(false, null); // 2. időtúllépés inaktiválása, ha kell
                    callAfterAnswer(); // olvasás utáni eseménykezelő hívása
                    out.write(1); // üzenés a szervernek ...
                    out.flush(); // ... azonnal
                }
                catch (SocketTimeoutException ex) { // ha az in.read() az 1. időkorláton belül nem kapott bájtot
                    setTimeoutActive(true, ex); // 2. időtúllépés aktiválása, ha kell
                    callOnTimeout(ex); // időtúllépés eseménykezelő hívása
                }
                Thread.sleep(getWaiting()); // várakozik egy kicsit, hogy a sávszélességet ne terhelje, és hogy szinkronban legyen a szerverrel
            }
        }
        catch (Exception ex) { // ha bármilyen hiba történt
            callOnDisconnect(ex); // disconnect eseménykezelő hívása, ha kell
        }
    }
    
}
