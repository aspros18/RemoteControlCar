package org.dyndns.fzoli.rccar.controller.socket;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.controller.Config;
import org.dyndns.fzoli.rccar.controller.ConnectionProgressFrame.Status;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.socket.ClientConnectionHelper;
import org.dyndns.fzoli.socket.SSLSocketUtil;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

/**
 * A vezérlő kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends ClientConnectionHelper implements ConnectionKeys {
    
    /**
     * A vezérlő konfigurációja.
     */
    private final Config CONFIG;
    
    /**
     * Konstruktor.
     * TODO: Egyelőre teszt.
     */
    public ConnectionHelper(Config config) {
        super(KEY_DEV_CONTROLLER, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_DUMMY});
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
        return new ControllerHandler(socket, deviceId, connectionId);
    }

    /**
     * Ha kapcsolódott a kliens összes szála a szerverhez, a kapcsolódásjelzés elrejtődik.
     */
    @Override
    protected void onConnected() {
        showConnectionStatus(null);
    }

    /**
     * Kivételek kezelése.
     * Kapcsolódás hiba esetén hiba jelenik meg.
     * Nem várt hiba esetén kivétel dobódik, amit egy dialógus ablak jelenít meg.
     */
    @Override
    protected void onException(Exception ex, int connectionId) {
        try {
            throw ex;
        }
        catch (ConnectException e) {
            showConnectionStatus(Status.CONNECTION_ERROR);
        }
        catch (UnknownHostException e) {
            showConnectionStatus(Status.UNKNOWN_HOST);
        }
        catch (SocketException e) {
            showConnectionStatus(Status.DISCONNECTED);
        }
        catch (Exception e) {
            super.onException(e, connectionId);
        }
    }
    
}
