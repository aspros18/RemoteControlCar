package org.dyndns.fzoli.socket.process;

import javax.net.ssl.SSLSocket;

/**
 * Interfész a külön szálban, az SSL socketen át adatfeldolgozást végző osztály írására szerver és kliens oldalra.
 * @author zoli
 */
public interface SecureProcess extends Process {
    
    /**
     * @return SSLSocket, amin keresztül folyik a kommunikáció.
     */
    @Override
    public SSLSocket getSocket();
    
    /**
     * A titkosított kommunikáció ezen oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     */
    public String getLocalCommonName();
    
    /**
     * A titkosított kommunikáció másik oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     */
    public String getRemoteCommonName();
    
}
