package org.dyndns.fzoli.rccar.host.vehicle.impl;

import org.dyndns.fzoli.rccar.host.ConnectionService;

/**
 * PWM alapú járművezérlő teszt.
 * Az eredeti vezérlőjel módosításra kerül, a kezdőérték 40.
 */
public class PWM40Vehicle extends PWMVehicle {

	public PWM40Vehicle(ConnectionService service) {
		super(service);
		START = 40;
	}
	
//	@Override
//	public boolean isFullX() {
//		return true;
//	}
	
}
