package org.dyndns.fzoli.rccar.host.socket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

public class HostDisconnectProcess extends ClientDisconnectProcess implements ConnectionKeys {

	public HostDisconnectProcess(SecureHandler handler) {
		super(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY); // 1 és 10 mp időtúllépés, 250 ms sleep
	}

}
