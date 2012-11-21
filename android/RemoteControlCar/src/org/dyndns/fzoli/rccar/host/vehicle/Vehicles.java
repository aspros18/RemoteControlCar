package org.dyndns.fzoli.rccar.host.vehicle;

import org.dyndns.fzoli.rccar.host.ConnectionBinder;
import org.dyndns.fzoli.rccar.host.vehicle.impl.DefaultVehicle;

/**
 * @author zoli
 */
public class Vehicles {

	public static Vehicle createVehicle(ConnectionBinder binder, int index) {
		switch (index) {
			default:
				return new DefaultVehicle(binder);
		}
	}

}
