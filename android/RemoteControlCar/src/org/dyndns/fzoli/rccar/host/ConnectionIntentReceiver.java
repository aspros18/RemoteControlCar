package org.dyndns.fzoli.rccar.host;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectionIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action == null) action = "";
		if (action.equals("android.intent.action.BOOT_COMPLETED")) {
			if (ConnectionService.isStarted(context)) context.startService(new Intent(context, ConnectionService.class));
		}
	}

}
