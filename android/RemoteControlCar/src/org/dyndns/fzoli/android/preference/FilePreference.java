package org.dyndns.fzoli.android.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;

/**
 * Fájltallózó Preference komponens.
 * A tallózás befejeződésekor a kiválasztott fájl útvonala átadódik a tallózó alkalmazást előhívó Activitynek.
 * Az útvonal Uri formátuma String alapú teljes útvonallá alakítható a {@code getPath} segédmetódussal.
 * @author zoli
 */
public class FilePreference extends Preference {

	/**
	 * A megadott azonosító referenciája.
	 */
	private final int ID;
	
	/**
	 * Java kódból hívandó metódus.
	 * Az alapértelmezett azonosító kerül használatra.
	 */
	public FilePreference(Context context) {
		this(context, null);
	}

	/**
	 * Java kódból hívandó metódus.
	 * @param id a fájltallózó azonosítója
	 */
	public FilePreference(Context context, int id) {
		this(context, id, null, null);
	}
	
	/**
	 * XML fájlból, generáláskor hívandó metódus.
	 */
	public FilePreference(Context context, AttributeSet attrs) {
		this(context, attrs, null);
	}
	
	/**
	 * XML fájlból, generáláskor hívandó metódus.
	 */
	public FilePreference(Context context, AttributeSet attrs, Integer defStyle) {
		this(context, null, attrs, defStyle);
	}
	
	/**
	 * Összefoglaló konstruktor.
	 * Az ős inicializálódása után inicializálódik a komponens azonosítója.
	 */
	private FilePreference(Context context, Integer id, AttributeSet attrs, Integer defStyle) {
		super(context, attrs, defStyle == null ? 0 : defStyle);
		ID = createId(id, attrs);
	}
	
	/**
	 * Kiolvassa az XML fájlból a megadott azonosítót, ha nincs kézzel megadva.
	 * @param id a kézzel megadott id
	 * @param attrs az XML-ben megadott id
	 * @return a megadott azonosító, vagy ha nincs megadva, akkor 0.
	 */
	private int createId(Integer id, AttributeSet attrs) {
		if (id != null) return id;
		return attrs.getAttributeResourceValue("http://schemas.android.com/apk/res/android", "id", 0);
	}
	
	/**
	 * Megjeleníti a fájlkezelőt.
	 * Ha több fájlkezelő is van telepítve, listából lehet kiválasztani, melyik jelenjen meg.
	 */
	private void selectFile() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
		intent.setType("file/*");
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		if (ID != 0) try {
			((Activity) getContext()).startActivityForResult(Intent.createChooser(intent, getTitle()), ID);
		}
		catch (Exception ex) {
			;
		}
	}
	
	/**
	 * Amikor rákattintanak erre az opcióra, megjelenik a fájlböngésző.
	 */
	@Override
	protected void onClick() {
		super.onClick();
		if (isEnabled()) selectFile();
	}
	
	/**
	 * Segédmetódus azoknak az Activity osztályoknak, melyek használják ezt a Preference osztályt.
	 * A fájlböngészők által visszatért adat alapján megadja a fájl pontos útvonalát.
	 * @return a fájl útvonala
	 */
	public static String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;
			try {
				cursor = context.getContentResolver().query(uri, projection, null, null, null);
				int column_index = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index);
				}
			} catch (Exception e) {
				;
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}
	
}
