package org.dyndns.fzoli.rccar.host.socket;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

public class HostMessageProcess extends MessageProcess {

	public HostMessageProcess(SecureHandler handler) {
		super(handler);
	}

	@Override
	protected void onMessage(Object arg0) {
		
	}

}
