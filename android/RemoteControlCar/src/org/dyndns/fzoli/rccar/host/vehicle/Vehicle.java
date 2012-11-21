package org.dyndns.fzoli.rccar.host.vehicle;

import ioio.lib.util.IOIOLooper;

/**
 * @author zoli
 */
public interface Vehicle extends IOIOLooper {
	
	public boolean isFullX();
	
	public boolean isFullY();
	
	public boolean isConnected();
	
	public int getX();
	
	public int getY();
	
}
