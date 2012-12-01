package org.dyndns.fzoli.rccar.host.socket;

import java.io.InvalidClassException;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

import android.util.Log;

public class HostMessageProcess extends MessageProcess {

	private final ConnectionService SERVICE;
	
	public HostMessageProcess(ConnectionService service, SecureHandler handler) {
		super(handler);
		SERVICE = service;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		SERVICE.setConnectionError(null);
	}
	
	@Override
	protected void onMessage(Object arg0) {
		
	}

	@Override
	protected void onException(Exception ex) {
		ConnectionError err = null;
		try {
			throw ex;
		}
		catch (InvalidClassException e) {
			Log.e(ConnectionService.LOG_TAG, "bridge is not compatible with this client", e);
			err = ConnectionError.WRONG_CLIENT_VERSION;
		}
		catch (Exception e) {
			err = ConnectionError.OTHER;
			Log.i(ConnectionService.LOG_TAG, "unknown error", e);
		}
		SERVICE.setConnectionError(err);
	}

}
