package org.dyndns.fzoli.rccar.host.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;

import javax.net.ssl.SSLException;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.SecureHandler;

import android.util.Log;

/**
 * A kamerakép streamelése a hídnak MJPEG folyamként.
 * Egy nem ellenőrzött, gyors streamelést valósít meg.
 * Egyszerűen a kiolvasott adatot, továbbítja a Híd felé.
 * Hátránya, hogy hiba esetén a header adatok újra elküldődnek, mivel
 * az osztály nem elemzi a kapott adatot, cserébe kíméle az erőforrást.
 * @author zoli
 */
public class UninspectedHostVideoProcess extends AbstractHostVideoProcess {

	/**
	 * Biztonságos MJPEG stream küldő inicializálása.
	 * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
	 * @throws NullPointerException ha handler null
	 */
	public UninspectedHostVideoProcess(ConnectionService service, ConnectionHelper helper, SecureHandler handler) {
		super(service, helper, handler);
	}
	
	/**
	 * Elkezdi streamelni az IP Webcam által küldött MJPEG folyamot.
	 * @see AbstractHostVideoProcess#run()
	 */
	@Override
	public void run() {
		try {
			Log.i(ConnectionService.LOG_TAG, "video process started");
			if (openIPWebcamConnection()) {
				try {
					int length;
					byte[] buffer = new byte[2048];
					InputStream in = conn.getInputStream();
					OutputStream out = getSocket().getOutputStream();
					while (!getSocket().isClosed()) { // amíg van kapcsolat, MJPEG stream olvasása és feltöltése a hídnak
						if (!SERVICE.getBinder().getHostData().isStreaming()) { // ha nem kell streamelni
							sleep(200); // kis várakozás
							continue; // ciklus újrakezdése
						}
						if (((length = in.read(buffer)) != -1)) try { // ha sikerült az olvasás és van adat
							out.write(buffer, 0, length); // megkísérli a feltöltést
						}
						catch (SocketException ex) { // ha nem sikerült az írás
							throw new SSLException(ex); // híddal való kacsolat hibaként feldolgozás
						}
						else { // ha sikerült az olvasás, de bezárult az IP Webcam: újrakezdés
							throw new SocketException("IP Webcam connection closed");
						}
					}
				}
				catch (SSLException ex) { // a hídnaknak való streamelés közben hiba történt
					Log.i(ConnectionService.LOG_TAG, "mjpeg streaming error. socket closed: " + getSocket().isClosed(), ex);
					// ha a socket nincs lezárva, a feldolgozó kapcsolatának lezárása és új process példányosítása új kapcsolattal
					if (!getSocket().isClosed()) {
						closeIPWebcamConnection(false);
						SERVICE.onConnectionError(ConnectionError.CONNECTION_LOST, HELPER);
						return;
					}
				}
				catch (SocketException ex) { // a szerver leállt, nagy valószínűséggel a felhasználó állította le
					Log.i(ConnectionService.LOG_TAG, "IP Webcam closed", ex);
					// mjpeg stream kapcsolat bontása, run metódus rekurzív hívása és return;
					if (!getSocket().isClosed()) {
						closeIPWebcamConnection(false);
						SERVICE.onConnectionError(ConnectionError.WEB_IPCAM_CLOSED, HELPER);
						return;
					}
				}
				catch (Exception ex) { // az IP Webcam nem streamel, valószínűleg a kamerát nem tudja kezelni
					Log.i(ConnectionService.LOG_TAG, "IP Webcam error", ex);
					SERVICE.onConnectionError(ConnectionError.WEB_IPCAM_UNREACHABLE, HELPER); // ip webcam fatális hiba hívása
				}
			}
			else {
				SERVICE.onConnectionError(ConnectionError.WEB_IPCAM_UNREACHABLE, HELPER); // ip webcam fatális hiba hívása
			}
			Log.i(ConnectionService.LOG_TAG, "video process finished");
			closeIPWebcamConnection(false); // a kapcsolat bontása, de az IP Webcam futva hagyása
		}
		catch (Throwable t) { // egyéb hiba történt
			Log.i(ConnectionService.LOG_TAG, "not important error", t);
			SERVICE.onConnectionError(ConnectionError.OTHER, HELPER);
		}
	}

}
