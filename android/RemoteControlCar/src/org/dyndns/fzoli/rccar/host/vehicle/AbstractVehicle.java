package org.dyndns.fzoli.rccar.host.vehicle;

import ioio.lib.api.AnalogInput;
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
	 * Eseménykezelő akkumulátor-szint változás detektálására.
	 */
	private Callback callback;
	
	/**
	 * Az eseménykezelőnek utóljára jelzett akkumulátor-szint.
	 */
	private Integer oldBatteryLevel;
	
	/**
	 * Feszültségméréshez használt analóg bemenet.
	 */
	private AnalogInput inBattery;
	
	/**
	 * Konstruktor.
	 * @param service a szolgáltatás a vezérlőjelet tartalmazó objektum referenciájának megszerzéséhez
	 */
	public AbstractVehicle(ConnectionService service) {
		SERVICE = service;
	}
	
	/**
	 * Eseménykezelő beállítása az akkumulátor-szint változás detektálására.
	 */
	@Override
	public void setCallback(Callback callback) {
		this.callback = callback;
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
		inBattery = ioio_.openAnalogInput(getBatteryPin()); // feszültségméréshez használt analóg bemenet létrehozása
	}
	
	/**
	 * Ha az akkumulátor-szint változott, meghívja az eseménykezelőt, végül vár 20 ezredmásodpercet.
	 */
	@Override
	public void loop() throws ConnectionLostException, InterruptedException {
		float voltage = inBattery.getVoltage(); // feszültség-szint olvasása
		SERVICE.getBinder().fireVoltageChanged(voltage); // feszültség küldése a felületnek
		refreshBattery(getBatteryLevel(voltage)); // akku-szint frissítése, ha kell
		Thread.sleep(20); // 20 ms szünet
	}
	
	/**
	 * Frissíti az akkumulátor-szintet, ha az megbízható és megváltozott.
	 * @param level az akku-szint százalékban, vagy null, ha nincs akku.
	 */
	private void refreshBattery(Integer level) {
		if (callback != null && getX() == 0 && getY() == 0) { // ha van eseménykezelő és megbízható az adat (egyik motor sem jár)
			if ((oldBatteryLevel == null || !oldBatteryLevel.equals(level)) && (level == null || level > 0)) { // ha változott az akkuszint és az akkuszint nem nulla
				oldBatteryLevel = level; // akkuszint frissítése
				SERVICE.getBinder().getHostData().setBatteryLevel(level); // host data frissítése
				callback.onBatteryLevelChanged(level); // callback hívása
			}
		}
	}
	
	/**
	 * Az akkumulátor töltöttségét adja vissza százalékban.
	 */
	@Override
	public int getBatteryLevel(float voltage) {
		int percent = (int)((voltage - getMinVoltage()) * 100 / (getMaxVoltage() - getMinVoltage()));
		if (percent >= 100) return 100;
		if (percent <= 0) return 0;
		return percent;
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
	 * Újrainicializálás esetén előző akku-szint nullázása.
	 */
	@Override
	public void onReset() {
		oldBatteryLevel = null;
	}
	
	/**
	 * Állapotváltozás jelzése a szolgáltatásnak.
	 */
	private void updateState(boolean connected) {
		this.connected = connected; // új érték beállítása
		SERVICE.onVehicleConnectionStateChanged(); // majd jelzés a szolgáltatásnak
	}
	
}
