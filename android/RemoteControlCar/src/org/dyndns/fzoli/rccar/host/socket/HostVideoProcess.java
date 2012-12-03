package org.dyndns.fzoli.rccar.host.socket;

import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.ssl.Base64;

import org.dyndns.fzoli.rccar.host.Config;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.R;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

import android.content.Intent;
import android.util.Log;

/**
 * A kamerakép streamelése a hídnak MJPEG folyamként.
 * @author zoli
 */
public class HostVideoProcess extends AbstractSecureProcess {

	/**
	 * Az IP Webcam alkalmazás elindításához szükséges.
	 */
	private final ConnectionService SERVICE;
	
	/**
     * Biztonságos MJPEG stream küldő inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
	public HostVideoProcess(ConnectionService service, SecureHandler handler) {
		super(handler);
		SERVICE = service;
	}

	private HttpURLConnection conn;
	
	private void startIPWebcam() { // TODO: egyelőre csak teszt
		Config conf = SERVICE.getConfig();
		String port = conf.getCameraStreamPort();
		String user = conf.getCameraStreamUser();
		String password = conf.getCameraStreamPassword();
		Intent launcher = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
		Intent ipwebcam = 
			new Intent()
			.setClassName("com.pas.webcam", "com.pas.webcam.Rolling")
			.putExtra("cheats", new String[] {
					"set(Awake,true)", // ébrenlét fenntartása
					"set(Notification,true)", // rendszerikon ne legyen rejtve
					"set(Audio,1)", // audio stream kikapcsolása
					"set(Video,320,240)", // videó felbontás 320x240
					"set(DisableVideo,false)", // videó stream engedélyezése
					"set(Quality,30)", // JPEQ képkockák minősége 30 százalék
					"set(Port," + port + ")", // figyelés beállítása a 8080-as portra
					"set(Login," + user + ")", // felhasználónév beállítása a konfig alapján
					"set(Password," + password + ")" // jelszó beállítása a konfig alapján
					})
			.putExtra("hidebtn1", true) // Súgó gomb elrejtése
			.putExtra("caption2", SERVICE.getString(R.string.run_in_background)) // jobb oldali gomb feliratának beállítása
			.putExtra("intent2", launcher) // jobb oldali gomb eseménykezelőjének beállítása, hogy hozza fel az asztalt
		    .putExtra("returnto", launcher) // ha a programból kilépnek, szintén az asztal jön fel
		    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // activity indítása új folyamatként, hogy a service elindíthassa
		SERVICE.startActivity(ipwebcam); // a program indítása a fenti konfigurációval
		try {
			conn = (HttpURLConnection) new URL("http://127.0.0.1:" + port + "/videofeed").openConnection(); //kapcsolat objektum létrehozása
			conn.setRequestMethod("GET"); // GET metódus beállítása
			conn.setRequestProperty("Authorization", Base64.encodeBase64String(new String(user + ':' + password).getBytes()));
			// most, hogy minden be van állítva, kapcsolódás
			conn.connect();
			InputStream in = conn.getInputStream(); // mjpeg stream megszerzése
//			while(!getSocket().isClosed()) { // tesztelés
//				in.read();
//			}
		}
		catch (ConnectException ex) {
			Log.i(ConnectionService.LOG_TAG, "retry later", ex);
			try {
				Thread.sleep(2000);
			}
			catch (Exception e) {
				;
			}
			finally {
				startIPWebcam();
			}
		}
		catch (Exception ex) {
			Log.i(ConnectionService.LOG_TAG, "error", ex);
		}
	}

	private void stopIPWebcam() { //TODO: hatástalan a broadcast intent küldése. Miért?
		SERVICE.sendBroadcast(new Intent("com.pas.webcam.CONTROL").putExtra("action", "stop"));
	}
	
	@Override
	public void run() {
		try {
			Log.i(ConnectionService.LOG_TAG, "video process started");
			startIPWebcam();
            getSocket().getInputStream().read();
            Log.i(ConnectionService.LOG_TAG, "video process finished");
            stopIPWebcam();
        }
        catch (Exception ex) {
            Log.i(ConnectionService.LOG_TAG, "exception", ex);
        }
	}

}
