package org.dyndns.fzoli.rccar.host;

import ioio.lib.util.android.IOIOService;
import android.content.Intent;

public class ConnectionService extends IOIOService {
	
	private final ConnectionBinder BINDER = new ConnectionBinder();
	
	@Override
	public IOIOVehicleLooper createIOIOLooper() {
		return new IOIOVehicleLooper(BINDER);
	}
	
	@Override
	public ConnectionBinder onBind(Intent intent) {
		return BINDER;
	}

}
