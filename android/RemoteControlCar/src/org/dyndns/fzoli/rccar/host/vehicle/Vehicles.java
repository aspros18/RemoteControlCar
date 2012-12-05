package org.dyndns.fzoli.rccar.host.vehicle;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.vehicle.impl.DefaultVehicle;

/**
 * Járműgyártó osztály.
 * Az összes járművezérlő implementáció közül index alapján példányosít egy vezérlőt.
 * Az indexet a felhasználó állítja be, amikor a felületen kiválasztja a beállításokban a járművet.
 * Az alapértelmezett index: 0
 * @author zoli
 */
public class Vehicles {

	/**
	 * Járművezérlő gyártása.
	 * @param service szükséges referecia a járművezérlő példányosításához
	 * @param index ez alapján dől el, melyik járművezérlő jöjjön létre
	 */
	public static Vehicle createVehicle(ConnectionService service, int index) {
		switch (index) {
			default: // még nem tudok mással szolgálni
				return new DefaultVehicle(service);
		}
	}

}
