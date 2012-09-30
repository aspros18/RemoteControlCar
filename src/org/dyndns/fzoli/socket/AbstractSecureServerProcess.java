package org.dyndns.fzoli.socket;

import javax.net.ssl.SSLSocket;

/**
 * Külön szálban, a biztonságos socketen át adatfeldolgozást végző szerver oldali osztály alapja.
 * @author zoli
 */
public abstract class AbstractSecureServerProcess extends AbstractServerProcess implements SecureProcess {

    private final String commonName;
    
    /**
     * @throws SecureProcessException ha nem megbízható a kapcsolat
     */
    public AbstractSecureServerProcess(SSLSocket socket, int connectionId) {
        super(socket, connectionId);
        commonName = SecureUtil.getCommonName(socket);
    }

    /**
     * @return SSLSocket, amin keresztül folyik a kommunikáció.
     */
    @Override
    public SSLSocket getSocket() {
        return (SSLSocket) super.getSocket();
    }

    /**
     * A titkosított kommunikáció másik oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getCommonName() {
        return commonName;
    }
    
}
