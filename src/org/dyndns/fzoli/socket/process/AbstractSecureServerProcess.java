package org.dyndns.fzoli.socket.process;

import org.dyndns.fzoli.socket.handler.SecureUtil;
import javax.net.ssl.SSLSocket;

/**
 * Külön szálban, a biztonságos socketen át adatfeldolgozást végző szerver oldali osztály alapja.
 * @author zoli
 */
public abstract class AbstractSecureServerProcess extends AbstractServerProcess implements SecureProcess {

    private final String localCommonName, remoteCommonName;
    
    /**
     * Szerver oldali adatfeldolgozó konstruktora.
     * @param socket SSLSocket, amin keresztül folyik a titkosított kommunikáció.
     * @throws SecureProcessException ha nem megbízható a kapcsolat vagy a tanúsítvány hibás
     */
    public AbstractSecureServerProcess(SSLSocket socket, int connectionId) {
        super(socket, connectionId);
        localCommonName = SecureUtil.getLocalCommonName(socket);
        remoteCommonName = SecureUtil.getRemoteCommonName(socket);
    }

    /**
     * @return SSLSocket, amin keresztül folyik a kommunikáció.
     */
    @Override
    public SSLSocket getSocket() {
        return (SSLSocket) super.getSocket();
    }

    /**
     * A titkosított kommunikáció ezen oldalán álló szerver tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getLocalCommonName() {
        return localCommonName;
    }

    /**
     * A titkosított kommunikáció másik oldalán álló kliens tanúsítványának CN mezőjét adja vissza.
     */
    @Override
    public String getRemoteCommonName() {
        return remoteCommonName;
    }
    
}
