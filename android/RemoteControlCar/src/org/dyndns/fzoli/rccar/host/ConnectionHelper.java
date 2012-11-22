package org.dyndns.fzoli.rccar.host;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.clients.AbstractConnectionHelper;
import org.dyndns.fzoli.rccar.clients.ClientConfig;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

/**
 * A hoszt kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends AbstractConnectionHelper {

	public ConnectionHelper(ClientConfig config, int deviceId, int[] connectionIds) {
		super(config, deviceId, connectionIds);
	}

	@Override
	protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
		return null;
	}

}
