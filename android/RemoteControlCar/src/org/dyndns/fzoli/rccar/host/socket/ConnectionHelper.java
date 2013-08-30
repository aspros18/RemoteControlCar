package org.dyndns.fzoli.rccar.host.socket;

import java.security.KeyStoreException;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.clients.AbstractConnectionHelper;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

import android.util.Log;

/**
 * A hoszt kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends AbstractConnectionHelper implements ConnectionKeys {

	/**
	 * A szolgáltatás referenciája, hogy lehessen a változásról értesíteni és a konfiguráció is tőle kérődik le.
	 */
	private final ConnectionService SERVICE;
	
	/**
	 * Konstruktor.
	 * @param service a szolgáltatás referenciája
	 */
	public ConnectionHelper(ConnectionService service) {
		super(service.getConfig(), KEY_DEV_HOST, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_MESSAGE, KEY_CONN_VIDEO_STREAM});
		SERVICE = service;
	}
	
	/**
	 * A kapcsolatok bezárása.
	 * Meghívása után a szolgáltatásban frissül a kapcsolódás állapota.
	 */
	@Override
	public void disconnect() {
		super.disconnect();
		updateConnectionState(false); // nincs kapcsolódás folyamatban
	}
	
	/**
	 * Kapcsolódás a hídhoz.
	 * Meghívása előtt a szolgáltatásban frissül a kapcsolódás állapota.
	 */
	@Override
	public void connect() {
		updateConnectionState(true); // kapcsolódás folyamatban
		super.connect();
	}
	
	/**
	 * Ha a kapcsolódás végetért, ez a metódus fut le.
	 * A szolgáltatásban frissül a kapcsolódás állapota.
	 */
	@Override
	protected void onConnected() {
		super.onConnected();
		updateConnectionState(false); // nincs kapcsolódás folyamatban
	}
	
	/**
	 * Handler példányosítása.
	 * @param socket a kapcsolat a szerverrel
	 * @param deviceId az eszközazonosító
	 * @param connectionId a kapcsolatazonosító
	 */
	@Override
	protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
		return new HostHandler(this, SERVICE, socket, deviceId, connectionId);
	}
	
	/**
	 * Ha kivétel keletkezik, ebben a metódusban le lehet kezelni.
	 * A szolgáltatás univerzális kapcsolódás hibakezelő metódusát hívja meg a kivételnek megfelelően.
	 * A hibakezelő metódus megjeleníti az értesítést a felületen és a hibától függően reagál.
	 * Az OTHER hibakategória esetén nem jelenik meg értesítés a felületen.
	 * Ha a hibakategórió fatális hiba, nem lesz megismételve a kapcsolódás.
	 * További részlet: {@link ConnectionService.setConnectionError}
	 * @param ex a keletkezett kivétel
	 * @param connectionId a közben használt kapcsolatazonosító
	 */
	@Override
	protected void onException(Exception ex, int connectionId) {
		ConnectionError err = null;
		try {
			Log.i(ConnectionService.LOG_TAG, "connection error", ex);
			throw ex;
		}
		catch (NullPointerException e) {
			err = ConnectionError.WRONG_CERTIFICATE_SETTINGS;
		}
		catch (KeyStoreException e) {
			err = ex.getMessage().startsWith("failed to extract") ? ConnectionError.WRONG_CERTIFICATE_PASSWORD : ConnectionError.WRONG_CERTIFICATE_SETTINGS;
		}
		catch (Exception e) {
			err = ConnectionError.CONNECTION_ERROR;
		}
		SERVICE.onConnectionError(err, this);
	}
	
	/**
	 * A kapcsolódás állapotának frissítése a szolgáltatásban.
	 * Az MJPEG-folyam inaktiválása kapcsolódáskor és lekapcsolódáskor is.
	 * @param connecting true esetén a kapcsolódás folyamatban van, egyébként meg nincs folyamatban.
	 */
	private void updateConnectionState(boolean connecting) {
		SERVICE.updateNotificationText();
		SERVICE.getBinder().getHostData().setStreaming(false);
		SERVICE.getBinder().fireConnectionStateChange(connecting);
	}
	
}
