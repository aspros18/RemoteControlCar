package org.dyndns.fzoli.rccar.host.socket;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.clients.AbstractConnectionHelper;
import org.dyndns.fzoli.rccar.clients.ClientConfig;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

import android.util.Log;

/**
 * A hoszt kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends AbstractConnectionHelper implements ConnectionKeys {

	public ConnectionHelper(ClientConfig config) {
		super(config, KEY_DEV_HOST, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_MESSAGE});
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
	public void connect() {
		// TODO: a kapcsolódáskor null pointer exception. a hiba a createConnect metódusban keresendő az ősben
		super.connect();
	}
	
	@Override
	protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
		return new HostHandler(socket, deviceId, connectionId);
	}

}
