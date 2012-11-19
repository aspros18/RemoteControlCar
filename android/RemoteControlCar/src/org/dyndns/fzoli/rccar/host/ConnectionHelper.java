package org.dyndns.fzoli.rccar.host;

import java.io.IOException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.socket.ClientConnectionHelper;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

/**
 * A hoszt kliens híd szerverhez való kapcsolódását oldja meg.
 * @author zoli
 */
public class ConnectionHelper extends ClientConnectionHelper {

	public ConnectionHelper(int deviceId, int[] connectionIds) {
		super(deviceId, connectionIds);
	}

	@Override
	protected SSLSocket createConnection() throws GeneralSecurityException, IOException {
		return null;
	}

	@Override
	protected AbstractSecureClientHandler createHandler(SSLSocket socket, int deviceId, int connectionId) {
		return null;
	}

}
