package org.dyndns.fzoli.rccar.host;

import ioio.lib.util.android.IOIOService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ConnectionService extends IOIOService {
	
	private final static int ID_NOTIFY = 0;
	
	private final static String KEY_STARTED = "started";
	
	private final ConnectionBinder BINDER = new ConnectionBinder(this);
	private final IOIOVehicleLooper LOOPER = new IOIOVehicleLooper(BINDER);
	
	private NotificationManager nm;
	private Notification notification;
	private PendingIntent contentIntent;
	
	@Override
	public ConnectionBinder onBind(Intent intent) {
		return BINDER;
	}
	
	@Override
	public IOIOVehicleLooper createIOIOLooper() {
		return LOOPER;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		if (startId == 1) {
			initNotification();
			updateNotificationText();
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
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
		setNotificationText("Vehicle " + (isVehicleConnected() ? "" : "NOT") + " OK");
	}
	
	private void removeNotification() {
		nm.cancel(ID_NOTIFY);
		nm = null;
		notification = null;
		contentIntent = null;
	}
	
	public boolean isVehicleConnected() {
		return LOOPER.isConnected();
	}
	
	private static SharedPreferences getSharedPreferences(Context context) {
		PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public static boolean isStarted(Context context) {
		return getSharedPreferences(context).getBoolean(KEY_STARTED, false);
	}
	
	public static void setStarted(Context context, boolean b) {
		getSharedPreferences(context).edit().putBoolean(KEY_STARTED, b).commit();
	}
	
}
