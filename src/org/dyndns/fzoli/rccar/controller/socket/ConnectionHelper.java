package org.dyndns.fzoli.rccar.controller.socket;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import javax.net.ssl.SSLSocket;
import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.clients.AbstractConnectionHelper;
import org.dyndns.fzoli.rccar.clients.ClientConfig;
import org.dyndns.fzoli.rccar.controller.Main;
import static org.dyndns.fzoli.rccar.controller.Main.showConnectionStatus;
import org.dyndns.fzoli.rccar.controller.view.ConnectionProgressFrame.Status;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

/**
 * A vezérlő kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends AbstractConnectionHelper implements ConnectionKeys {

    /**
     * Konstruktor.
     */
    public ConnectionHelper(ClientConfig config) {
        super(config, KEY_DEV_CONTROLLER, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_MESSAGE, KEY_CONN_VIDEO_STREAM});
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
     * Megpróbálja létrehozni a kapcsolatot.
     * Ha a tanúsítvány beolvasása nem sikerült, valószínűleg jelszóvédett a fájl, ezért bekéri a jelszót és újra próbálkozik.
     */
    @Override
    protected SSLSocket createConnection() throws GeneralSecurityException, IOException {
        try {
            return super.createConnection();
        }
        catch (KeyStoreException ex) {
            if (ex.getMessage().startsWith("failed to extract")) {
                Main.showPasswordDialog();
                return createConnection();
            }
            throw ex;
        }
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
        catch (NoRouteToHostException e) {
            showConnectionStatus(Status.CONNECTION_ERROR);
        }
        catch (UnknownHostException e) {
            showConnectionStatus(Status.UNKNOWN_HOST);
        }
        catch (SocketException e) {
            showConnectionStatus(Status.DISCONNECTED);
        }
        catch (KeyStoreException e) {
            showConnectionStatus(Status.KEYSTORE_ERROR);
        }
        catch (Exception e) {
            super.onException(e, connectionId);
        }
    }
    
}
