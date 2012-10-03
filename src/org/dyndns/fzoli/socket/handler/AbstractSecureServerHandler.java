package org.dyndns.fzoli.socket.handler;

import java.net.Socket;
import javax.net.ssl.SSLSocket;

/**
 * Biztonságos kapcsolatkezelő szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public abstract class AbstractSecureServerHandler extends AbstractServerHandler implements SecureHandler {

    private String localCommonName, remoteCommonName;
    
    public AbstractSecureServerHandler(Socket socket, int connectionId) {
        super(socket, connectionId);
    }

    /**
     * Megszerzi a helyi és távoli tanúsítvány Common Name mezőjét.
     */
    @Override
    protected void init() {
        localCommonName = SecureUtil.getLocalCommonName(getSocket());
        remoteCommonName = SecureUtil.getRemoteCommonName(getSocket());
    }
    
    /**
     * @return SSLSocket, amin keresztül folyik a titkosított kommunikáció.
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
