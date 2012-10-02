package org.dyndns.fzoli.socket.handler;

import org.dyndns.fzoli.socket.Socketter;

/**
 * Socketfeldolgozó implementálásához kliens és szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public interface Handler extends Socketter {
    
    /**
     * Megadja, hány socketkapcsolat szükséges a feladat ellátásához.
     */
    public int getConnectionCount();
    
    /**
     * Megadja, hány socketkapcsolat van kiépítve jelenleg.
     */
    public int getConnections();
    
    /**
     * Létrehozza a kapcsolatfeldolgozó objektumot.
     */
    public Process createProcess();
    
    /**
     * Elindítja a kapcsolatfeldolgozót új szálban.
     */
    public void start();
    
}
