package org.dyndns.fzoli.rccar.host.socket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

import android.util.Log;

public class HostDisconnectProcess extends ClientDisconnectProcess implements ConnectionKeys {

	private final ConnectionService SERVICE;
	
	public HostDisconnectProcess(ConnectionService service, SecureHandler handler) {
		super(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY); // 1 és 10 mp időtúllépés, 250 ms sleep
		SERVICE = service;
	}
	
	@Override
	protected void onTimeout(Exception ex) throws Exception {
		super.onTimeout(ex);
		SERVICE.getBinder().setX(0, false);
		SERVICE.getBinder().setY(0, false);
	}
	
	@Override
	protected void onDisconnect(Exception ex) {
		super.onDisconnect(ex);
		Log.i("test", "disconnected from the bridge");
		SERVICE.setConnectionError(ConnectionError.CONNECTION_LOST);
	}
	
}
