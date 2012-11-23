package org.dyndns.fzoli.rccar.host;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectionIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectionService.isStarted(context)) {
			String action = intent.getAction();
			if (action == null) action = "";
			Intent serviceIntent = new Intent(context, ConnectionService.class);
			if (action.equals("android.intent.action.BOOT_COMPLETED")) {
				context.startService(serviceIntent);
			}
			else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE")) {
				serviceIntent.putExtra(ConnectionService.KEY_EVENT, ConnectionService.EVT_CONNECTIVITY_CHANGE);
				context.startService(serviceIntent);
			}
		}
	}

}
