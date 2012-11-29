package org.dyndns.fzoli.rccar.host;

import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.host.HostData;

import android.os.Binder;

/**
 * A service és az activity közötti kommunikációt megvalósító osztály.
 * @author zoli
 */
public class ConnectionBinder extends Binder {
	
	/**
	 * Amikor új vezérlőparancs érkezik az activity felületén látható nyilat újra kell rajzolni.
	 * Erre egy eseményfigyelőt implementálok az activityben.
	 */
	public static interface Listener {
		
		public void onArrowChange(int x, int y);
		
		public void onConnectionStateChange(boolean connecting);
		
	}
	
	/**
	 * A jármű adatai.
	 */
	private final HostData DATA = new HostData();
	
	private final ConnectionService SERVICE;
	
	/**
	 * Az activity eseményfigyelője.
	 */
	private Listener mListener;
	
	/**
	 * Az utolsó állapotjelzés.
	 * Null, ha már friss az adat vagy még nem érkezett adat.
	 */
	private Boolean cacheConnecting = null;
	
	public ConnectionBinder(ConnectionService service) {
		SERVICE = service;
	}
	
	public ConnectionService getService() {
		return SERVICE;
	}
	
	private Control getControl() {
		return DATA.getControl();
	}
	
	public boolean isFullX() {
		return DATA.isFullX();
	}
	
	public boolean isFullY() {
		return DATA.isFullY();
	}
	
	public int getX() {
		return getControl().getX();
	}
	
	public int getY() {
		return getControl().getY();
	}
	
	public void setX(int x) {
		setX(x, true);
	}
	
	public void setY(int y) {
		setY(y, true);
	}
	
	public void setX(int x, boolean remote) {
		getControl().setX(x);
		fireArrowChange(remote);
	}
	
	public void setY(int y, boolean remote) {
		getControl().setY(y);
		fireArrowChange(remote);
	}
	
	public void fireConnectionStateChange(boolean connecting) {
		if (isListener()) {
			cacheConnecting = null;
			mListener.onConnectionStateChange(connecting);
		}
		else {
			cacheConnecting = connecting;
		}
	}
	
	private void fireArrowChange(boolean remote) {
		if (remote) {
			if (isListener()) mListener.onArrowChange(getX(), getY());
		}
		else {
			//TODO: küldés szervernek
		}
	}
	
	private boolean isListener() {
		return mListener != null;
	}
	
	public void setListener(Listener listener) {
		mListener = listener;
		if (listener != null) {
			if (cacheConnecting != null) synchronized (cacheConnecting) {
				listener.onConnectionStateChange(cacheConnecting);
				cacheConnecting = null;
			}
		}
	}
	
}
