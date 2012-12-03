package org.dyndns.fzoli.rccar.host.socket;

import java.io.InvalidClassException;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

import android.util.Log;

public class HostMessageProcess extends MessageProcess {

	/**
	 * A szolgáltatás referenciája, hogy lehessen a változásról értesíteni.
	 */
	private final ConnectionService SERVICE;
	
	public HostMessageProcess(ConnectionService service, SecureHandler handler) {
		super(handler);
		SERVICE = service;
	}
	
	/**
	 * A híd által küldött üzenetek feldolgozása.
	 * @param msg az üzenet
	 */
	@Override
	protected void onMessage(Object msg) {
		// TODO
	}

	/**
	 * Ha üzenetküldés vagy fogadás közben hiba történik, ez a metódus fut le.
	 * Itt csak egyetlen hiba az említésre méltó:
	 * eltérő kliens és szerver verzió, tehát nem kompatibilis a kliens a szerverrel, nem lehet üzenni
	 * A szolgáltatás univerzális kapcsolódás hibakezelő metódusát hívja meg a kivételnek megfelelően.
	 * A hibakezelő metódus megjeleníti az értesítést a felületen és a hibától függően reagál.
	 * Az OTHER hibakategória esetén nem jelenik meg értesítés a felületen.
	 * Ha a hibakategórió fatális hiba, nem lesz megismételve a kapcsolódás.
	 */
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
		SERVICE.onConnectionError(err);
	}

}
