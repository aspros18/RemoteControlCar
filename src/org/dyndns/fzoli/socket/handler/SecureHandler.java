package org.dyndns.fzoli.socket.handler;

import org.dyndns.fzoli.socket.SecureSocketter;

/**
 * Biztonságos socketfeldolgozó implementálásához kliens és szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public interface SecureHandler extends Handler, SecureSocketter {
    
}
