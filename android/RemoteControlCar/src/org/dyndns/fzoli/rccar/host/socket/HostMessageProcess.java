package org.dyndns.fzoli.rccar.host.socket;

import java.io.InvalidClassException;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.rccar.model.host.HostData.PartialHostData;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

import android.util.Log;

public class HostMessageProcess extends MessageProcess {

	// TODO: akkuszint változás észlelés és küldés a hídnak [SERVICE.getVehicle().setCallback(...);]
	// hasonlóképpen a szenzorok eseményeinek küldésével
	// a host data küldése előtt a szenzorok adatainak kiolvasása, hogy egyből friss adatokat kapjon a híd
	// a szenzorok figyelése csak kiépített kapcsolatok esetén szükséges továbbá
	// ha a szenzor eseményfigyelője meghívódik, a helyi adat frissítésével egy időben üzenés a hídnak is a beállított időköznek megfelelően
	
	/**
	 * A szolgáltatás referenciája, hogy lehessen a változásról értesíteni.
	 */
	private final ConnectionService SERVICE;
	
	public HostMessageProcess(ConnectionService service, SecureHandler handler) {
		super(handler);
		SERVICE = service;
	}
	
	/**
	 * Amint kapcsolódott a hídhoz az alkalmazás, a szenzorok figyelése megkezdődik.
	 * Amint az akkumulátor-szint, GPS helyzet, mágneses-térerősség és nehézségi gyorsulás
	 * első adatai megérkeztek és beállítódtak a jármű modelében, a feldolgozó elküldi a jármű adatait.
	 * Ekkor a híd az online járművek listájába beteszi a jármű modelt a kezdőadatokkal.
	 * Ettől kezdve már csak a részadatok küldése szükséges külön-külön a megadott időközönként.
	 * A regisztrált szenzorok eseményfigyelői továbbá is frissítik a helyi model adatait és amikor ideje küldeni,
	 * a szenzorok adatai el lesznek küldve, ha valamelyikük megváltozott.
	 * A telefon három szenzorának változásai függenek a frissítési időköztől és részmodelben küldődnek el.
	 * Az akkumulátor-szint változása viszont nem függ a frissítési időköztől, hanem amint 1 tizedet csökken
	 * az akkumulátor feszültsége, azonnal külön részmodelben jut el az adat a hídhoz.
	 * TODO: Az akkumulátor százalékos értékének kiszámítása a minimum és maximum feszültségszinttel történik a HÍD oldalán.
	 */
	@Override
	protected void onStart() {
		SERVICE.getBinder().sendHostData(this); // teszt
	}
	
	/**
	 * TODO: Ha a kapcsolat bezárul a híddal, akkor nem kell tovább figyelni a szenzoradatok változását,
	 * ezért az eseménykezelők leregisztrálódnak.
	 */
	@Override
	protected void onStop() {
		;
	}
	
	/**
	 * A híd által küldött üzenetek feldolgozása.
	 * Ha az üzenet a HostData részadata, akkor frissíti a HostData változóit.
	 * @param msg az üzenet
	 */
	@Override
	protected void onMessage(Object msg) {
		if (msg instanceof HostData.PartialHostData) {
			SERVICE.getBinder().updateHostData((PartialHostData<?>) msg);
		}
	}

	/**
	 * Ha üzenetküldés vagy fogadás közben hiba történik, ez a metódus fut le.
	 * Itt csak egyetlen hiba az említésre méltó:
	 * eltérő kliens és szerver verzió, tehát nem kompatibilis a kliens a szerverrel, nem lehet üzenni
	 * A szolgáltatás univerzális kapcsolódás hibakezelő metódusát hívja meg a kivételnek megfelelően.
	 * A hibakezelő metódus megjeleníti az értesítést a felületen és a hibától függően reagál.
	 * Az OTHER hibakategória esetén nem jelenik meg értesítés a felületen.
	 * Ha a hibakategórió fatális hiba, nem lesz megismételve a kapcsolódás.
	 */
	@Override
	protected void onException(Exception ex) {
		ConnectionError err = null;
		try {
			throw ex;
		}
		catch (InvalidClassException e) {
			Log.e(ConnectionService.LOG_TAG, "bridge is not compatible with this client", e);
			err = ConnectionError.WRONG_CLIENT_VERSION;
		}
		catch (Exception e) {
			err = ConnectionError.OTHER;
			Log.i(ConnectionService.LOG_TAG, "unknown error", e);
		}
		SERVICE.onConnectionError(err);
	}

}
