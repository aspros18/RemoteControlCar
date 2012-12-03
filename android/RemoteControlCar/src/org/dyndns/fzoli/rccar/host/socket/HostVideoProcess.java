package org.dyndns.fzoli.rccar.host.socket;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

import android.util.Log;

/**
 * A kamerakép streamelése a hídnak MJPEG folyamként.
 * @author zoli
 */
public class HostVideoProcess extends AbstractSecureProcess {

	/**
     * Biztonságos MJPEG stream küldő inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
	public HostVideoProcess(SecureHandler handler) {
		super(handler);
	}

	@Override
	public void run() {
		try {
			Log.i(ConnectionService.LOG_TAG, "video process started");
            getSocket().getInputStream().read();
            Log.i(ConnectionService.LOG_TAG, "video process finished");
        }
        catch (Exception ex) {
            Log.i(ConnectionService.LOG_TAG, "exception", ex);
        }
	}

}
