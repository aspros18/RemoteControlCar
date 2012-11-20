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
		public void onChange(int x, int y);
	}
	
	/**
	 * A jármű adatai.
	 */
	private final HostData DATA = new HostData();
	
	/**
	 * Az activity eseményfigyelője.
	 */
	private Listener mListener;
	
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
		fireChange(remote);
	}
	
	public void setY(int y, boolean remote) {
		getControl().setX(y);
		fireChange(remote);
	}
	
	private void fireChange(boolean remote) {
		if (remote) {
			if (isListener()) mListener.onChange(getX(), getY());
		}
		else {
			//TODO: küldés szervernek
		}
	}
	
	public boolean isListener() {
		return mListener != null;
	}
	
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
}
