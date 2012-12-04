package org.dyndns.fzoli.rccar.host.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

import org.apache.commons.ssl.Base64;
import org.dyndns.fzoli.rccar.host.Config;
import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.rccar.host.R;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

import android.content.Context;
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
	 * Leállítja az IP Webcam alkalmazást.
	 */
	private void stopIPWebcamActivity() {
		stopIPWebcamActivity(SERVICE);
	}
	
	/**
	 * Leállítja az IP Webcam alkalmazást.
	 * @param context Activity vagy Service
	 */
	public static void stopIPWebcamActivity(Context context) {
		context.sendBroadcast(new Intent("com.pas.webcam.CONTROL").putExtra("action", "stop"));
	}
	
	/**
	 * Kapcsolódik az IP Webcam alkalmazás MJPEG folyamához.
	 * Tíz alkalommal kísérli meg a kapcsolódást 2 másodperc szünetet tartva a két próbálkozás között.
	 * Ha a kapcsolódás nem sikerül, kiküldi az alkalmazást elindító utasítást az Androidnak.
	 * Az alkalmazást a felhasználó beállítása alapján indítja el és ez alapján kapcsolódik a helyi szerverhez.
	 * Ha sikerült a kapcsolódás, de olvasni nem lehet a folyamból, újraindítja az IP Webcam alkalmazást,
	 * hogy frissüljenek a konfigurációs beállításai, mert valószínűleg azzal van a gond.
	 * Ha a harmadik kapcsolódás sem sikerül, szintén újraindítja az IP Webcam alkalmazást.
	 * @return true, ha sikerült a kapcsolódás és a folyam olvasása, egyébként false
	 */
	private boolean openIPWebcamConnection() {
		Config conf = SERVICE.getConfig();
		String port = conf.getCameraStreamPort(); // szerver port
		String user = conf.getCameraStreamUser(); // felhasználónév
		String password = conf.getCameraStreamPassword(); // jelszó
		
		String httpUrl = "http://127.0.0.1:" + port + "/videofeed"; // a szerver pontos címe
		String authProp = "Basic " + Base64.encodeBase64String(new String(user + ':' + password).getBytes()); // a HTTP felhasználóazonosítás Base64 alapú
		
		boolean stopped = false; // kezdetben még nem lett leállítva az IP Webcam
		for (int i = 1; i <= 10; i++) { // 10 próbálkozás a kapcsolat létrehozására
			try {
				conn = (HttpURLConnection) new URL(httpUrl).openConnection(); //kapcsolat objektum létrehozása
				conn.setRequestMethod("GET"); // GET metódus beállítása
				if (user != null && !user.equals("")) conn.setRequestProperty("Authorization", authProp); // ha van azonosítás, adat beállítása
				conn.connect(); // kapcsolódás
				if (i != 0) { // ellenőrzés csak akkor, ha a ciklusváltozó értéke nem 0
					conn.getInputStream().read(new byte[5120]); // olvashatóság tesztelése
					closeIPWebcamConnection(false); // sikeres teszt, kapcsolat lezárása, de program futva hagyása
					i = -1; // a következő ciklus futáskor a változó értéke 0 lesz
					continue; // következő ciklusra lépés
				}
				return true;
			}
			catch (ConnectException ex) { // ha a kapcsolódás nem sikerült
				Log.i(ConnectionService.LOG_TAG, "ip webcam error", ex);
				if (i == 5 && !stopped) { // az 5. próbálkozásra leállítás, ha még nem volt
					i = 0; // újra 10 próbálkozás
					stopped = true; // több leállítás nem kell
					stopIPWebcamActivity(); // ha a harmadik kapcsolódás sem sikerült, alkalmazás leállítása
				}
				startIPWebcamActivity(port, user, password); // IP Webcam program indítása, hátha még nem fut
				sleep(); // 2 másodperc várakozás a program töltésére
			}
			catch (SocketException ex) { // valószínűleg eltérő konfigurációval fut az IP Webcam vagy éppen újraindul
				Log.i(ConnectionService.LOG_TAG, "ip webcam error", ex);
				if (!stopped) { // leállítás, ha még nem volt
					i = 0; // újra 10 próbálkozás
					stopped = true; // több leállítás nem kell
					stopIPWebcamActivity(); // az alkalmazás leállítása
				}
				startIPWebcamActivity(port, user, password); // majd újra elindítása
				sleep(); // 2 másodperc várakozás a program töltésére
			}
			catch (Exception ex) { // egyéb ismeretlen hiba
				Log.i(ConnectionService.LOG_TAG, "ip webcam error", ex);
				sleep(); // 2 másodperc várakozás a program töltésére
			}
		}
		
		return false;
	}

	/**
	 * Két másodperc szünetet tart a szál.
	 */
	private void sleep() {
		try {
			Thread.sleep(2000); // 2 másodperc várakozás
		}
		catch (Exception e) {
			;
		}
	}
	
	/**
	 * Lezárja a kapcsolatot az IP Webcam alkalmazás szerverével és leállítja az IP Webcam programot.
	 * @param stop true esetén az IP Webcam program leáll, egyébként csak a kapcsolat zárul le
	 */
	private void closeIPWebcamConnection(boolean stop) {
		if (conn != null) conn.disconnect();
		if (stop) stopIPWebcamActivity();
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
					
					// TODO: elindul a stream olvasása rendesen, de rá nem sokkal írás hiba történik, amit már nem lehet megoldani újrakapcsolódás nélkül, ezért kell egy olyan funkció, ami egyetlen processt újrapéldányosít.
					// fontos még az, hogy ebben az esetben az MJPEG streamelő alkalmazás ne legyen bezárva, mert az első megnyitáskor történik ez a hiba állandóan.
					// A legjobb az lenne, ha a hibát lehetne elkerülni. Valami olyasmi gond lehet, hogy hibás adat érkezik az IP Webcamtól ami kiírásra kerül és hazavágja az SSL kapcsolatot.
					// Esetleg a kapcsolódás nyitás után kiolvasni egy kevés adatot és ha sikerült, akkor biztosan lehet rá kapcsolódni, így lezárni az aktuális kapcsolatot és újat nyitni, amit már biztonságosan lehet küldeni a hídnak.
					
					Log.i(ConnectionService.LOG_TAG, "wth?", ex);
					if (!getSocket().isClosed()) {
						closeIPWebcamConnection(false);
						getHandler().closeProcesses();
						return; // ideiglenes megoldásként újrakapcsolódást idézek elő és hagyom futni az IP Webcam alkalmazást
					}
				}
			}
			else {
				SERVICE.onConnectionError(ConnectionError.WEB_IPCAM_UNREACHABLE);
			}
			Log.i(ConnectionService.LOG_TAG, "video process finished");
			closeIPWebcamConnection(false); // TODO: az alkalmazás leállásával a home képernyő jön elő, ezért egyelőre az alkalmazás nem zárul be, csak a kapcsolat bontódik
		}
		catch (Exception ex) {
			Log.i(ConnectionService.LOG_TAG, "not important exception", ex);
		}
	}

}
