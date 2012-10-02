package org.dyndns.fzoli.socket.handler;

import org.dyndns.fzoli.socket.Socketter;

//TODO: Ami eddig Process volt, most átkerül a Handlerbe és a Process konstruktorban fogja megkapni az adatokat valamint a kivételek is át lesznek nevezve és helyezve. Egyelőre még nagyon az elején járok a cserének, ezért a Process még nem módosult.

/**
 * Kapcsolatkezelő implementálásához kliens és szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public interface Handler extends Socketter {
    
}
