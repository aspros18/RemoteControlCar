package org.dyndns.fzoli.rccar.host.socket;

import java.io.EOFException;

import javax.net.ssl.SSLException;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

import android.util.Log;

public class HostMessageProcess extends MessageProcess {

	public HostMessageProcess(SecureHandler handler) {
		super(handler);
	}

	@Override
	protected void onMessage(Object arg0) {
		
	}

	@Override
	protected void onException(Exception ex) {
		try {
			throw ex;
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
