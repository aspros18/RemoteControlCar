package org.dyndns.fzoli.socket.process;

import org.dyndns.fzoli.socket.SecureSocketter;

/**
 * Interfész a külön szálban, az SSL socketen át adatfeldolgozást végző osztály írására szerver és kliens oldalra.
 * @author zoli
 */
public interface SecureProcess extends Process, SecureSocketter {
    
}
