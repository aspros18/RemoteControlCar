package org.dyndns.fzoli.socket.process;

import javax.net.ssl.SSLSocket;

/**
 * Külön szálban, a biztonságos socketen át adatfeldolgozást végző kliens oldali osztály alapja.
 * @author zoli
 */
public abstract class AbstractSecureClientProcess extends AbstractClientProcess implements SecureProcess {

    private final String localCommonName, remoteCommonName;
    
    /**
     * Kliens oldali adatfeldolgozó konstruktora.
     * @param socket SSLSocket, amin keresztül folyik a titkosított kommunikáció.
     * @throws SecureProcessException ha nem megbízható a kapcsolat vagy a tanúsítvány hibás
     */
    public AbstractSecureClientProcess(SSLSocket socket) {
        super(socket);
        localCommonName = SecureUtil.getLocalCommonName(socket);
        remoteCommonName = SecureUtil.getRemoteCommonName(socket);
    }

    /**
     * @return SSLSocket, amin keresztül folyik a titkosított kommunikáció.
     */
    @Override
    public SSLSocket getSocket() {
        return (SSLSocket) super.getSocket();
    }

    /**
     * A titkosított kommunikáció ezen oldalán álló kliens tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getLocalCommonName() {
        return localCommonName;
    }

    /**
     * A titkosított kommunikáció másik oldalán álló szerver tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getRemoteCommonName() {
        return remoteCommonName;
    }
    
}
