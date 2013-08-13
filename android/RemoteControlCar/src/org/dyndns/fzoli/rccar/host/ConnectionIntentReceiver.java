package org.dyndns.fzoli.rccar.host;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Az alábbi rendszereseményeket továbbítja a szolgáltatásnak ha az fut, vagy el kell indítani:
 * - android.intent.action.ACTION_POWER_DISCONNECTED (az áramforrást eltávolították)
 * - android.intent.action.BOOT_COMPLETED (az Android oprendszer betöltése befejeződött)
 * - android.intent.action.MEDIA_MOUNTED (az SD-kártya felcsattolódott)
 * - android.net.conn.CONNECTIVITY_CHANGE (a hálózati kapcsolat megszünt, a telefon kapcsolódás alatt van vagy kapcsolódott egy hálózathoz)
 * - android.location.PROVIDERS_CHANGED (a GPS szenzor be- vagy ki lett kapcsolva)
 * - android.intent.action.ACTION_SHUTDOWN (az Android oprendszer leáll vagy újraindul)
 * - org.dyndns.fzoli.rccar.host.SERVICE_DESTROY (az alkalmazás szolgáltatása megszűnik)
 */
public class ConnectionIntentReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (ConnectionService.isStarted(context)) { // ha fut (vagy futott de váratlanul leállt és el kell indítani) a service
			String evt = intent.getAction(); // esemény lekérése
			if (evt != null && (!ConnectionService.isOfflineMode(context) || evt.equals(ConnectionService.EVT_POWER_DOWN))) { // ha van esemény és az online mód aktív, esemény küldése a szolgáltatásnak
				Intent serviceIntent = new Intent(context, ConnectionService.class); // üzenet létrehozása
				serviceIntent.putExtra(ConnectionService.KEY_EVENT, evt); // az esemény mellékelése
				context.startService(serviceIntent); // üzenés a szolgáltatásnak (és ha nem futott, elindítása)
			}
		}
	}

}
