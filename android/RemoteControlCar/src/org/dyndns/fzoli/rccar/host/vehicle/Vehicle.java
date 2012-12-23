package org.dyndns.fzoli.rccar.host.vehicle;

import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.IOIOLooper;

/**
 * A jármű vezérlését ellátó interfész kiegészítve a szükséges adatokkal.
 * @author zoli
 */
public interface Vehicle extends IOIOLooper {
	
	public interface Callback {
		
		public void onBatteryLevelChanged(Integer level);
		
	}
	
	public void setCallback(Callback callback);
	
	/**
	 * Megadja, hogy támogatva van-e a precíz kormányzás.
	 * @return true esetén nem támogatott és csak 0 vagy 100 százalék állítható be
	 */
	public boolean isFullX();
	
	/**
	 * Megadja, hogy támogatva van-e a precíz gázadás.
	 * @return true esetén nem támogatott és csak 0 vagy 100 százalék állítható be
	 */
	public boolean isFullY();
	
	/**
	 * Megadja, hogy a telefon összeköttetésben van-e az IOIO mikrovezérlővel.
	 * @return true esetén van összeköttetés, egyébként nincs
	 */
	public boolean isConnected();
	
	/**
	 * Az aktuális irány százalékban.
	 */
	public int getX();
	
	/**
	 * Az aktuális sebesség százalékban.
	 */
	public int getY();
	
	/**
	 * Az akkumulátor töltöttségét adja vissza százalékban.
	 */
	public int getBatteryLevel() throws ConnectionLostException, InterruptedException;
	
	/**
	 * Megadja, melyik lábat kell használni a feszültségméréshez.
	 */
	public int getBatteryPin();
	
	/**
	 * Maximum feszültséghatár akkumulátor-szint becsélésre.
	 */
	public float getMaxVoltage();
	
	/**
	 * Minimum feszültséghatár akkumulátor-szint becsélésre.
	 */
	public float getMinVoltage();
	
}
