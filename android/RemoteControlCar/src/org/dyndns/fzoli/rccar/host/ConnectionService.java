package org.dyndns.fzoli.rccar.host;

import ioio.lib.util.android.IOIOService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ConnectionService extends IOIOService {
	
	private final static String KEY_STARTED = "started";
	
	private final ConnectionBinder BINDER = new ConnectionBinder();
	
	@Override
	public IOIOVehicleLooper createIOIOLooper() {
		return new IOIOVehicleLooper(BINDER);
	}
	
	@Override
	public ConnectionBinder onBind(Intent intent) {
		return BINDER;
	}
	
	public static SharedPreferences getSharedPreferences(Context context) {
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
