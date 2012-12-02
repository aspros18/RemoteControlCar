package org.dyndns.fzoli.rccar.host;

import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.host.HostData;

import android.os.Binder;
import android.util.Log;

/**
 * A service és az activity közötti kommunikációt megvalósító osztály.
 * @author zoli
 */
public class ConnectionBinder extends Binder {
	
	/**
	 * Amikor új vezérlőparancs érkezik az activity felületén látható nyilat újra kell rajzolni.
	 * Ha a service elkezd kapcsolódni a szerverhez, vagy befejezi azt, az activity egy dialógus ablakot jelenít meg illetve tüntet el.
	 * Ezekre az eseményekre egy eseményfigyelőt implementálok az activityben.
	 */
	public static interface Listener {
		
		/**
		 * Vezérlőparancs érkezett.
		 * @param x irány százalékos értéke
		 * @param y sebesség százalékos értéke
		 */
		public void onArrowChange(int x, int y);
		
		/**
		 * Kapcsolódás állapotváltozás.
		 * @param connecting true, ha elindult a kapcsolódás egyébként végetért
		 */
		public void onConnectionStateChange(boolean connecting);
		
	}
	
	/**
	 * A jármű adatai.
	 */
	private final HostData DATA = new HostData();
	
	/**
	 * Az objektumot létrehozó Service referenciája.
	 */
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
	
	/**
	 * Az objektumot létrehozó Service referenciája.
	 */
	public ConnectionService getService() {
		return SERVICE;
	}
	
	/**
	 * Az autó vezérlőjelét adja vissza.
	 */
	private Control getControl() {
		return DATA.getControl();
	}
	
	/**
	 * Megadja, hogy pontosan szabályozható-e a az irány.
	 * True esetén csak 0 vagy 100 lehet az érték.
	 */
	public boolean isFullX() {
		return DATA.isFullX();
	}
	
	/**
	 * Megadja, hogy pontosan szabályozható-e a a sebesség.
	 * True esetén csak 0 vagy 100 lehet az érték.
	 */
	public boolean isFullY() {
		return DATA.isFullY();
	}
	
	/**
	 * Irány százalékban.
	 */
	public int getX() {
		return getControl().getX();
	}
	
	/**
	 * Sebesség százalékban.
	 */
	public int getY() {
		return getControl().getY();
	}
	
	/**
	 * Irány nullázása.
	 * Jelzi az Activitynek a módosulást.
	 * @return a nullázás előtti érték
	 */
	public int resetX() {
		int tmp = getX();
		setX(0);
		return tmp;
	}
	
	/**
	 * Sebesség nullázása.
	 * Jelzi az Activitynek a módosulást.
	 * @return a nullázás előtti érték
	 */
	public int resetY() {
		int tmp = getY();
		setY(0);
		return tmp;
	}
	
	/**
	 * Irány és sebesség nullázása.
	 * Jelzi az Activitynek a módosulást.
	 */
	public void resetXY() {
		resetX();
		resetY();
	}
	
	/**
	 * Irány és sebesség megadása százalékban.
	 * Jelzi az Activitynek a módosulást.
	 */
	public void setXY(int x, int y) {
		setX(x);
		setY(y);
	}
	
	/**
	 * Irány megadása százalékban.
	 * Jelzi az Activitynek a módosulást.
	 */
	public void setX(int x) {
		setX(x, true);
	}
	
	/**
	 * Sebesség megadása százalékban.
	 * Jelzi az Activitynek a módosulást.
	 */
	public void setY(int y) {
		setY(y, true);
	}
	
	/**
	 * Irány megadása százalékban.
	 * @param remote true esetén jelzi az Activitynek a módosulást
	 */
	public void setX(int x, boolean remote) {
		getControl().setX(x);
		fireArrowChange(remote);
	}
	
	/**
	 * Sebesség megadása százalékban.
	 * @param remote true esetén jelzi az Activitynek a módosulást
	 */
	public void setY(int y, boolean remote) {
		getControl().setY(y);
		fireArrowChange(remote);
	}
	
	/**
	 * Jelzést ad le az Activitynek, hogy megváltozott a kapcsolódás állapota.
	 * Ha az Activity még nem regisztrálta eseményfigyelőjét, az eseményt akkor fogja megkapni, amikor regisztrálja azt.
	 */
	public void fireConnectionStateChange(boolean connecting) {
		Log.i(ConnectionService.LOG_TAG, "connecting dialog: " + connecting);
		if (mListener != null) {
			cacheConnecting = null;
			mListener.onConnectionStateChange(connecting);
		}
		else {
			cacheConnecting = connecting;
		}
	}
	
	/**
	 * Vezérlőjel változás jelzése az Activitynek, ha kell.
	 * Ha a felületről állították be, nem kell újra jelezni.
	 * Ha nincs kinek jelezni, a jelzés elmarad.
	 * @param remote true, ha a híd adja az üzenetet, false ha a felületről érkezik.
	 */
	private void fireArrowChange(boolean remote) {
		if (remote) {
			if (mListener != null) mListener.onArrowChange(getX(), getY());
		}
		else {
			//TODO: küldés szervernek UPDATE: nem fog kelleni, mert csak local módban állítható a telefonon az érték
		}
	}
	
	/**
	 * Az Activity eseménykezelőjének felregisztrálása és leregisztrálása.
	 * Felregisztráláskor az elmulasztott kapcsolódás állapot is elküldésre kerül.
	 * @param listener ha null, akkor leregisztrálás, egyébként felregisztrálás
	 */
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
