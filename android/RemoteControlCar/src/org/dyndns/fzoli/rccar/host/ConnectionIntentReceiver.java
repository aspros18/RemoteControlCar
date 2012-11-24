package org.dyndns.fzoli.rccar.host;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Az alábbi rendszereseményeket továbbítja a szolgáltatásnak:
 * - android.intent.action.BOOT_COMPLETED
 * - android.net.conn.CONNECTIVITY_CHANGE
 * - android.location.PROVIDERS_CHANGED
 */
public class ConnectionIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectionService.isStarted(context)) {
			String evt = intent.getAction();
			if (evt != null) {
				Intent serviceIntent = new Intent(context, ConnectionService.class);
				serviceIntent.putExtra(ConnectionService.KEY_EVENT, evt);
				context.startService(serviceIntent);
			}
		}
	}

}
