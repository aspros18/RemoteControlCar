package org.dyndns.fzoli.rccar.host.vehicle;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.vehicle.impl.DefaultVehicle;
import org.dyndns.fzoli.rccar.host.vehicle.impl.PWM40Vehicle;
import org.dyndns.fzoli.rccar.host.vehicle.impl.PWMVehicle;

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
			case 1: // PWM-teszt
				return new PWMVehicle(service);
			case 2: // PWM40-teszt
				return new PWM40Vehicle(service);
			default: // alapértelmezett jármű: Prototípus
				return new DefaultVehicle(service);
		}
	}

}
