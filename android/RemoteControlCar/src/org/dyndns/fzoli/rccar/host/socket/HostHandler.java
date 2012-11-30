package org.dyndns.fzoli.rccar.host.socket;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyStoreException;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;
import org.dyndns.fzoli.socket.handler.exception.RemoteHandlerException;
import org.dyndns.fzoli.socket.process.SecureProcess;

import android.util.Log;

public class HostHandler extends AbstractSecureClientHandler implements ConnectionKeys {

	private final ConnectionService SERVICE;
	
	public HostHandler(ConnectionService service, SSLSocket socket, int deviceId, int connectionId) {
		super(socket, deviceId, connectionId);
		SERVICE = service;
	}
	
	@Override
	protected void onException(Exception ex) {
		ConnectionError err = null;
		try {
			Log.i("test", "handler exception", ex);
			throw ex;
		}
		catch (RemoteHandlerException e) {
			err = ConnectionError.CONNECTION_REFUSED;
		}
		catch (SocketTimeoutException e) {
			err = ConnectionError.CONNECTION_LOST;
		}
		catch (SSLHandshakeException e) {
			err = ConnectionError.INVALID_CERTIFICATE;
		}
		catch (SocketException e) {
			err = ConnectionError.CONNECTION_ERROR;
		}
		catch (KeyStoreException e) {
			err = ConnectionError.WRONG_CERTIFICATE_SETTINGS;
		}
		catch (Exception e) {
			err = ConnectionError.OTHER;
		}
		SERVICE.setConnectionError(err);
	}
	
	@Override
	protected SecureProcess selectProcess() {
		switch (getConnectionId()) {
        	case KEY_CONN_DISCONNECT:
        		return new HostDisconnectProcess(SERVICE, this);
        	case KEY_CONN_MESSAGE:
        		return new HostMessageProcess(SERVICE, this);
		}
		return null;
	}

}
