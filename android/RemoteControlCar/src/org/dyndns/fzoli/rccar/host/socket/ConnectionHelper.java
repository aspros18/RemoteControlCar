package org.dyndns.fzoli.rccar.host.socket;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.clients.AbstractConnectionHelper;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

import android.util.Log;

/**
 * A hoszt kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends AbstractConnectionHelper implements ConnectionKeys {

	private final ConnectionService SERVICE;
	
	public ConnectionHelper(ConnectionService service) {
		super(service.getConfig(), KEY_DEV_HOST, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_MESSAGE});
		SERVICE = service;
	}
	
	@Override
	protected SSLSocket createConnection() throws GeneralSecurityException, IOException {
		Log.i("test", "config: " + CONFIG);
		try {
			return super.createConnection();
		}
		catch (Throwable ex) {
			Log.e("test", "connection error", ex);
			return null;
		}
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
		SERVICE.updateNotificationText();
	}
	
	@Override
	protected void onConnected() {
		super.onConnected();
		SERVICE.updateNotificationText();
	}
	
	@Override
	protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
		return new HostHandler(SERVICE, socket, deviceId, connectionId);
	}

}
