package org.dyndns.fzoli.rccar.host;

import java.io.File;

import android.content.SharedPreferences;

/**
 * A hídhoz való kapcsolódás konfigurációja.
 * Használatos még konfiguráció ellenőrzésére is.
 * Az androidon használt {@code SharedPreferences} alapján tér vissza értékekkel.
 */
public class Config implements org.dyndns.fzoli.rccar.clients.ClientConfig {

	/**
	 * A telefon belső memóriájában tárolt konfiguráció.
	 */
	private final SharedPreferences PREFERENCES;
	
	/**
	 * Konstruktor.
	 * @param preferences a telefon belső memóriájában tárolt konfiguráció
	 */
	public Config(SharedPreferences preferences) {
		PREFERENCES = preferences;
	}
	
	/**
	 * Megadja, hogy a konfiguráció helyes-e.
	 * @return true esetén használható a konfiguráció
	 */
	@Override
	public boolean isCorrect() {
		return getCAFile() != null && getCertFile() != null && getKeyFile() != null;
	}
	
	/**
	 * A szerver címét adja meg.
	 * Ha nincs beállítva, az android virtuális gépet futtató számítógép localhost címét adja vissza.
	 */
	@Override
	public String getAddress() {
		return PREFERENCES.getString("address", "10.0.2.2");
	}
	
	/**
	 * Megadja, hogy mely port legyen használva a kapcsolat kialakítására.
	 * Ha nincs beállítva, az alapértelmezett 8443 értékkel tér vissza.
	 */
	@Override
	public Integer getPort() {
		return Integer.parseInt(PREFERENCES.getString("port", "8443"));
	}
	
	/**
	 * A tanúsítvány jelszavát adja vissza.
	 * Ha nincs beállítva vagy nem kell jelszó a tanúsítványhoz, üres karaktertömb adódik vissza.
	 */
	@Override
	public char[] getPassword() {
		return PREFERENCES.getString("password", "").toCharArray();
	}
	
	/**
	 * A tanúsítvány kiállító kulcsfájlt adja vissza.
	 * @return null, ha a fájl nem létezik, vagy nincs beállítva
	 */
	@Override
	public File getCAFile() {
		return createFile("ca");
	}
	
	/**
	 * A tanúsítvány publikus kulcsfájlt adja vissza.
	 * @return null, ha a fájl nem létezik, vagy nincs beállítva
	 */
	@Override
	public File getCertFile() {
		return createFile("crt");
	}

	/**
	 * A tanúsítvány privát kulcsfájlt adja vissza.
	 * @return null, ha a fájl nem létezik, vagy nincs beállítva
	 */
	@Override
	public File getKeyFile() {
		return createFile("key");
	}

	/**
	 * Debuggoláshoz értelmes toString metódus.
	 */
	@Override
	public String toString() {
		return "Correct: " + isCorrect() +
				"; Address: " + getAddress() +
				"; Port: " + getPort() +
				"; CA: " + getCAFile() +
				"; CRT: " + getCertFile() +
				"; KEY: " + getKeyFile();
	}

	/**
	 * Készít egy File objektumot a beállítás kulcs alapján.
	 * Ha az nincs beállítva vagy nem létezik a fájl, null referencia adódik vissza.
	 */
	private File createFile(String key) {
		String path = PREFERENCES.getString(key, null);
		if (path == null) return null;
		File file = new File(path);
		if (!file.isFile()) return null;
		return file;
	}

	/**
	 * Az IP Webcam program szerverének a figyelő portját adja meg.
	 * Ha nincs beállítva, az alapértelmezett port a 8080-as.
	 */
	public String getCameraStreamPort() {
		return PREFERENCES.getString("cam_port", "8080");
	}
	
	/**
	 * Az IP Webcam program szerveréhez használt felhasználónév.
	 * Ha nincs beállítva, üres string, ami azt jelenti, nem szükséges bejelentkezni.
	 */
	public String getCameraStreamUser() {
		return PREFERENCES.getString("cam_user", "");
	}
	
	/**
	 * Az IP Webcam program szerveréhez használt jelszó.
	 * Ha nincs beállítva, üres string, csak azért, hogy ne lehessen NullPointerException.
	 * Ha a felhasználónév üres string, lényegtelen, hogy mi az értéke a jelszónak.
	 */
	public String getCameraStreamPassword() {
		return PREFERENCES.getString("cam_password", "");
	}
	
	/**
	 * A megváltozott szenzoradatok küldésének időköze.
	 */
	public int getRefreshInterval() {
		return Integer.parseInt(PREFERENCES.getString("refresh_interval", "1000"));
	}
	
}
