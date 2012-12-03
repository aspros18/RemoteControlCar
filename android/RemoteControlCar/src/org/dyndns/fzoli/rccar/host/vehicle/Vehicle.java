package org.dyndns.fzoli.rccar.host.vehicle;

import ioio.lib.util.IOIOLooper;

/**
 * A jármű vezérlését ellátó interfész kiegészítve a szükséges adatokkal.
 * @author zoli
 */
public interface Vehicle extends IOIOLooper {
	
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
	
}