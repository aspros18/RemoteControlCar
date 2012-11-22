package org.dyndns.fzoli.rccar.host.socket;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.clients.AbstractConnectionHelper;
import org.dyndns.fzoli.rccar.clients.ClientConfig;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

/**
 * A hoszt kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends AbstractConnectionHelper implements ConnectionKeys {

	public ConnectionHelper(ClientConfig config) {
		super(config, KEY_DEV_HOST, new int[] {KEY_CONN_DISCONNECT, KEY_CONN_MESSAGE});
	}

	@Override
	protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
		return new HostHandler(socket, deviceId, connectionId);
	}

}
