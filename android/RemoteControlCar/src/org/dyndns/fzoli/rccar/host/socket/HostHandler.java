package org.dyndns.fzoli.rccar.host.socket;

import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.process.SecureProcess;

public class HostHandler extends AbstractSecureClientHandler implements ConnectionKeys {

	public HostHandler(SSLSocket socket, int deviceId, int connectionId) {
		super(socket, deviceId, connectionId);
	}

	@Override
	protected SecureProcess selectProcess() {
		switch (getConnectionId()) {
        	case KEY_CONN_DISCONNECT:
        		return new HostDisconnectProcess(this);
        	case KEY_CONN_MESSAGE:
        		return new HostMessageProcess(this);
		}
		return null;
	}

}
