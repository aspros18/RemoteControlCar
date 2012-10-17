package org.dyndns.fzoli.android.preference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.util.AttributeSet;

public class FilePreference extends Preference {

	/**
	 * Az XML fájlban megadott azonosító referenciája.
	 */
	private final int ID;
	
	public FilePreference(Context context) {
		this(context, null);
	}

	public FilePreference(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public FilePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ID = createId(attrs);
	}
	
	/**
	 * Kiolvassa az XML fájlból a megadott azonosítót.
	 * @return a megadott azonosító, vagy ha nincs megadva, akkor 0.
	 */
	private int createId(AttributeSet attrs) {
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
				// Edd meg.
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}
		return null;
	}
	
}
