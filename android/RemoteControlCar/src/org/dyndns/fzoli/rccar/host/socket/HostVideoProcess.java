package org.dyndns.fzoli.rccar.host.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.ssl.Base64;
import org.dyndns.fzoli.rccar.host.Config;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
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
	 * A kiépített MJPEG stream HTTP kapcsolata.
	 */
	private HttpURLConnection conn;
	
	/**
     * Biztonságos MJPEG stream küldő inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
	public HostVideoProcess(ConnectionService service, SecureHandler handler) {
		super(handler);
		SERVICE = service;
	}

	/**
	 * Elindítja az IP Webcam alkalmazás szerverét a megadott paraméterekkel.
	 * Ha a szerver már fut, a paraméterben megadott beállítások nem érvényesülnek.
	 * Miután a szerver elindult, a kameraképet mutató Activity megjelenik egy elrejtő gombbal.
	 * @param port a szerver portja, amin figyel
	 * @param user a szerver eléréséhez szükséges felhasználónév (üres string esetén névtelen hozzáférés)
	 * @param password nem névtelen hozzáférés esetén a felhasználónévhez tartozó jelszó
	 */
	private void startIPWebcamActivity(String port, String user, String password) {
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
	}
	
	/**
	 * Kapcsolódik az IP Webcam alkalmazás MJPEG folyamához.
	 * Öt alkalommal kísérli meg a kapcsolódást 2 másodperc szünetet tartva a két próbálkozás között.
	 * Ha a kapcsolódás nem sikerül, kiküldi az alkalmazást elindító utasítást az Androidnak.
	 * Az alkalmazást a felhasználó beállítása alapján indítja el és ez alapján kapcsolódik a helyi szerverhez.
	 * @return true, ha sikerült a kapcsolódás, egyébként false
	 */
	private boolean openIPWebcamConnection() {
		Config conf = SERVICE.getConfig();
		String port = conf.getCameraStreamPort(); // szerver port
		String user = conf.getCameraStreamUser(); // felhasználónév
		String password = conf.getCameraStreamPassword(); // jelszó
		
		String httpUrl = "http://127.0.0.1:" + port + "/videofeed"; // a szerver pontos címe
		String authProp = "Basic " + Base64.encodeBase64String(new String(user + ':' + password).getBytes()); // a HTTP felhasználóazonosítás Base64 alapú
		
		for (int i = 1; i <= 5; i++) { // 5 próbálkozás a kapcsolat létrehozására
			try {
				conn = (HttpURLConnection) new URL(httpUrl).openConnection(); //kapcsolat objektum létrehozása
				conn.setRequestMethod("GET"); // GET metódus beállítása
				if (user != null && !user.equals("")) conn.setRequestProperty("Authorization", authProp); // ha van azonosítás, adat beállítása
				conn.connect(); // kapcsolódás
				return true;
			}
			catch (Exception ex) {
				Log.i(ConnectionService.LOG_TAG, "retry later", ex);
				startIPWebcamActivity(port, user, password); // IP Webcam program indítása
				try {
					Thread.sleep(2000); // 2 másodperc várakozás
				}
				catch (Exception e) {
					;
				}
			}
		}
		
		return false;
	}

	/**
	 * Lezárja a kapcsolatot az IP Webcam alkalmazás szerverével és leállítja az IP Webcam programot.
	 */
	private void closeIPWebcamConnection() {
		if (conn != null) conn.disconnect();
		SERVICE.sendBroadcast(new Intent("com.pas.webcam.CONTROL").putExtra("action", "stop"));
	}
	
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
					while (!getSocket().isClosed()) {
						if (((length = in.read(buffer)) != -1)) out.write(buffer, 0, length);
						else throw new Exception("IP Webcam closed");
					}
				}
				catch (Exception ex) {
					Log.i(ConnectionService.LOG_TAG, "wth?", ex);
					getHandler().closeProcesses();
				}
			}
			else {
				SERVICE.onConnectionError(ConnectionError.WEB_IPCAM_UNREACHABLE);
			}
            Log.i(ConnectionService.LOG_TAG, "video process finished");
            closeIPWebcamConnection();
        }
        catch (Exception ex) {
            Log.i(ConnectionService.LOG_TAG, "exception", ex);
        }
	}

}