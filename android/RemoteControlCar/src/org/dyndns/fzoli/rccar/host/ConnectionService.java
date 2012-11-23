package org.dyndns.fzoli.rccar.host;

import ioio.lib.util.android.IOIOService;

import org.dyndns.fzoli.rccar.host.socket.ConnectionHelper;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicle;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicles;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.widget.Toast;

public class ConnectionService extends IOIOService {
	
	private final static int ID_NOTIFY = 0;
	private final static int ID_NOTIFY_CONFIG = 1;
	private final static int ID_NOTIFY_NETWORK = 2;
	
	private final static String KEY_STARTED = "started";
	
	public final static String KEY_EVENT = "event";
	public final static String EVT_CONNECTIVITY_CHANGE = "connectivity change";
	
	private final ConnectionBinder BINDER = new ConnectionBinder(this);
	
	private Vehicle vehicle;
	private Config config;
	private ConnectionHelper conn;
	
	private ConnectivityManager cm;
	
	private NotificationManager nm;
	private Notification notification;
	private PendingIntent contentIntent;
	
	@Override
	public ConnectionBinder onBind(Intent intent) {
		return BINDER;
	}
	
	@Override
	public Vehicle createIOIOLooper() {
		return vehicle = Vehicles.createVehicle(BINDER, Integer.parseInt(getSharedPreferences(this).getString("vehicle", "0")));
	}
	
	private ConnectionHelper createConnectionHelper(boolean warning) {
		config = createConfig(this);
		if (!config.isCorrect() || !isNetworkAvailable()) {
			if (warning) Toast.makeText(this, R.string.local_mode, Toast.LENGTH_SHORT).show();
			return null;
		}
		return conn = new ConnectionHelper(config);
	}
	
	private void connect(boolean warning) {
		disconnect();
		if (createConnectionHelper(warning) != null) conn.connect();
	}
	
	private void disconnect() {
		if (conn != null) conn.disconnect();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (startId == 1) {
			initNotification();
			connect(true);
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
				if (isNetworkAvailable()) connect(false);
				else disconnect();
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
		setNotificationText("Vehicle" + (isVehicleConnected() ? "" : " NOT") + " OK" + (config.isCorrect() ? "" : "; " + getString(R.string.local_mode)));
	}
	
	private void removeNotification() {
		nm.cancel(ID_NOTIFY);
		nm = null;
		notification = null;
		contentIntent = null;
	}
	
	@SuppressWarnings("deprecation")
	private void addNotification(int resText, Intent intent, int key) {
		removeNotification(key);
		Notification notification = new Notification(R.drawable.ic_launcher, getString(resText), System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
		notification.setLatestEventInfo(getApplicationContext(), getString(R.string.app_name), getString(resText), contentIntent);
		nm.notify(key, notification);
	}
	
	private void setNotificationsVisible(boolean visible) {
		setConfigNotificationVisible(visible);
		setNetworkNotificationVisible(visible);
	}
	
	private void setConfigNotificationVisible(boolean visible) {
		if (visible && !config.isCorrect()) addNotification(R.string.set_config, new Intent(this, MainActivity.class), ID_NOTIFY_CONFIG);
		else removeNotification(ID_NOTIFY_CONFIG);
	}
	
	private void setNetworkNotificationVisible(boolean visible) {
		if (visible && !isNetworkAvailable()) addNotification(R.string.set_network, new Intent(Settings.ACTION_WIRELESS_SETTINGS), ID_NOTIFY_NETWORK);
		else removeNotification(ID_NOTIFY_NETWORK);
	}
	
	private void removeNotification(int key) {
		nm.cancel(key);
	}
	
	public boolean isVehicleConnected() {
		return vehicle.isConnected();
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
