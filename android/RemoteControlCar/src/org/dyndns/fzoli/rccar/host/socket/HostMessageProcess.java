package org.dyndns.fzoli.rccar.host.socket;

import java.io.EOFException;
import java.io.InvalidClassException;

import javax.net.ssl.SSLException;

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
	protected void onMessage(Object arg0) {
		
	}

	@Override
	protected void onException(Exception ex) {
//		ConnectionError err = null;
//		try {
//			throw ex;
//		}
//		catch (InvalidClassException e) {
//			err = ConnectionError.WRONG_CLIENT_VERSION;
//		}
//		catch (Exception e) {
//			err = ConnectionError.OTHER;
//		}
//		SERVICE.setConnectionError(err);
		try {
			throw ex;
		}
		catch (InvalidClassException e) {
			Log.e("test", "bridge is not compatible with this client", e);
		}
		catch (EOFException e) {
			;
		}
		catch (SSLException e) {
			Log.i("test", "ssl exception", e);
		}
		catch (Exception e) {
			super.onException(e);
		}
	}

}
