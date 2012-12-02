package org.dyndns.fzoli.rccar.host.socket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.ClientDisconnectProcess;

import android.util.Log;

public class HostDisconnectProcess extends ClientDisconnectProcess implements ConnectionKeys {

	/**
	 * A szolgáltatás referenciája, hogy lehessen a változásról értesíteni.
	 */
	private final ConnectionService SERVICE;
	
	/**
	 * Az utolsó időtúllépéskori vezérlőjel.
	 */
	private int lastX, lastY;
	
	/**
	 * Hoszt oldali időtúllépés detektáló.
	 */
	public HostDisconnectProcess(ConnectionService service, SecureHandler handler) {
		super(handler, DC_TIMEOUT1, DC_TIMEOUT2, DC_DELAY); // 1 és 10 mp időtúllépés, 250 ms sleep
		SERVICE = service;
	}
	
	/**
	 * Sikeres kapcsolódás esetén az esetleges előző hibák eltüntetése.
	 */
	@Override
	protected void onConnect() {
		super.onConnect();
		Log.i(ConnectionService.LOG_TAG, "connected to the bridge");
		SERVICE.setConnectionError(null);
	}
	
	/**
	 * Időtúllépés esetén a jármű megállítása és az aktuális vezérlőjel mentése.
	 */
	@Override
	protected void onTimeout(Exception ex) throws Exception {
		Log.i(ConnectionService.LOG_TAG, "on timeout");
		lastX = SERVICE.getBinder().resetX();
		lastY = SERVICE.getBinder().resetY();
	}
	
	/**
	 * Ha az időtúllépés után is él a kapcsolat.
	 * A vezérlőjel visszaállítása, mint ha semmi sem történt volna.
	 */
	@Override
	protected void afterTimeout() throws Exception {
		Log.i(ConnectionService.LOG_TAG, "after timeout");
		SERVICE.getBinder().setXY(lastX, lastY);
	}
	
	/**
	 * Disconnect esetén jelzés a szolgáltatásnak, hogy a kapcsolat elveszett és megállítja a járművet.
	 */
	@Override
	protected void onDisconnect(Exception ex) {
		super.onDisconnect(ex);
		Log.i(ConnectionService.LOG_TAG, "disconnected from the bridge", ex);
		SERVICE.setConnectionError(ConnectionError.CONNECTION_LOST);
		SERVICE.getBinder().resetXY();
	}
	
}
