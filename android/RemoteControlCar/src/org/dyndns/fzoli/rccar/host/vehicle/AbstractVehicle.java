package org.dyndns.fzoli.rccar.host.vehicle;

import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

import org.dyndns.fzoli.rccar.host.ConnectionService;

public abstract class AbstractVehicle extends BaseIOIOLooper implements Vehicle {
	
	/**
	 * A vezérlőjelet tartalmazó objektum referenciája a szolgáltatáson keresztül érhető el.
	 */
	private final ConnectionService SERVICE;
	
	/**
	 * Megadja, hogy van-e kapcsolat a mikrovezérlővel.
	 */
	private boolean connected = false;
	
	/**
	 * Konstruktor.
	 * @param service a szolgáltatás a vezérlőjelet tartalmazó objektum referenciájának megszerzéséhez
	 */
	public AbstractVehicle(ConnectionService service) {
		SERVICE = service;
	}
	
	/**
	 * Megadja, hogy a telefon összeköttetésben van-e az IOIO mikrovezérlővel.
	 * @return true esetén van összeköttetés, egyébként nincs
	 */
	@Override
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Az aktuális irány százalékban.
	 */
	@Override
	public int getX() {
		if (SERVICE.getBinder() == null) return 0;
		return SERVICE.getBinder().getX();
	}
	
	/**
	 * Az aktuális sebesség százalékban.
	 */
	@Override
	public int getY() {
		if (SERVICE.getBinder() == null) return 0;
		return SERVICE.getBinder().getY();
	}
	
	/**
	 * A kapcsolat létrejötte után a mikrovezérlőn kigyullad az állapotjelző LED
	 * és a szolgáltatás tudomást szerez az állapotváltozásról.
	 */
	@Override
	protected void setup() throws ConnectionLostException, InterruptedException {
		updateState(true); // állapotváltozás jelzése a szolgáltatásnak
		ioio_.openDigitalOutput(IOIO.LED_PIN, !true); // a LED bekapcsolása
	}
	
	/**
	 * Ha a kapcsolat megszakad a mikrovezérlővel,
	 * állapotváltozás jelzése a szolgáltatásnak.
	 */
	@Override
	public void disconnected() {
		updateState(false);
	}
	
	/**
	 * Állapotváltozás jelzése a szolgáltatásnak.
	 */
	private void updateState(boolean connected) {
		this.connected = connected; // új érték beállítása
		SERVICE.onVehicleConnectionStateChanged(); // majd jelzés a szolgáltatásnak
	}
	
}
