package org.dyndns.fzoli.rccar.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ClientConnectionHelper;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

/**
 * A vezérlő kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends ClientConnectionHelper {
    
    /**
     * A vezérlő konfigurációja.
     */
    private final Config CONFIG;
    
    /**
     * Konstruktor az alábbi adatokkal.
     * Eszközazonosító: 1
     * Kapcsolatazonosítók: 0, 1, 2
     */
    public ConnectionHelper(Config config) {
        super(1, new int[] {0, 1, 2});
        CONFIG = config;
    }

    /**
     * Socket létrehozása.
     * Kapcsolódás a híd szerverhez a konfiguráció alapján.
     */
    @Override
    protected SSLSocket createConnection() throws GeneralSecurityException, IOException {
        return SSLSocketUtil.createClientSocket(CONFIG.getAddress(), CONFIG.getPort(), CONFIG.getCAFile(), CONFIG.getCertFile(), CONFIG.getKeyFile(), CONFIG.getPassword());
    }

    /**
     * A kapcsolatfeldolgozó példányosítása.
     * @param socket a kapcsolat a szerverrel
     * @param deviceId az eszközazonosító
     * @param connectionId a kapcsolatazonosító
     */
    @Override
    protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
        return new ConnectionHandler(socket, deviceId, connectionId);
    }
    
}
