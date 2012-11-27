package org.dyndns.fzoli.rccar.host.socket;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.clients.AbstractConnectionHelper;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

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
