package org.dyndns.fzoli.rccar.host.socket;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
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

	private final ConnectionService SERVICE;
	
	public ConnectionHelper(ConnectionService service) {
		super(service.getConfig(), KEY_DEV_HOST, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_MESSAGE});
		SERVICE = service;
	}
	
	@Override
	public void disconnect() {
		super.disconnect();
		update(false);
	}
	
	@Override
	public void connect() {
		update(true);
		super.connect();
	}
	
	@Override
	protected void onConnected() {
		super.onConnected();
		update(false);
	}
	
	@Override
	protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
		return new HostHandler(SERVICE, socket, deviceId, connectionId);
	}
	
	@Override
	protected void onException(Exception ex, int connectionId) {
		ConnectionError err = null;
		try {
			Log.i(ConnectionService.LOG_TAG, "connection error", ex);
			throw ex;
		}
		catch (ConnectException e) {
			err = ConnectionError.CONNECTION_ERROR;
		}
		catch (NoRouteToHostException e) {
			err = ConnectionError.CONNECTION_ERROR;
		}
		catch (UnknownHostException e) {
			err = ConnectionError.CONNECTION_ERROR;
		}
		catch (SocketException e) {
			err = ConnectionError.CONNECTION_LOST;
		}
		catch (NullPointerException e) {
			err = ConnectionError.WRONG_CERTIFICATE_SETTINGS;
		}
		catch (KeyStoreException e) {
			err = ConnectionError.WRONG_CERTIFICATE_SETTINGS;
		}
		catch (Exception e) {
			err = ConnectionError.OTHER;
		}
		SERVICE.setConnectionError(err);
	}
	
	private void update(boolean connecting) {
		SERVICE.updateNotificationText();
		SERVICE.getBinder().fireConnectionStateChange(connecting);
	}
	
}
