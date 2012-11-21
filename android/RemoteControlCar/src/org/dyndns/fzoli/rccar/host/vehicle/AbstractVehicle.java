package org.dyndns.fzoli.rccar.host.vehicle;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

import org.dyndns.fzoli.rccar.host.ConnectionBinder;

public abstract class AbstractVehicle extends BaseIOIOLooper implements Vehicle {
	
	private final ConnectionBinder BINDER;
	
	private boolean connected = false;
	
	public AbstractVehicle(ConnectionBinder binder) {
		BINDER = binder;
	}
	
	@Override
	public boolean isConnected() {
		return connected;
	}
	
	@Override
	public int getX() {
		return BINDER.getX();
	}
	
	@Override
	public int getY() {
		return BINDER.getY();
	}
	
	@Override
	protected void setup() throws ConnectionLostException, InterruptedException {
		updateState(true);
		ioio_.openDigitalOutput(IOIO.LED_PIN, !true);
	}
	
	@Override
	public void disconnected() {
		updateState(false);
	}
	
	private void updateState(boolean connected) {
		this.connected = connected;
		BINDER.getService().updateNotificationText();
	}
	
}
