package org.dyndns.fzoli.rccar.host;

import ioio.lib.util.android.IOIOService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dyndns.fzoli.rccar.host.socket.ConnectionHelper;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicle;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicles;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import com.ramdroid.adbtoggle.accesslib.AdbToggleAccess;

/**
 * Az alkalmazás háttérben futó szolgáltatása.
 * A hídhoz való hálózati kapcsolódást és a jármű mikrovezérlőjéhez való kapcsolódás a fő feladatköre.
 * A felhasználót értesíti minden fontos eseményről (warning, error). Arról is, hogy a szolgáltatás fut.
 * @author zoli
 */
public class ConnectionService extends IOIOService {
	
	/**
	 * A fatális hibák (röviden hibák) beállításukkor bontják a kapcsolatot a híddal és nem távolíthatóak el a notification barról. A hibák a service leállásakor mind eltűnnek. A hibákra kattintva a főablak jelenik meg.
	 * A figyelmeztetések (nem hibák) beállításukkor reconnect ütemezést aktiválnak és a service leállásakor vagy sikeres kapcsolódás esetén mind eltűnnek. A figyelmeztetések eltávolíthatóak és rájuk kattintva azonnal eltűnnek és lefuttatják a reconnect metódust.
	 * A figyelmeztetések közül egyszerre csak egy látható.
	 * Az other figyelmeztetés ismeretlen hibát jelöl.
	 * A sikeres kapcsolatfelvételt a null referencia jelzi.
	 */
	public static enum ConnectionError {
		OTHER,
		CONNECTION_ERROR,
		CONNECTION_LOST,
		CONNECTION_REFUSED,
		INVALID_CERTIFICATE,
		WEB_IPCAM_CLOSED,
		WEB_IPCAM_UNREACHABLE(true),
		WRONG_CLIENT_VERSION(true),
		WRONG_CERTIFICATE_SETTINGS(true),
		WRONG_CERTIFICATE_PASSWORD(true);
		
		/**
		 * Megadja, hogy fatális hibáról van-e szó.
		 */
		private final boolean FATAL_ERROR;
		
		/**
		 * A felsorolás konstruktora.
		 * Alapértelmezésként nem fatális hibáról van szó.
		 */
		private ConnectionError() {
			this(false);
		}
		
		/**
		 * A felsorolás konstruktora.
		 * @param error true esetén fatális hiba
		 */
		private ConnectionError(boolean error) {
			FATAL_ERROR = error;
		}
		
		/**
		 * A hibához kapcsolódó figyelmeztetés azonosítóját adja meg.
		 * Generált érték a felsorolás index alapján.
		 * Pl. 101, 102 ... 107
		 */
		public int getNotificationId() {
			return 100 + ordinal();
		}
		
		/**
		 * Megadja, hogy a hibához tartozik-e felületi figyelmeztetés.
		 * @return true esetén látható, tehát tartozik hozzá figyelmeztetés
		 */
		public boolean isVisible() {
			return this != OTHER;
		}
		
		/**
		 * Megadja, hogy a hiba fatális-e.
		 * Fatális hiba esetén nem ismétlődik meg a kapcsolódás.
		 * @return true esetén fatális a hiba, egyébként nem
		 */
		public boolean isFatalError() {
			return FATAL_ERROR;
		}
		
	}
	
	/**
	 * Segédváltozó ahhoz, hogy a {@code R.string} osztály Integer változóihoz hozzá lehessen férni úgy,
	 * hogy két változó segítégével (String + Integer) lehessen megkapni a referenciát.
	 * Pl. "err_" + key , ahol key egy változó érték, de a String konstans.
	 */
	private final static R.string STRINGS = new R.string();
	
	/**
	 * Konstans azonosítók azokhoz a figyelmeztetésekhez, amik nem a kapcsolódás figyelmeztetései.
	 */
	private final static int ID_FOREGROUND = 0,
						ID_NOTIFY = 1,
						ID_NOTIFY_CONFIG = 2,
						ID_NOTIFY_NETWORK = 3,
						ID_NOTIFY_INST_CAM = 4,
						ID_NOTIFY_GPS_ENABLE = 5,
						ID_NOTIFY_ADB_ENABLE = 6,
						ID_NOTIFY_SET_CONFIG = 7;
	
	/**
	 * A SharedPreference azon kulcsai, melyek kódban több helyen is használatban vannak.
	 * Tipikus eset amikor getterben és setter metódus is írva van egy paraméterhez.
	 */
	private final static String KEY_STARTED = "started";
	
	/**
	 * Az alkalmazásom az IP Webcam alkalmazásra támaszkodik.
	 * Az IP Webcam alkalmazás ingyenes és a Play áruházból letölthető és
	 * ha nincs még telepítve, figyelmeztetés jelenik meg.
	 * Erre a figyelmeztetésre kattintva megnyílik a Play áruház és behozza az alkalmazást.
	 * Ahhoz, hogy az alkalmazást be lehessen hozni, a pontos csomag útvonalára van szükség.
	 * Szintén kell ahhoz, hogy a program meg tudja állapítani, hogy telepítve van-e az alkalmazás.
	 */
	public final static String PACKAGE_CAM = "com.pas.webcam";
	
	/**
	 * Az Android naplózójához használt teg.
	 */
	public final static String LOG_TAG = "mobilerc";
	
	/**
	 * A szolgáltatás az eseményeket ezzel a kulccsal várja.
	 */
	public final static String KEY_EVENT = "event", KEY_ORIG = "orig";
	
	/**
	 * A szolgáltatás ezeket az eseményeket fogadja és dolgozza fel.
	 * A kód Android 2.1-től egészen a 4-es verzióig működőképes, ezért már elavult eseményeket is használ.
	 */
	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public final static String EVT_RECONNECT_NOW = "reconnect now",
						EVT_POWER_DOWN = Intent.ACTION_POWER_DISCONNECTED,
						EVT_CONNECTIVITY_CHANGE = ConnectivityManager.CONNECTIVITY_ACTION,
						EVT_GPS_SENSOR_CHANGE = LocationManager.PROVIDERS_CHANGED_ACTION,
						EVT_SDCARD_MOUNTED = Intent.ACTION_MEDIA_MOUNTED,
						EVT_SHUTDOWN = Intent.ACTION_SHUTDOWN,
						EVT_APP_INSTALL = Intent.ACTION_PACKAGE_INSTALL,
						EVT_APP_ADDED = Intent.ACTION_PACKAGE_ADDED,
						EVT_SERVICE_DESTROY = "org.dyndns.fzoli.rccar.host.SERVICE_DESTROY";
	
	/**
	 * Összeköttetés az Activity és a Service között, amit a jármű mikrovezérlője is használ.
	 */
	private ConnectionBinder binder;
	
	/**
	 * A felhasználó által kiválasztott jármű vezérlője.
	 */
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();
	
	/**
	 * A hídhoz való kapcsolódáshoz szükséges beállítások.
	 */
	private Config config;
	
	/**
	 * A hídhoz való kapcsolódást megvalósító objektum.
	 */
	private static ConnectionHelper conn;
	
	/**
	 * Megadja, hogy a disconnect metódus lefutott-e.
	 */
	private boolean disconnected;
	
	/**
	 * Az Android kapcsolódáskezelője.
	 * Arra kell, hogy a szolgáltatás értesüljön arról, ha a hálózati kapcsolat kialakult vagy megszünt.
	 * Pl. megszakadt a WiFi jel, vagy a felhasználó kiválasztott egy hálózatot és sikerült rá kapcsolódni
	 * Addig, míg nincs hálózat, fölösleges próbálkozni kapcsolódással, úgy se fog menni.
	 */
	private ConnectivityManager cm;
	
	/**
	 * Az Android helyzetmeghatározója.
	 * GPS jel fogadására kell, valamint arra, hogy megtudja a szolgáltatás, hogy elérhető-e a GPS szenzor.
	 * A hídhoz való kapcsolódásnak nem feltétele a GPS jel megléte. Ez esetben ismeretlen lesz a pozíció.
	 */
	private LocationManager lm;
	
	/**
	 * ActivityManager objektum ahhoz, hogy meg lehessen állapítani, fut-e egy adott alkalmazás.
	 */
	private ActivityManager activityManager;
	
	/**
	 * Az újrakapcsolódás időzítésére fenntartott időzítő.
	 * Ha a kapcsolat megszakadt a híddal vagy nem sikerült kapcsolódni rá,
	 * újra megpróbálja a szolgáltatás a kapcsolódást későbbre időzítve.
	 */
	private static final Timer CONN_TIMER = new Timer();
	
	/**
	 * Az újrakapcsolódáshoz használt ütemezett feladat.
	 * Egy kapcsolódáshoz egy példány használható, ezért mindig az aktuális példányt tartalmazza a változó.
	 */
	private static TimerTask connTask;
	
	/**
	 * Az Android rendszerikon kezelője.
	 * Arra kell, hogy figyelmeztetést tudjon megjeleníteni a szolgáltatás a felhasználónak.
	 */
	private NotificationManager nm;
	
	/**
	 * A fő rendszerikon.
	 * Akkor látható, amikor a szolgáltatás fut.
	 */
	private Notification notification;
	
	/**
	 * A fő rendszerikon értesítő szövege.
	 * Jelzi, hogy ki van-e alakítva a kapcsolat a járművel és a híddal.
	 * Ha rákattintanak a rendszerikonra, felhozza az alkalmazás főablakát.
	 */
	private PendingIntent contentIntent;
	
	/**
	 * Dudahangot játszik le.
	 */
	private MediaPlayer mpHorn;
	
	/**
	 * A szolgáltatást felfüggesztése illetve aktiválása.
	 * Ha a szolgáltatás felfüggesztett, úgy viselkedik, mint ha nem futna.
	 * Erre azért van szükség, mert attól, hogy a szolgáltatás még le van állítva,
	 * az általa indított szálak még aktívak és bármikor jöhet olyan esemény, amely
	 * nem várt hatást eredményez. Pl. híd disconnect esetén figyelmeztetés és újrakapcsolódás
	 * Kezdetben a szolgáltatás aktív, tehát végzi a dolgát első indulástól kezdve.
	 */
	private static boolean suspended = false;
	
	/**
	 * Ha végzetes hiba történik a kapcsolódás közben, a változó értéke true.
	 * A szolgáltatás leállásával vagy újraindulásával a változó újra false értéket vesz fel.
	 */
	private static boolean fatal = false;
	
	/**
	 * Ha az Android rendszer leállítás alá kerül, akkor true az értéke.
	 */
	private static boolean shutdown = false;
	
	/**
	 * Megadja, hogy a kapcsolódás folyamatban van-e éppen.
	 */
	private boolean connecting = false;
	
	/**
	 * Megadja, hogy a szolgáltatás fel van-e függesztve.
	 * A szolgáltatás mindkenképpen fel van függesztve, ha az Android rendszer leállítás alá kerül
	 * ezzel elkerülve azt, hogy az Activity a felfüggesztést törölje a rendszer leállásakor, amikor
	 * a kapcsolódás állapota hamisra változik és eltűnik a dialógus.
	 */
	public static boolean isSuspended() {
		Log.i(LOG_TAG, "suspended: " + (shutdown || suspended));
		return shutdown || suspended;
	}
	
	/**
	 * Beállítja a szolgáltatást aktívra vagy felfüggesztettre.
	 * A felfüggesztés két helyen van használva:
	 * 1. amikor az Android rendszer leáll, le kell állítani a szolgáltatást, de az eseménykezelő visszahívására (hogy megszakadt a kapcsolat) már nem kell reagálni
	 * 2. amikor a szolgáltatás fut és a beállítás Activityt meghívja a felhasználó, a szolgáltatást le kell állítani, de a kapcsolat megszakadásra nem kell reagálni
	 */
	public static void setSuspended(boolean suspended) {
		ConnectionService.suspended = suspended;
	}
	
	/**
	 * Megadja, hogy történt-e végzetes hiba.
	 * Az Activity előtérbe kerülésekor megnézi, hogy a szolgáltatásban történt-e végzetes hiba.
	 * Ha történt, akkor leállítja a szolgáltatás futását, ezzel eltűnnek a hibajelzések is és kézzel indítható a szolgáltatás újra.
	 */
	public static boolean isFatal() {
		return fatal;
	}
	
	/**
	 * Beállítja, hogy van-e fatális hiba.
	 * A szolgáltatás leállásakor és indulásakor az érték hamisra áll be, fatális hiba keletkezés esetén igazra.
	 */
	private static void setFatal(boolean fatal) {
		ConnectionService.fatal = fatal;
	}
	
	/**
	 * A kapcsolódáshoz szükséges konfiguráció.
	 */
	public Config getConfig() {
		return getConfig(false);
	}
	
	/**
	 * A kapcsolódáshoz szükséges konfiguráció.
	 * @param create true esetén legyártja az objektumot, ha még nem létezik
	 */
	private Config getConfig(boolean create) {
		if (create && config == null) config = createConfig(this); // konfiguráció betöltése
		return config;
	}
	
	/**
	 * A járművet vezérlő mikrovezérlő irányító.
	 * @return null, ha még nem lett példányosítva
	 */
	public Vehicle getVehicle() {
		for (Vehicle vehicle : vehicles) {
			if (vehicle.isConnected()) return vehicle;
		}
		return vehicles.get(0);
	}
	
	/**
	 * A felületet a szolgáltatással és a mikrovezérlővel összekötő binder objektum.
	 */
	public ConnectionBinder getBinder() {
		if (binder == null) binder = new ConnectionBinder(this);
		return binder;
	}
	
	/**
	 * Amikor az Activity kapcsolódik a szolgáltatáshoz, binder referenciát kell visszaadni.
	 * Az alkalmazásnak elég egyetlen binder objektum, ezért mindig ugyan azt adja vissza.
	 */
	@Override
	public ConnectionBinder onBind(Intent intent) {
		return getBinder();
	}
	
	/**
	 * Amikor a szolgáltatás létrejön, létre kell hozni a mikrovezérlőt irányító objektumot is.
	 * A metódus a felhasználó beállítása alapján példányosítja az egyik járművezérlőt.
	 * További információ: {@link Vehicles}
	 */
	@Override
	public Vehicle createIOIOLooper(String connectionType, Object extra) {
		Vehicle vehicle = Vehicles.createVehicle(this, Integer.parseInt(getSharedPreferences(this).getString("vehicle", "0")));
		vehicles.add(vehicle);
		return vehicle;
	}
	
	/**
	 * A hídhoz való kapcsolódás előtt példányosítani kell a kapcsolódást segítő osztályt.
	 * A példányosítás csak akkor történik meg, ha biztonságosan kivitelezhető a kapcsolódás és online módban van a szolgáltatás.
	 * A biztonságos kapcsolódás feltételei:
	 * - helyes konfiguráció (helytelen konfiguráció esetén értesítés jelenik meg, ha online módban van a szolgáltatás)
	 * - legyen elérhető hálózat (WiFi vagy mobilnet)
	 * - telepítve legyen a kameraképet MJPEG-ben streamelő program
	 * Ha a fenti feltételek egyike nem teljesül, akkor elmarad a példányosítás és nem lesz kapcsolódva a szolgáltatás a hídhoz.
	 */
	private ConnectionHelper createConnectionHelper() {
		Log.i(LOG_TAG, "create connection helper");
		if (isOfflineMode(this) || !getConfig(true).isCorrect() || !isNetworkAvailableOrForced() || !isAppInstalled(PACKAGE_CAM)) {
			if (isSDCardMounted() && !getConfig(true).isCorrect()) {
				setFatal(true);
				addNotification(R.string.set_config, new Intent(this, MainActivity.class), null, ID_NOTIFY_SET_CONFIG, false, true);
			}
			return null;
		}
		if (conn != null) conn.disconnect();
		return conn = new ConnectionHelper(this);
	}
		
	/**
	 * Időzíti az újrakapcsolódást.
	 * Akkor van rá szükség, ha nem sikerült kapcsolódni a hídhoz vagy megszakadt a kapcsolat.
	 * A folyamatos kapcsolat érdekében a szolgáltatás addig próbálkozik, míg nem sikerül a kapcsolódás.
	 * @param reason debug paraméter, amit bent hagytam, mert még jól jöhet
	 */
	private void reconnectSchedule(String reason) {
		reconnectSchedule(false, reason);
	}
	
	/**
	 * Újrakapcsolódás hívó.
	 * Ha az újrakapcsolódást a felhasználó kezdeményezte a figyelmeztető üzenetre kattintva,
	 * azonnali újrakapcsolódás indul meg, egyébként időzített újrakapcsolódás.
	 * Ha az időzített újrakapcsolódás már aktiválva van és azonnali újrakapcsolódást kér a felhasználó,
	 * az időzített folyamat fut le azonnal, de ha még nincs időzített, akkor egyszerűen meghívódik a reconnect.
	 * @param now true esetén azonnali újrakapcsolódás, egyébként újrakapcsolódás időzítés
	 * @param reason debug paraméter, amit bent hagytam, mert még jól jöhet
	 */
	private void reconnectSchedule(boolean now, final String reason) {
		if (now) {
			if (connTask != null) {
				connTask.cancel();
				connTask = null;
				CONN_TIMER.purge();
			}
			connect(true, reason);
		}
		else if (connTask == null) {
			final ConnectionHelper helper = conn;
			connTask = new TimerTask() {
				
				@Override
				public void run() {
					connect(true, helper, reason);
					connTask = null;
				}
				
			};
			CONN_TIMER.schedule(connTask, getReconnectDelay());
		}
	}
	
	/**
	 * Újrakapcsolódás a hídhoz csak akkor, ha még nincs kapcsolat.
	 * Ha a híd nincs még kapcsolódva a hídhoz, meghívja a kapcsolódást úgy,
	 * hogy a jelenlegi kapcsolatokat lezárja és kapcsolódjon újra.
	 * @param reason debug paraméter, amit bent hagytam, mert még jól jöhet
	 */
	private synchronized void safeReconnect(String reason) {
		Log.i(LOG_TAG, "safe reconnect ASKED; reason: " + reason);
		if (!isBridgeConnected() && !isBridgeConnecting()) connect(true, reason);
	}
	
	/**
	 * Kapcsolódás a hídhoz.
	 * A kapcsolódás előtt új kapcsolódás segítő objektum jön létre,
	 * és csak akkor hívódik meg a kapcsolódás, ha a segítő létrejött.
	 * Ha nem kértek újrakapcsolódást, csak akkor fut le, ha még nincs kapcsolódás segítő.
	 * Ha a szolgáltatás felfüggesztett vagy nincs elindítva, biztos, hogy nem kapcsolódik.
	 * @param reconnect true esetén bontja a jelenlegi kapcsolatot és újra kapcsolódik
	 * @param helper a metódus csak akkor fog újrakapcsolódást kezdeni, ha a jelenlegi kapcsolódás segítő kéri
	 * @param reason debug paraméter, amit bent hagytam, mert még jól jöhet
	 */
	private void connect(boolean reconnect, ConnectionHelper helper, String reason) {
		Log.i(LOG_TAG, "connect calling; reconnect: " + reconnect+"; reason: " + reason);
		if ((isStarted(this) && !isSuspended()) && conn == null || (reconnect && conn == helper)) { // ha nincs kapcsolódás segítő vagy újra kell kapcsolódni
			disconnect(reason); // jelenlegi kapcsolatok bontása, ha esetleg vannak
			if ((isStarted(this) && !isSuspended()) && createConnectionHelper() != null) { // kapcsolódás csak akkor, ha aktív a szolgáltatás
				disconnected = false;
				removeWarnings(false);
				conn.connect();
			}
			else {
				Log.i(LOG_TAG, "connect not called");
			}
		}
	}
	
	/**
	 * @see #connect(boolean, String, ConnectionHelper)
	 */
	private void connect(boolean reconnect, String reason) {
		connect(reconnect, conn, reason);
	}
	
	/**
	 * Kapcsolat bontása a híddal.
	 * Az Activitynek jelzi azt, hogy nincs kapcsolódás.
	 * @param stopReconnect true esetén leállítja az időzített újrakapcsolódást is
	 */
	private void disconnect(String reason) {
		disconnect(reason, true);
	}
	
	/**
	 * Kapcsolat bontása a híddal.
	 * Az Activitynek jelzi azt, hogy nincs kapcsolódás.
	 * @param stopReconnect true esetén leállítja az időzített újrakapcsolódást is
	 * @param notice true esetén a disconnected logikai érték true-ra állítódik, ami egy jelzés
	 */
	private void disconnect(String reason, boolean notice) {
		Log.i(LOG_TAG, "disconnect calling; reason: " + reason);
		if (notice) disconnected = true;
		if (connTask != null) { // újrakapcsolódás időzítő inaktiválása
			connTask.cancel();
			connTask = null;
			CONN_TIMER.purge();
		}
		if (conn != null) { // lekapcsolódás a hídról
			conn.disconnect();
			conn = null;
		}
		getBinder().fireConnectionStateChange(false); // jelzés az felületnek
	}
	
	/**
	 * A dudahang-lejátszó inicializálása.
	 */
	private void createHornPlayer() {
		if (isOfflineMode(this)) return;
		try {
			if (mpHorn == null) mpHorn = MediaPlayer.create(this, R.raw.horn);
			else mpHorn.prepare();
		}
		catch (Exception ex) {
			;
		}
	}
	
	/**
	 * A dudahang-lejátszó felszabadítása.
	 */
	private void releaseHornPlayer() {
		if (mpHorn != null) mpHorn.release();
	}
	
	/**
	 * A szolgáltatás indulásakor hívódik meg.
	 * Első induláskor megjelenik az értesítés, hogy a szolgáltatás fut,
	 * meghívódik a kapcsolódó metódus, frissül az értesítőszöveg és
	 * végül a felmerülő egyéb problémák megjelennek. Pl. GPS engedélyezés
	 * A metódus akkor is meghívódik, ha a szolgáltatást az Android rendszer leállította,
	 * mert pl. memóriahiánya volt és van újra elég memória, hogy újra fusson.
	 * Ezért arra is fel van készítve, hogy lehetnek előzetes beállítások.
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (startId == 1) {
			setFatal(false); // ha esetleg még nem törlődött volna a fatális hiba státusz, törlés
			setSuspended(false); // a szolgáltatás aktív (ha esetleg még nem lenne az)
			initNotification(); // fő értesítés inicializálása
			connect(true, "onstart"); // kapcsolódás, ha kell újrakapcsolódás
			updateNotificationText(); // fő értesítés szövegének frissítése
			setNotificationsVisible(true); // egyéb értesítések megjelenítése, ha van mit
			createHornPlayer(); // dudahang-lejátszó inicializálása
			setAdbEnabled(true); // ADB bekapcsolása, ha szükséges
		}
	}
	
	/**
	 * A szolgáltatás elindításának hívásával üzenetet is lehet küldeni neki, nem csak elindítani.
	 * A szolgáltatáshoz tartozik egy rendszerüzenet figyelő, ami átadja az üzeneteket a szolgáltatásnak.
	 * Ha a felhasználó egy kapcsolathiba figyelmeztetésre kattint,
	 * akkor is a szolgáltatás indul el reconnect üzenettel.
	 * További infó: {@link ConnectionIntentReceiver}
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (intent.hasExtra(KEY_EVENT) && !isSuspended() && (getConfig(true).isCorrect() || ConnectionService.isOfflineMode(this))) { // ha van esemény üzenet és nincs felfüggesztve a szolgáltatás (és futhat)
			String event = intent.getStringExtra(KEY_EVENT); // esemény megszerzése
			if (event.equals(EVT_CONNECTIVITY_CHANGE)) { // ha a hálózati kapcsolat módosult
				if (startId != 1) setNetworkNotificationVisible(true); // ha nem első indítás, felhasználó figyelmeztetés frissítése
				if (!isConnectionForced(this)) { // ha nincs kényszerítve a kapcsolódás ...
					if (isNetworkAvailable()) safeReconnect("network available"); // és van hálózat, újrakapcsolódás azonnal
					else disconnect("network unavailable"); // egyébként kapcsolat bontása és időzítés törlése
				}
			}
			else if (event.equals(EVT_GPS_SENSOR_CHANGE)) { // ha a GPS elérhetősége változott
				if (startId != 1) {
					setGpsEnableNotificationVisible(true); // figyelmeztetés módosítása, ha nem első indítás
					if (isGpsEnabled()) getBinder().initSensorThread();
				}
			}
			else if (event.equals(EVT_POWER_DOWN)) { // ha az IOIO le lett választva,
				// az ADB Toggle automatikus módban kikapcsolhatja az ADB-t, ezért vissza kell kapcsolni,
				// hogy a legközelebbi csatlakozáskor legyen újra kapcsolat az IOIO-val
				if (startId != 1) setAdbEnabled(true);
			}
			else if (event.equals(EVT_APP_ADDED) || event.equals(EVT_APP_INSTALL)) { // ha az alkalmazás települt
				if (startId != 1) setCamInstallNotificationVisible(true); // figyelmeztetés frissítése, ha nem első indítás
				connect(false, "app installed"); // és újrakapcsolódás ha még nincs kapcsolódva
			}
			else if (event.equals(EVT_SDCARD_MOUNTED)) { // ha az sd kártya elérhetővé vált, olvasható a konfiguráció
				if (startId != 1) setConfigNotificationVisible(true); // figyelmeztetés frissítése, ha nem első indítás
				connect(false, "sdcard mounted"); // újrakapcsolódás, ha még nincs kapcsolat
			}
			else if (event.equals(EVT_RECONNECT_NOW)) { // azonnali újrakapcsolódás kérésre
				reconnectSchedule(true, "reconnect now request"); // azt teszi, amire kérik...
			}
			else if (event.equals(EVT_SHUTDOWN)) { // a rendszer leállítása esetén
				shutdown = true; // jelzés, hogy a rendszer leállítás alatt van
				stopSelf(); // leállás, mely az onDestroy metódust is meghívja
			}
			else if (event.equals(Intent.ACTION_MEDIA_SHARED) || event.equals(Intent.ACTION_MEDIA_BAD_REMOVAL) || event.equals(Intent.ACTION_MEDIA_EJECT) || event.equals(Intent.ACTION_MEDIA_REMOVED) || event.equals(Intent.ACTION_MEDIA_UNMOUNTED)) { // ha a háttértár nem érhető el
				if (startId != 1) setConfigNotificationVisible(true); // jelzés, hogy szükség van a háttértárra
			}
		}
		return START_STICKY; // ha a szolgáltatást bezárják (pl. kevés RAM), a rendszer újraindítja, ha tudja
	}
	
	/**
	 * A szolgáltatás leállítása előtt meghívódó metódus.
	 * - Ha a szolgáltatás leállás nem várt (pl. memóriahiány miatt az OS leállítja),
	 *   üzenet broadcastolása, hogy a szolgáltatás leáll és újra el kell indítani
	 * - A mikrovezérlővel megállítja a kommunikációt, hogy legközelebbi induláskor frissüljön
	 * - Felfüggeszti a szolgáltatást és törli a fatális hiba státuszt.
	 * - Lekapcsolódik a hídról, ha kell.
	 * - Az összes figyelmeztetést eltávolítja. (fő, kapcsolódás és egyéb)
	 */
	@Override
	public void onDestroy() {
		if (!isSuspended() && isStarted(this)) sendBroadcast(new Intent(EVT_SERVICE_DESTROY));
		stop();
		setAdbEnabled(false);
		setFatal(false);
		setSuspended(true);
		disconnect("on destroy");
		setNotificationsVisible(false);
		setConnectionError(null, null, true);
		removeNotification();
		releaseHornPlayer();
		super.onDestroy();
	}
	
	/**
	 * Ha a jármű mikrovezérlőjét csatlakoztatták vagy eltávolították.
	 * - A figyelmeztető üzenet frissítése.
	 * - A Híd szerver és Activity figyelmeztetése.
	 */
	public void onVehicleConnectionStateChanged() {
		updateNotificationText();
		getBinder().fireVehicleConnectionStateChanged(isVehicleConnected());
	}
	
	/**
	 * Ha a kapcsolódás megkezdődik vagy befejeződik, felirat frissítése.
	 */
	public void onBridgeConnectionStateChanged(boolean connecting) {
		this.connecting = connecting;
		updateNotificationText();
	}
	
	/**
	 * Példányosítja a figyelmeztetés megjelenítéséhez szükséges menedzsert és megjeleníti a szolgáltatás fő figyelmeztetést folyamatként.
	 */
	@SuppressWarnings("deprecation")
	private void initNotification() {
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ic_main, getString(R.string.app_name), System.currentTimeMillis());
		contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags |= Notification.FLAG_ONGOING_EVENT; // folyamatként jelenik meg
		startForeground(ID_FOREGROUND, notification); // a service futtatása biztonságban a háttérben (kevés memória esetén nem zárja be az OS)
	}
	
	/**
	 * A szolgáltatás fő figyelmeztetésének a szövegét állítja be a paraméterben megadottra.
	 * Ha még nincs példányosítva a fő figyelmeztetés, a metódus nem tesz semmit.
	 * @param s a beállítandó szöveg
	 */
	@SuppressWarnings("deprecation")
	private void setNotificationText(String s) {
		if (nm != null && notification != null && contentIntent != null) {
			notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), s, contentIntent);
			nm.notify(ID_NOTIFY, notification);
		}
	}
	
	/**
	 * Frissíti a fő figyelmeztetés szövegét az alapján, hogy a jármű mikrovezérlője és a híddal való kapcsolat ki van-e alakítva.
	 * Jelzi, hogy elérhető-e a jármű és a híd szerver kapcsolat aktív-e.
	 * Csak akkor módosul a szöveg, ha a szolgáltatás nincs felfüggesztve és fut.
	 */
	public void updateNotificationText() {
		if ((!isSuspended() || isFatal()) && isStarted(this)) setNotificationText(getString(R.string.vehicle) + ": " + getString(isVehicleConnected() ? R.string.exists : R.string.not_exists) + "; " + (isOfflineMode(this) ? getString(R.string.title_offline) : (getString(R.string.bridge_conn) + ": " + getString(!connecting ? (isBridgeConnected() ? R.string.exists : R.string.not_exists) : R.string.connecting))) + '.');
	}
	
	/**
	 * Lejátssza a dudahangot.
	 */
	public void playHorn() {
		if (mpHorn != null) {
			try {
				if (!mpHorn.isPlaying()) mpHorn.start();
			}
			catch (Exception ex) {
				;
			}
		}
	}
	
	/**
	 * Az ADB Toggle alkalmazáshoz biztosít hozzáférést.
	 * Segítségével be vagy kikapcsolható az USB hibakeresés.
	 */
	private static AdbToggleAccess adbToggle;
	
	/**
	 * Engedélyezi vagy tiltja az USB hibakeresést.
	 * @param enabled true esetén engedélyezés
	 */
	private void setAdbEnabled(boolean enabled) {
		if (!ConnectionService.isAdkAvailable() && AdbToggleAccess.isInstalled(this)) {
			boolean active = AdbToggleAccess.isEnabled(this);
			if (adbToggle == null) {
				adbToggle = new AdbToggleAccess();
			}
			if (enabled) {
				if (!active) adbToggle.enable(this, null);
			}
			else {
				if (active && !isAdbHeld(this)) adbToggle.disable(this, null);
			}
		}
	}
	
	/**
	 * A szolgáltatás fő figyelmeztetését távolítja el, és felszabadítja a változókat, hogy a GC memóriát szabadítson fel.
	 */
	private void removeNotification() {
		stopForeground(true);
		nm.cancel(ID_NOTIFY);
		nm = null;
		notification = null;
		contentIntent = null;
	}
	
	/**
	 * Csak akkor hívja meg a figylemeztetés megjelenítést, ha online módban van a szolgáltatás.
	 * Ezek az üzenetek nem távolíthatóak el és rájuk kattintva sem tünnek el, csak ha megszűnik a probléma.
	 * A figyelmeztető üzenet csak akkor jelenik meg, ha a szolgáltatás nincs felfüggesztve és el van indítva.
	 * @param resText az üzenet azonosítója a strings.xml fájl alapján
	 * @param intentActivity a nézet, mely meghívódik kattintásra
	 * @param key a figyelmeztető üzenet azonosítója, ami alapján el lehet távolítani később
	 * @param error hibaüzenet vagy figyelmeztetés ikon jelenjen meg
	 */
	private void addOnlineNotification(int resText, Intent intentActivity, int key, boolean error) {
		if (!isOfflineMode(this)) {
			addNotification(resText, intentActivity, key, false, error);
		}
	}
	
	/**
	 * Olyan figyelmeztető üzenetet jelenít meg, melyre kattintva Activity hívódik meg.
	 * A figyelmeztető üzenet csak akkor jelenik meg, ha a szolgáltatás nincs felfüggesztve és el van indítva.
	 * @param resText az üzenet azonosítója a strings.xml fájl alapján
	 * @param intentActivity a nézet, mely meghívódik kattintásra
	 * @param key a figyelmeztető üzenet azonosítója, ami alapján el lehet távolítani később
	 * @param removable a felhasználó legyen-e képes eltávolítani az üzenetet illetve rá kattintva tünjön-e el
	 * @param error hibaüzenet vagy figyelmeztetés ikon jelenjen meg
	 */
	private void addNotification(int resText, Intent intentActivity, int key, boolean removable, boolean error) {
		addNotification(resText, intentActivity, null, key, removable, error);
	}
	
	/**
	 * Megjelenít egy figyelmeztető üzenetet, ami hívhat szolgáltatást vagy nézetet is.
	 * A figyelmeztető üzenet csak akkor jelenik meg, ha a szolgáltatás nincs felfüggesztve és el van indítva.
	 * @param resText az üzenet azonosítója a strings.xml fájl alapján
	 * @param intentActivity a nézet, mely meghívódik kattintásra
	 * @param intentService a szolgáltatás, mely meghívódik kattintásra, ha nincs megadva nézet helyette
	 * @param key a figyelmeztető üzenet azonosítója, ami alapján el lehet távolítani később
	 * @param removable a felhasználó legyen-e képes eltávolítani az üzenetet illetve rá kattintva tünjön-e el
	 * @param error hibaüzenet vagy figyelmeztetés ikon jelenjen meg
	 */
	@SuppressWarnings("deprecation")
	private void addNotification(int resText, Intent intentActivity, Intent intentService, int key, boolean removable, boolean error) {
		removeNotification(key);
		if (isSuspended() || !isStarted(this)) return;
		Log.i(LOG_TAG, "add notification request: " + key);
		Notification notification = new Notification(error ? R.drawable.ic_error : R.drawable.ic_warning, getString(resText), System.currentTimeMillis());
		notification.flags |= removable ? Notification.FLAG_AUTO_CANCEL : Notification.FLAG_NO_CLEAR;
		PendingIntent contentIntent;
		int flag = removable ? PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_UPDATE_CURRENT;
		if (intentActivity != null) contentIntent = PendingIntent.getActivity(this, 0, intentActivity, removable ? PendingIntent.FLAG_ONE_SHOT : flag);
		else contentIntent = PendingIntent.getService(this, 0, intentService, flag);
		notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(resText), contentIntent);
		nm.notify(key, notification);
	}
	
	/**
	 * Ha a kapcsolatban bármi hiba történik, vagy a hiba megszűnik, ez a metódus hívódik meg.
	 * A hiba alapján cselekedik a metódus. További részletek: {@link ConnectionError}
	 * @param error a kapcsolat hibája vagy null, ha megszüntek a hibák
	 */
	public void onConnectionError(ConnectionError error, ConnectionHelper caller) {
		setConnectionError(error, caller, false);
	}
	
	/**
	 * A ConnectionError-os figyelmeztetéseket tünteti el.
	 * @param all true esetén a fatális hibákat is törli, nem csak a figyelmeztetéseket
	 */
	private void removeWarnings(boolean all) {
		for (ConnectionError err : ConnectionError.values()) {
			if (!err.isFatalError() || all) removeNotification(err.getNotificationId());
		}
	}
	
	/**
	 * Ha a kapcsolatban bármi hiba történik, vagy a hiba megszűnik, ez a metódus hívódik meg.
	 * A hiba alapján cselekedik a metódus. További részletek: {@link ConnectionError}
	 * @param error a kapcsolat hibája vagy null, ha megszüntek a hibák
	 * @param removeAll ha minden figyelmeztetést el kell távolítani, true
	 */
	private void setConnectionError(ConnectionError error, ConnectionHelper caller, boolean removeAll) {
		if (error != null && caller != null && conn != null && caller != conn) {
			Log.i(LOG_TAG, "old warning has been dropped");
			return;
		}
		boolean change = true;
		if (removeAll) {
			Log.i(LOG_TAG, "connection msg remove");
			// service leáll, figyelmeztetések és hibák eltüntetése
			removeWarnings(true);
		}
		else if (isStarted(this) && !isSuspended()) {
			if (error == null || !error.isFatalError()) {
				Log.i(LOG_TAG, "error: " + error + "; disconnected? " + disconnected);
				change = error == null || (error.isVisible() && !disconnected);
				if (change) {
					// sikeres kapcsolódás vagy figyelmeztető üzenet, figyelmeztetések eltüntetése
					Log.i(LOG_TAG, "connection warn remove");
					removeWarnings(false);
				}
			}
			if (error != null) {
				Integer id = null;
				if (error.isVisible()) {
					try {
						id = (Integer) R.string.class.getField("err_" + error.ordinal()).get(STRINGS);
					}
					catch (Exception ex) {
						Log.i(LOG_TAG, "no text");
					}
				}
				if (error.isFatalError()) {
					Log.i(LOG_TAG, "add error notify " + error);
					if (id != null) addNotification(id, new Intent(this, MainActivity.class), error.getNotificationId(), false, true);
					setFatal(true);
					setSuspended(true); // szolgáltatás felfüggesztése, hogy a kapcsolódás bontása után lévő figyelmeztetés ne jelenjen meg
					disconnect("notify error"); // kapcsolat bontása, üzenet megjelenítése, amire kattintva a főablak jelenik meg
				}
				else {
					Log.i(LOG_TAG, "add warn notify " + error);
					if (id != null && change) addNotification(id, null, new Intent(this, ConnectionService.class).putExtra(KEY_EVENT, EVT_RECONNECT_NOW), error.getNotificationId(), true, false);
					// a kapcsolatok bezárása
					disconnect("notify warning", error != ConnectionError.OTHER);
					// reconnect ütemezés, üzenet megjelenítése eltávolíthatóként, amire kattintva azonnali reconnect fut le
					reconnectSchedule("notify warning");
				}
			}
		}
		else {
			disconnect("notify warning during stopped service");
		}
	}
	
	/**
	 * Az összes, a nem kapcsolódással kapcsolatos figylemeztetést megjeleníti, ha kell illetve elrejti kérésre.
	 * @param visible true esetén megjelennek a figyelmeztetések, melyeknek meg kell jelenni, egyébként mind eltűnik
	 */
	private void setNotificationsVisible(boolean visible) {
		setConfigNotificationVisible(visible);
		setNetworkNotificationVisible(visible);
		setGpsEnableNotificationVisible(visible);
		setCamInstallNotificationVisible(visible);
		setAdbEnableNotificationVisible(visible);
		if (!visible) {
			removeNotification(ID_NOTIFY_SET_CONFIG);
		}
	}
	
	/**
	 * Megjelenít egy figyelmeztetést, ha a konfiguráció nem hibás, de ha az SD-kártya nem érhető el, akkor előbb azt közli.
	 * @param visible true esetén megjeleníti a figyelmeztetést, de csak akkor, ha kell és false esetén eltünteti a figyelmeztetést
	 */
	private void setConfigNotificationVisible(boolean visible) {
		if (config == null) return;
		if (visible && !getConfig().isCorrect()) {
			if (isSDCardMounted()) addOnlineNotification(R.string.set_config, new Intent(this, MainActivity.class), ID_NOTIFY_CONFIG, true);
			else addOnlineNotification(R.string.sdcard_not_mounted, new Intent(Settings.ACTION_MEMORY_CARD_SETTINGS), ID_NOTIFY_CONFIG, false);
		}
		else {
			removeNotification(ID_NOTIFY_CONFIG);
		}
	}
	
	/**
	 * Megjelenít egy figyelmeztetést, ha egy hálózat sem érhető el.
	 * @param visible true esetén megjeleníti a figyelmeztetést, de csak akkor, ha kell és false esetén eltünteti a figyelmeztetést
	 */
	private void setNetworkNotificationVisible(boolean visible) {
		if (visible && !isNetworkAvailableOrForced() && !isNetworkConnecting()) addOnlineNotification(R.string.set_network, new Intent(Settings.ACTION_WIRELESS_SETTINGS), ID_NOTIFY_NETWORK, false);
		else removeNotification(ID_NOTIFY_NETWORK);
	}
	
	/**
	 * Megjelenít egy figyelmeztetést, ha az IP Webcam alkalmazás nincs telepítve.
	 * @param visible true esetén megjeleníti a figyelmeztetést, de csak akkor, ha kell és false esetén eltünteti a figyelmeztetést
	 */
	private void setCamInstallNotificationVisible(boolean visible) {
		if (visible && !isAppInstalled(PACKAGE_CAM)) addOnlineNotification(R.string.install_cam, new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + PACKAGE_CAM)), ID_NOTIFY_INST_CAM, true);
		else removeNotification(ID_NOTIFY_INST_CAM);
	}
	
	/**
	 * Megjelenít egy figyelmeztetést, ha a GPS szenzor nincs bekapcsolva.
	 * @param visible true esetén megjeleníti a figyelmeztetést, de csak akkor, ha kell és false esetén eltünteti a figyelmeztetést
	 */
	private void setGpsEnableNotificationVisible(boolean visible) {
		if (visible && !isGpsEnabled()) addOnlineNotification(R.string.set_gps, new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), ID_NOTIFY_GPS_ENABLE, false);
		else removeNotification(ID_NOTIFY_GPS_ENABLE);
	}
	
	/**
	 * Megjelenít egy figyelmeztetést, ha az ADB nincs bekapcsolva.
	 * @param visible true esetén megjeleníti a figyelmeztetést, de csak akkor, ha kell és false esetén eltünteti a figyelmeztetést
	 */
	private void setAdbEnableNotificationVisible(boolean visible) {
		if (visible && !isAdkAvailable() && !AdbToggleAccess.isInstalled(this) && !isAdbEnabled(this)) addNotification(R.string.set_adb, new Intent(Settings.ACTION_APPLICATION_SETTINGS), ID_NOTIFY_ADB_ENABLE, true, false);
		else removeNotification(ID_NOTIFY_ADB_ENABLE);
	}
	
	/**
	 * Eltávolítja a figyelmeztetést.
	 * @param key a figyelmeztetés azonosítója.
	 */
	private void removeNotification(int key) {
		if (nm == null) initNotification();
		nm.cancel(key);
	}
	
	/**
	 * Feszültséghatár lekérése.
	 * @param max true esetén maximum határ, false esetén minimum határ
	 */
	public float getVoltageLimit(boolean max) {
		return Float.parseFloat(getSharedPreferences(this).getString(max ? "max_voltage" : "min_voltage", max ? "4.0" : "2.0"));
	}
	
	/**
	 * A Híd oldalán a generált északtól való eltéréshez hozzáadódó értéket adja meg.
	 */
	public int getAdditionalDegree() {
		return Integer.parseInt(getSharedPreferences(this).getString("additional_degree", "0"));
	}
	
	/**
	 * Az újrakapcsolódás késleltetésének értékét adja meg.
	 */
	private int getReconnectDelay() {
		return Integer.parseInt(getSharedPreferences(this).getString("reconnect_delay", "20000"));
	}
	
	/**
	 * Megadja, hogy a járművet irányító mikrovezérlő kapcsolódva van-e a telefonhoz.
	 */
	public boolean isVehicleConnected() {
		return getVehicle().isConnected();
	}
	
	/**
	 * Megadja, hogy a híddal van-e teljesen kiépített kapcsolat.
	 */
	public boolean isBridgeConnected() {
		return conn != null && conn.isConnected();
	}
	
	/**
	 * Megadja, hogy a Hídhoz való kapcsolódás folyamatban van-e.
	 */
	public boolean isBridgeConnecting() {
		return conn != null && conn.isConnecting();
	}
	
	/**
	 * Megmondja, hogy a hálózat kapcsolódás alatt van-e.
	 */
	private boolean isNetworkConnecting() {
		return isNetworkState(NetworkInfo.State.CONNECTING);
	}
	
	/**
	 * Megmondja, hogy elérhető-e a hálózat, tehát van-e kialakítva kapcsolat.
	 */
	private boolean isNetworkAvailable() {
		return isNetworkState(NetworkInfo.State.CONNECTED);
	}
	
	/**
	 * Megmondja, hogy van-e elérhető hálózat, vagy kényszerített a kapcsolódás.
	 */
	private boolean isNetworkAvailableOrForced() {
		return isConnectionForced(this) || isNetworkAvailable();
	}
	
	/**
	 * Megmondja, hogy a hálózati állapot megegyezik-e a paraméterben átadottal.
	 * @param state a hálózati állapot, amivel az egyezést vizsgáljuk
	 * @return true, ha egyezik, egyébként false
	 */
	private boolean isNetworkState(NetworkInfo.State state) {
		final NetworkInfo activeNetwork = getActiveNetworkInfo();
		return activeNetwork != null && state != null && activeNetwork.getState() == state;
	}
	
	/**
	 * Az aktív hálózat információit adja vissza.
	 * @return null, ha nincs aktív hálózat, egyébként infó
	 */
	private NetworkInfo getActiveNetworkInfo() {
		try {
			if (cm == null) cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo();
		}
		catch (RuntimeException ex) {
			return null;
		}
	}
	
	/**
	 * Megmondja, hogy a GPS szenzor engedélyezve van-e.
	 */
	public boolean isGpsEnabled() {
		if (lm == null) lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
	/**
	 * Megadja, hogy egy adott alkalmazás fut-e.
	 * Ez általában akkor igaz, ha van legalább 1 Activity, ami fut előtérben vagy háttérben.
	 * @param pkg az alkalmazás csomagneve
	 */
	public boolean isAppRunning(String packageName) {
		if (packageName == null) return false;
		if (activityManager == null) activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);
		for (RunningTaskInfo task : tasks) {
			if (packageName.equalsIgnoreCase(task.baseActivity.getPackageName())) return true;
		}
		return false;
	}
	
	/**
	 * Megmondja, hogy az alkalmazás telepítve van-e a telefonra.
	 * @param packageName az alkalmazás csomagjának útvonala
	 */
	private boolean isAppInstalled(String packageName) {
	    PackageManager pm = getPackageManager();
	    boolean installed = false;
	    try {
	       pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
	       installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	       installed = false;
	    }
	    return installed;
	}
	
	/**
	 * Az Androidon tárolt beállítások megszerzése.
	 * Ha egyes beállítások még nincsenek megadva, az alapértelmezett értékek lesznek használva azokhoz.
	 * @param context a meghívó referenciája Pl. Service, Activity
	 */
	private static SharedPreferences getSharedPreferences(Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	/**
	 * A kapcsolódáshoz szükséges konfiguráció példányosítása.
	 * @param context a meghívó referenciája Pl. Service, Activity
	 */
	public static Config createConfig(Context context) {
		return new Config(getSharedPreferences(context));
	}
	
	/**
	 * Megadja, hogy az SD-kártya elérhető-e.
	 */
	public static boolean isSDCardMounted() {
		String state = Environment.getExternalStorageState();
	    return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
	}
	
	/**
	 * Megadja, hogy a jármű áramköréhez van-e elérhető csatorna.
	 */
	public static boolean isVehicleChannelAvailable(Context context) {
		boolean adbToggleInstalled = AdbToggleAccess.isInstalled(context);
		boolean adbEnabled = ConnectionService.isAdbEnabled(context);
		boolean adkAvailable = ConnectionService.isAdkAvailable();
		return !(!adkAvailable && !adbEnabled && !adbToggleInstalled);
	}
	
	/**
	 * Megadja, hogy az Adb engedélyezve van-e.
	 */
	@SuppressWarnings("deprecation")
	public static boolean isAdbEnabled(Context context) {
		try {
			return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1;
		}
		catch (Exception ex) {
			return false;
		}
	}
	
	/**
	 * Megadja, hogy a rendszer a verziószáma alapján támogathatja-e az ADK-t.
	 */
	public static boolean isAdkAvailable() {
		String androidVersion = Build.VERSION.RELEASE;
		String kernelVersion = System.getProperty("os.version");
		return chkVer(androidVersion, 2, 3, 4) && chkVer(kernelVersion, 2, 6, 35);
	}
	
    /**
     * A verziószám ellenőrzéshez felhasznált reguláris kifejezés.
     */
    private static final Pattern PT_VER = Pattern.compile("^(\\d)\\.(\\d)\\.?(\\d)?");

    /**
     * Verziószám ellenőrző.
     * @param s a vizsgált verziószám (pl. 2.0; 2.1; 2.3.3)
     * @param v a várt verziószám részekre bontva
     * @return true, ha a vizsgált verziószám megegyező vagy nagyobb a vártnál
     */
    private static boolean chkVer(String s, int... v) {
        if (s != null && v.length > 0) { // ha érvényesek a paraméterek
            Matcher m = PT_VER.matcher(s);
            if (m.find()) { // ha a verzió formátuma megfelelő
                // ha a 3. szám nem létezik, kettő, egyébként három szám van
                int cnt = m.group(3) == null ? 2 : 3;
                // ciklus, amíg van mit összehasonlítani
                for (int i = 1; i <= cnt && i - 1 < v.length; i++) {
                    int ver1 = v[i - 1]; // várt szám
                    int ver2 = Integer.parseInt(m.group(i)); // kapott szám
                    // ha a kapott szám nagyobb a vártnál, jó a verzió
                    if (ver2 > ver1) return true;
                    // ha a vizsgált verziószám hosszabb a kritériuménál
                    // vagy van még további szám, akkor az egyenlőség megengedve
                    boolean eq = cnt >= v.length || i < cnt;
                    // ha a kapott szám nem éri el a vártat, alacsony verzió
                    if (!(eq ? ver2 >= ver1 : ver2 > ver1)) return false;
                }
                // ha az összes szám átment a próbán, jó a verzió
                return true;
            }
        }
        return false; // érvénytelen paraméter
    }
	
    /**
	 * Megadja, hogy maradjin-e az ADB bekapcsolva
	 * @param context a meghívó referenciája Pl. Service, Activity
	 */
	private static boolean isAdbHeld(Context context) {
		return getSharedPreferences(context).getBoolean("keep_adb", false);
	}
    
	/**
	 * Megadja, hogy legyen-e ellenőrzötten streamelve az MJPEG-folyam.
	 * @param context a meghívó referenciája Pl. Service, Activity
	 */
	public static boolean isInspectedStream(Context context) {
		return getSharedPreferences(context).getBoolean("inspected_stream", false);
	}
	
	/**
	 * Megadja, hogy kényszerítve van-e a kapcsolódás a Hídhoz.
	 * Ha igen, akkor nem kell nézni, hogy van-e aktív hálózati kapcsolat, hanem meg kell kísérelni a kapcsolódást.
	 * @param context a meghívó referenciája Pl. Service, Activity
	 */
	public static boolean isConnectionForced(Context context) {
		return getSharedPreferences(context).getBoolean("force_connect", false);
	}
	
	/**
	 * Megadja, hogy a szolgáltatás offline módba van-e állítva.
	 * @param context a meghívó referenciája Pl. Service, Activity
	 */
	public static boolean isOfflineMode(Context context) {
		return getSharedPreferences(context).getBoolean("offline", false);
	}
	
	/**
	 * Megadja, hogy a szolgáltatás el van/volt-e indítva.
	 * @param context a meghívó referenciája Pl. Service, Activity
	 */
	public static boolean isStarted(Context context) {
		return getSharedPreferences(context).getBoolean(KEY_STARTED, false);
	}
	
	/**
	 * Beállítja a szolgáltatás futási állapotát.
	 * Ha a rendszer váratlanul újraindul, újra el kell indítani a szolgáltatást, ha el volt indítva.
	 * @param context a meghívó referenciája Pl. Service, Activity
	 * @param true esetén elindítva egyébként leállítva
	 */
	public static void setStarted(Context context, boolean b) {
		getSharedPreferences(context).edit().putBoolean(KEY_STARTED, b).commit();
	}
	
}
