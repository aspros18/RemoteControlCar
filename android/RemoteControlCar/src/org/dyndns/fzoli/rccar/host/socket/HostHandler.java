package org.dyndns.fzoli.rccar.host.socket;

import java.security.KeyStoreException;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;

import org.dyndns.fzoli.rccar.ConnectionKeys;
import org.dyndns.fzoli.rccar.host.ConnectionService;
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
		try {
			throw ex;
		}
		catch (RemoteHandlerException e) {
			Log.i("test", "remote handler exception", e);
		}
		catch (SSLHandshakeException e) {
			Log.i("test", "handshake exception", e);
		}
		catch (KeyStoreException e) {
			Log.e("test", "keystore exception", e);
		}
		catch (Exception e) {
			super.onException(e);
		}
	}
	
	@Override
	protected SecureProcess selectProcess() {
		switch (getConnectionId()) {
        	case KEY_CONN_DISCONNECT:
        		return new HostDisconnectProcess(SERVICE, this);
        	case KEY_CONN_MESSAGE:
        		return new HostMessageProcess(this);
		}
		return null;
	}

}
