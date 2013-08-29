package org.dyndns.fzoli.rccar.host.socket;

import java.io.InputStream;
import java.io.OutputStream;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.mjpeg.impl.MjpegStreamRepeater;

import android.util.Log;

/**
 * A kamerakép streamelése a hídnak MJPEG folyamként.
 * A streamelés erőforrásigényesebb, mint a {@link UninspectedHostVideoProcess} esetén,
 * mivel minden egyes képkocka külön töltődik le, de cserébe hiba esetén a header adatok
 * nem küldődnek el újra a szervernek és ha a szerver szünetelteti a streamelést,
 * nem kap utólag elavult képkockákat.
 * @author zoli
 */
public class InspectedHostVideoProcess extends AbstractHostVideoProcess {

	/**
	 * Megadja, hanyadik alkalommal indítják a streamelést.
	 * Csak az első alkalommal kell a header adatokat kiküldeni.
	 */
	private int runCounter = 0;
	
	/**
	 * Biztonságos MJPEG stream küldő inicializálása.
	 * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
	 * @throws NullPointerException ha handler null
	 */
	public InspectedHostVideoProcess(ConnectionService service, ConnectionHelper helper, SecureHandler handler) {
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
					InputStream in = conn.getInputStream();
					OutputStream out = getSocket().getOutputStream();
					new MjpegStreamRepeater(in, out, runCounter++ == 0) {
						
						protected boolean isUnwriteable() {
							return !SERVICE.getBinder().getHostData().isStreaming(); // ha nem kell streamelni, akkor tiltja a képkockák továbbítását a Híd felé
						};
						
						@Override
						protected boolean isInterrupted() {
							if (getSocket().isClosed()) return true; // ha a socket kapcsolat végetért, befejezi a streamelést
							return super.isInterrupted();
						}
						
						@Override
						protected boolean onException(Exception ex, int err) {
							if (err == ERR_FIRST_READ || err == ERR_READ) { // nem sikerült olvasni a streamet (az IP Webcam talán leállt és nagy valószínűséggel a felhasználó állította le)
								Log.i(ConnectionService.LOG_TAG, "IP Webcam closed", ex);
								// mjpeg stream kapcsolat bontása, run metódus rekurzív hívása és return;
								if (!getSocket().isClosed()) {
									closeIPWebcamConnection(false);
									SERVICE.onConnectionError(ConnectionError.WEB_IPCAM_CLOSED, HELPER);
									return false;
								}
							}
							else if (err == ERR_HEADER_WRITE || err == ERR_WRITE) { // nem sikerült írni a socket kimenő folyamába (a hídnaknak való streamelés közben hiba történt)
								Log.i(ConnectionService.LOG_TAG, "mjpeg streaming error. socket closed: " + getSocket().isClosed(), ex);
								// ha a socket nincs lezárva, a feldolgozó kapcsolatának lezárása és új process példányosítása új kapcsolattal
								if (!getSocket().isClosed()) {
									closeIPWebcamConnection(false);
									SERVICE.onConnectionError(ConnectionError.CONNECTION_LOST, HELPER);
									return false;
								}
							}
							return !getSocket().isClosed();
						}
						
					}.handleConnection();
				}
				catch (Exception ex) { // ismeretlen hiba történt
					Log.i(ConnectionService.LOG_TAG, "unknown error", ex);
					SERVICE.onConnectionError(ConnectionError.OTHER, HELPER);
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
