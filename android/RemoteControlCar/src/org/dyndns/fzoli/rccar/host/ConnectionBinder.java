package org.dyndns.fzoli.rccar.host;

import java.io.Serializable;

import org.dyndns.fzoli.rccar.host.socket.HostMessageProcess;
import org.dyndns.fzoli.rccar.model.Command;
import org.dyndns.fzoli.rccar.model.Control;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
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
		
		/**
		 * A feszültség-szint változását jelzi.
		 * Null esetén nincs adat.
		 */
		public void onVoltageChanged(Float voltage);
	}
	
	/**
	 * A jármű adatai.
	 */
	private final HostData DATA;
	
	/**
	 * Az objektumot létrehozó Service referenciája.
	 */
	private final ConnectionService SERVICE;
	
	private int mXYcounter = 0;
	
	/**
	 * Az activity eseményfigyelője.
	 */
	private Listener mListener;
	
	/**
	 * Az aktív üzenetküldő.
	 */
	private HostMessageProcess mSender;
	
	/**
	 * Az utolsó állapotjelzés.
	 * Null, ha már friss az adat vagy még nem érkezett adat.
	 */
	private Boolean cacheConnecting = null;
	
	/**
	 * Konstruktor.
	 * A jármű kezdeti paramétereit állítja be és a változókat.
	 * @param service a szolgáltatás objektuma
	 */
	public ConnectionBinder(ConnectionService service) {
		SERVICE = service;
		DATA = new HostData(SERVICE.getVehicle().isFullX(), SERVICE.getVehicle().isFullY());
	}
	
	/**
	 * Az objektumot létrehozó Service referenciája.
	 */
	public ConnectionService getService() {
		return SERVICE;
	}
	
	public HostData getHostData() {
		return DATA;
	}
	
	/**
	 * Az autó vezérlőjelét adja vissza.
	 */
	private Control getControl() {
		return DATA.getControl();
	}
	
	/**
	 * Irány százalékban.
	 */
	public int getX() {
		if (getControl() == null) return 0;
		return getControl().getX();
	}
	
	/**
	 * Sebesség százalékban.
	 */
	public int getY() {
		if (getControl() == null) return 0;
		return getControl().getY();
	}
	
	/**
	 * Megadja, hogy hány alkalommal állították át a vezérlőjelet.
	 * Így lehet a legegyszerűbben megtudni, hogy egy szálban az utolsó vezérlőjel
	 * átállítása óta más szál módosította-e már a vezérlőjelet.
	 */
	public int getXYCounter() {
		return mXYcounter;
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
	 * @param remote true esetén jelzi az Activitynek a módosulást, egyébként a hídnak küld üzenetet.
	 */
	public void setXY(int x, int y, boolean remote) {
		if (getControl() != null) {
			getControl().setX(x);
			getControl().setY(y);
		}
		fireArrowChange(remote);
		mXYcounter++;
	}
	
	/**
	 * Irány és sebesség megadása százalékban.
	 * Jelzi az Activitynek a módosulást.
	 */
	public void setXY(int x, int y) {
		setXY(x, y, true);
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
	 * @param remote true esetén jelzi az Activitynek a módosulást, egyébként a hídnak küld üzenetet.
	 */
	public void setX(int x, boolean remote) {
		setXY(x, getY(), remote);
	}
	
	/**
	 * Sebesség megadása százalékban.
	 * @param remote true esetén jelzi az Activitynek a módosulást, egyébként a hídnak küld üzenetet.
	 */
	public void setY(int y, boolean remote) {
		setXY(getX(), y, remote);
	}
	
	/**
	 * A jármű adatait elküldi a hídnak, ha tudja és eltárolja az üzenetküldő referenciáját.
	 */
	public void sendHostData(HostMessageProcess sender) {
		if (sender != null) {
			mSender = sender;
			sender.sendMessage(DATA);
		}
	}
	
	/**
	 * Az üzenetküldő referenciáját kinullázza, ha azt maga az üzenetküldő kéri.
	 */
	public void removeSender(HostMessageProcess sender) {
		if (sender == mSender) mSender = null;
	}
	
	/**
	 * Üzenetet küld a Hídnak, ha van kiépített kapcsolat.
	 */
	private void sendMessage(Serializable s) {
		if (mSender != null) mSender.sendMessage(s);
	}
	
	/**
	 * Parancs feldolgozó.
	 * Továbbfejlesztési lehetőség; még nem csinál semmit.
	 */
	public void onCommand(Command cmd) {
		switch (cmd) {
			
		}
	}
	
	/**
	 * Frissíti a HostData beállításait annak egyik részadata alapján.
	 * Ha a részadat vezérlőjel, akkor jelzi az eseményt a felületnek.
	 */
	public void updateHostData(PartialBaseData<HostData, ?> partialData) {
		DATA.update(partialData);
		if (partialData instanceof HostData.ControlPartialHostData) {
			fireArrowChange(true);
		}
	}
	
	/**
	 * Jelzi a felület eseménykezelőjének, hogy változott a feszültség.
	 */
	public void fireVoltageChanged(Float voltage) {
		if (mListener != null) mListener.onVoltageChanged(voltage);
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
	 * Frissíti az adatmodelt, az Activity felületét illetve üzen a Híd szervernek.
	 * Ha a jármű le lett választva, a felületen a feszültség-szint elrejtése.
	 */
	public void fireVehicleConnectionStateChanged(boolean connected) {
		if (!connected) fireVoltageChanged(null);
		getHostData().setVehicleConnected(connected);
		sendMessage(new HostData.BooleanPartialHostData(connected, HostData.BooleanPartialHostData.BooleanType.VEHICLE_CONNECTED));
	}
	
	/**
	 * Vezérlőjel változás jelzése az Activitynek vagy a Hídnak, ha kell.
	 * Ha a felületről állították be, nem kell újra jelezni.
	 * Ha nincs kinek jelezni, a jelzés elmarad.
	 * @param remote true, ha a híd adja az üzenetet, false ha a felületről érkezik.
	 */
	private void fireArrowChange(boolean remote) {
		if (remote) {
			if (mListener != null) mListener.onArrowChange(getX(), getY());
		}
		else {
			// csak a teljesség kedvéért, de egyelőre nincs használva sehol
			sendMessage(new HostData.ControlPartialHostData(getControl()));
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
