package org.dyndns.fzoli.rccar.host;

//import java.io.InputStream;
//import java.net.ConnectException;
//import java.net.HttpURLConnection;
//import java.net.URL;

import ioio.lib.util.android.IOIOService;

//import org.apache.commons.ssl.Base64;
import org.dyndns.fzoli.rccar.host.socket.ConnectionHelper;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicle;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicles;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
//import android.util.Log;

public class ConnectionService extends IOIOService {
	
	private final static int ID_NOTIFY = 0;
	private final static int ID_NOTIFY_CONFIG = 1;
	private final static int ID_NOTIFY_NETWORK = 2;
	private final static int ID_NOTIFY_INST_CAM = 3;
	private final static int ID_NOTIFY_GPS_ENABLE = 4;
	
	private final static String KEY_STARTED = "started";
	
	private final static String PACKAGE_CAM = "com.pas.webcam";
	
	public final static String KEY_EVENT = "event";
	public final static String EVT_CONNECTIVITY_CHANGE = ConnectivityManager.CONNECTIVITY_ACTION;
	public final static String EVT_GPS_SENSOR_CHANGE = LocationManager.PROVIDERS_CHANGED_ACTION;
	
	@SuppressWarnings("deprecation")
	public final static String EVT_APP_INSTALL = Intent.ACTION_PACKAGE_INSTALL;
	public final static String EVT_APP_ADDED = Intent.ACTION_PACKAGE_ADDED;
	
	private final ConnectionBinder BINDER = new ConnectionBinder(this);
	
	private Vehicle vehicle;
	private Config config;
	private ConnectionHelper conn;
	
	private ConnectivityManager cm;
	private LocationManager lm;
	
	private NotificationManager nm;
	private Notification notification;
	private PendingIntent contentIntent;
	
	public Config getConfig() {
		return config;
	}
	
	public boolean isConnectionCreated() {
		return conn != null;
	}
	
	public ConnectionBinder getBinder() {
		return BINDER;
	}
	
	@Override
	public ConnectionBinder onBind(Intent intent) {
		return BINDER;
	}
	
	@Override
	public Vehicle createIOIOLooper() {
		return vehicle = Vehicles.createVehicle(BINDER, Integer.parseInt(getSharedPreferences(this).getString("vehicle", "0")));
	}
	
	private ConnectionHelper createConnectionHelper() {
		config = createConfig(this);
		if (isOfflineMode() || !config.isCorrect() || !isNetworkAvailable() || !isAppInstalled(PACKAGE_CAM)) {
			return null;
		}
		return conn = new ConnectionHelper(this);
	}
	
//	private void startIPWebcam() { //TODO: ez egyelőre csak teszt, nem itt a helye, hanem majd a kapcsolódáskor kell hasonlót futtatni
//		if (isAppInstalled(PACKAGE_CAM)) {
//			SharedPreferences pref = getSharedPreferences(this);
//			String port = pref.getString("cam_port", "8080");
//			String user = pref.getString("cam_user", "");
//			String password = pref.getString("cam_password", "");
//			Intent launcher = new Intent().setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME);
//			Intent ipwebcam = 
//				new Intent()
//				.setClassName("com.pas.webcam", "com.pas.webcam.Rolling")
//				.putExtra("cheats", new String[] {
//						"set(Awake,true)", // ébrenlét fenntartása
//						"set(Notification,true)", // rendszerikon ne legyen rejtve
//						"set(Audio,1)", // audio stream kikapcsolása
//						"set(Video,320,240)", // videó felbontás 320x240
//						"set(DisableVideo,false)", // videó stream engedélyezése
//						"set(Quality,30)", // JPEQ képkockák minősége 30 százalék
//						"set(Port," + port + ")", // figyelés beállítása a 8080-as portra
//						"set(Login," + user + ")", // felhasználónév beállítása a konfig alapján
//						"set(Password," + password + ")" // jelszó beállítása a konfig alapján
//						})
//				.putExtra("hidebtn1", true) // Súgó gomb elrejtése
//				.putExtra("caption2", getString(R.string.run_in_background)) // jobb oldali gomb feliratának beállítása
//				.putExtra("intent2", launcher) // jobb oldali gomb eseménykezelőjének beállítása, hogy hozza fel az asztalt
//			    .putExtra("returnto", launcher) // ha a programból kilépnek, szintén az asztal jön fel
//			    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // activity indítása új folyamatként, hogy a service elindíthassa
//			startActivity(ipwebcam); // a program indítása a fenti konfigurációval
//			try {
//				HttpURLConnection conn = (HttpURLConnection) new URL("http://127.0.0.1:" + port + "/videofeed").openConnection(); //kapcsolat objektum létrehozása
//	            conn.setRequestMethod("GET"); // GET metódus beállítása
//	            conn.setRequestProperty("Authorization", Base64.encodeBase64String(new String(user + ':' + password).getBytes()));
//	            // most, hogy minden be van állítva, kapcsolódás
//	            conn.connect();
//	            InputStream in = conn.getInputStream(); // mjpeg stream megszerzése
//			}
//			catch (ConnectException ex) {
//				Log.i("test", "retry later", ex);
//				try {
//					Thread.sleep(2000);
//				}
//				catch (Exception e) {
//					;
//				}
//				finally {
//					startIPWebcam();
//				}
//			}
//			catch (Exception ex) {
//				Log.i("test", "error", ex);
//			}
//		}
//	}
//	
//	private void stopIPWebcam() { //TODO: hatástalan a broadcast intent küldése. Miért?
//		if (isAppInstalled(PACKAGE_CAM)) {
//			sendBroadcast(new Intent("com.pas.webcam.CONTROL").putExtra("action", "stop"));
//		}
//	}
	
	private void connect() {
		connect(false);
	}
	
	private void connect(boolean reconnect) {
		if (conn == null || reconnect) {
			disconnect();
			if (createConnectionHelper() != null) conn.connect();
		}
	}
	
	private void disconnect() {
		if (conn != null) conn.disconnect();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (startId == 1) {
			initNotification();
			connect();
			updateNotificationText();
			setNotificationsVisible(true);
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (intent.hasExtra(KEY_EVENT)) {
			String event = intent.getStringExtra(KEY_EVENT);
			if (event.equals(EVT_CONNECTIVITY_CHANGE)) {
				if (startId != 1) setNetworkNotificationVisible(true);
				if (isNetworkAvailable()) connect();
				else disconnect();
			}
			else if (event.equals(EVT_GPS_SENSOR_CHANGE)) {
				if (startId != 1) setGpsEnableNotificationVisible(true);
			}
			else if (event.equals(EVT_APP_ADDED) || event.equals(EVT_APP_INSTALL)) {
				if (startId != 1) {
					setCamInstallNotificationVisible(true);
					connect();
				}
			}
		}
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		disconnect();
		setNotificationsVisible(false);
		removeNotification();
		super.onDestroy();
	}
	
	@SuppressWarnings("deprecation")
	private void initNotification() {
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification(R.drawable.ic_launcher, getString(R.string.app_name), System.currentTimeMillis());
		contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		notification.flags |= Notification.FLAG_ONGOING_EVENT;
	}
	
	@SuppressWarnings("deprecation")
	private void setNotificationText(String s) {
		if (nm != null && notification != null && contentIntent != null) {
			notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), s, contentIntent);
			nm.notify(ID_NOTIFY, notification);
		}
	}
	
	public void updateNotificationText() {
		setNotificationText(getString(R.string.vehicle) + ": " + getString(isVehicleConnected() ? R.string.exists : R.string.not_exists) + "; " + (isOfflineMode() ? getString(R.string.title_offline) : (getString(R.string.bridge_conn) + ": " + getString(isBridgeConnected() ? R.string.exists : R.string.not_exists))) + '.');
	}
	
	private void removeNotification() {
		nm.cancel(ID_NOTIFY);
		nm = null;
		notification = null;
		contentIntent = null;
	}
	
	private void addOnlineNotification(int resText, Intent intent, int key, boolean removable) {
		if (!isOfflineMode()) {
			addNotification(resText, intent, key, removable);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void addNotification(int resText, Intent intent, int key, boolean removable) {
		removeNotification(key);
		Notification notification = new Notification(R.drawable.ic_warning, getString(resText), System.currentTimeMillis());
		notification.flags |= removable ? Notification.FLAG_AUTO_CANCEL : Notification.FLAG_NO_CLEAR;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, removable ? PendingIntent.FLAG_ONE_SHOT : PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(resText), contentIntent);
		nm.notify(key, notification);
	}
	
	private void setNotificationsVisible(boolean visible) {
		setConfigNotificationVisible(visible);
		setNetworkNotificationVisible(visible);
		setGpsEnableNotificationVisible(visible);
		setCamInstallNotificationVisible(visible);
	}
	
	private void setConfigNotificationVisible(boolean visible) {
		if (visible && !config.isCorrect()) addOnlineNotification(R.string.set_config, new Intent(this, MainActivity.class), ID_NOTIFY_CONFIG, false);
		else removeNotification(ID_NOTIFY_CONFIG);
	}
	
	private void setNetworkNotificationVisible(boolean visible) {
		if (visible && !isNetworkAvailable()) addOnlineNotification(R.string.set_network, new Intent(Settings.ACTION_WIRELESS_SETTINGS), ID_NOTIFY_NETWORK, false);
		else removeNotification(ID_NOTIFY_NETWORK);
	}
	
	private void setCamInstallNotificationVisible(boolean visible) {
		if (visible && !isAppInstalled(PACKAGE_CAM)) addOnlineNotification(R.string.install_cam, new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=" + PACKAGE_CAM)), ID_NOTIFY_INST_CAM, false);
		else removeNotification(ID_NOTIFY_INST_CAM);
	}
	
	private void setGpsEnableNotificationVisible(boolean visible) {
		if (visible && !isGpsEnabled()) addOnlineNotification(R.string.set_gps, new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), ID_NOTIFY_GPS_ENABLE, false);
		else removeNotification(ID_NOTIFY_GPS_ENABLE);
	}
	
	private void removeNotification(int key) {
		if (nm == null) initNotification();
		nm.cancel(key);
	}
	
	public boolean isVehicleConnected() {
		return vehicle.isConnected();
	}
	
	public boolean isBridgeConnected() {
		return conn != null && conn.isConnected();
	}
	
	private boolean isOfflineMode() {
		return getSharedPreferences(this).getBoolean("offline", false);
	}
	
	private boolean isNetworkAvailable() {
		final NetworkInfo activeNetwork = getActiveNetworkInfo();
		return activeNetwork != null && activeNetwork.getState() == NetworkInfo.State.CONNECTED;
	}
	
	private NetworkInfo getActiveNetworkInfo() {
		try {
			if (cm == null) cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo();
		}
		catch (RuntimeException ex) {
			/* Null or Permission Denied */
			return null;
		}
	}
	
	private boolean isGpsEnabled() {
		if (lm == null) lm = (LocationManager) getSystemService(LOCATION_SERVICE);
		return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}
	
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
	
	private static SharedPreferences getSharedPreferences(Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static Config createConfig(Context context) {
		return new Config(getSharedPreferences(context));
	}
	
	public static boolean isStarted(Context context) {
		return getSharedPreferences(context).getBoolean(KEY_STARTED, false);
	}
	
	public static void setStarted(Context context, boolean b) {
		getSharedPreferences(context).edit().putBoolean(KEY_STARTED, b).commit();
	}
	
}
