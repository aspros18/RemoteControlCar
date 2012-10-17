package org.dyndns.fzoli.rccar.host;

import static org.dyndns.fzoli.android.preference.FilePreference.getPath;

import java.io.File;
import java.util.regex.Pattern;

import org.dyndns.fzoli.android.preference.TextWatcherAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.widget.Toast;

/**
 * A beállításokat megjelenítő és módosító osztály, mely a preferences.xml fájlt használja.
 * A deprecated metódusokat a 16-os API-tól a {@code PreferenceFragment} osztály tartalmazza, de annak használatához minimum 11-es API kell.
 * Azért, hogy a 7-es API-n is elfusson a program, a régebbi rendszereken is működő metódusokat használom.
 * @see android.preference.PreferenceFragment
 */
public class SettingActivity extends PreferenceActivity {
	
	/**
	 * A preferences.xml fájlban megadott id és kulcs párokat tartalmazó felsorolás. 
	 */
	private static final SparseArray<String> KEYS = new SparseArray<String>() {
		{
			put(R.id.file_ca, "ca");
			put(R.id.file_crt, "crt");
			put(R.id.file_key, "key");
		}
	};
	
	/**
	 * Nem engedi meg, hogy a beírt érték 0 alá vagy 65535 felé menjen, miközben gépel a felhasználó.
	 * Ha pozitív szám lett megadva, a fölösleges nullákat is eltávolítja.
	 */
	private static final TextWatcher TW_PORT = new TextWatcherAdapter() {
		
		@Override
		public void afterTextChanged(Editable s) {
			try {
			    int port = Integer.parseInt(s.toString());
			    if (port > 65535) setText(s, "65535");
			    if (port < 0) setText(s, "0");
			    if (port != 0 && s.toString().startsWith("0")) setText(s, Integer.toString(port));
			}
			catch (NumberFormatException ex) {
				setText(s, "0");
			}
		}
		
		private void setText(Editable s, CharSequence cs) {
			s.replace(0, s.length(), cs);
		}
		
	};
	
	/**
	 * Még mielőtt a megváltozott szöveg elmentődne, megnézi, megfelel-e az intervallumnak (1 - 65535), és ha nem felel meg, az adat a szerkesztés előtti marad.
	 */
	private final OnPreferenceChangeListener CL_PORT = new OnPreferenceChangeListener() {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			try {
				int port = Integer.parseInt(newValue.toString());
				boolean ok = port >= 1 && port <= 65535;
				if (!ok) showWarning();
				return ok;
			}
			catch (NumberFormatException ex) {
				showWarning();
				return false;
			}
		}
		
	};
	
	/**
	 * A cím ellenőrzésére használt regex.
	 */
	private static final Pattern PT_ADDRESS = Pattern.compile("^[a-z\\d]{1}[\\w\\.\\d]{0,18}[a-z\\d]{1}$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Csak azokat a karaktereket engedi meg leütni, melyek biztosan használhatóak.
	 */
	private static final TextWatcher TW_ADDRESS = new TextWatcherAdapter() {
    	
		/**
		 * Az előző szöveg.
		 * Arra kell, hogy vissza lehessen állítani a hibás szöveget.
		 */
		private String tmp;

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			tmp = s.toString(); // még mielőtt változik a szöveg, elmentődik a régi
		}

		@Override
		public void afterTextChanged(Editable s) {
			String text = s.toString(); // miután megváltozott a szöveg
			if (text.length() < 2 || text.endsWith(".")) return; // ha a szöveg nem felel meg a követelményeknek, de később még megfelelhet, nincs ellenőrzés 
			if (!PT_ADDRESS.matcher(text).matches()) {
				resetText(s); // ha nem felel meg a szöveg a követelményeknek, visszaállítás
			}
		}

		/**
		 * Visszaállítja a szöveget az előző állapotra, ha a szöveg eltérő.
		 */
		private void resetText(Editable s) {
			if (!s.toString().equals(tmp)) s.replace(0, s.length(), tmp);
		}

	};

	/**
	 * Ha a cím beírása félbeszakad, előfordulhat, hogy hibás az érték.
	 * Ha az érték nem megfelelő, a szöveg nem módosul és figyelmeztetve lesz a felhasználó.
	 */
	private final OnPreferenceChangeListener CL_ADDRESS = new OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			boolean ok = PT_ADDRESS.matcher(newValue.toString()).matches();
			if (!ok) showWarning();
			return ok;
		};

	};

	/**
	 * Ez a metódus fut le, amikor létrejön az Activity.
	 * - Legenerálódik a nézet a preferences.xml fájl alapján.
	 * - A fájlkereső opciók címsora alatt megjelennek az aktuálisan beállított fájlok nevei.
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		initEditTextPreferences();
		initFilePreferences();
	}
	
	/**
	 * Ez a metódus fut le, amikor a fájlkereső ablak bezárul.
	 * Ha volt fájl kiválasztva, akkor a {@code data} változó tartalmazza azt,
	 * a változás megjelenik az Activity felületén és az új útvonal mentésre kerül.
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			String key = KEYS.get(requestCode);
			String path = getPath(this, data.getData());
			getPreferenceManager().getSharedPreferences().edit().putString(key, path).commit();
			showFileName(key, path);
		}
	}
	
	/**
	 * Frissíti a fájlnevet, ha a fájl létezik.
	 * @param key a preference kulcsa
	 * @param path a fájl útvonala 
	 */
	@SuppressWarnings("deprecation")
	private void showFileName(String key, String path) {
		File f = new File(path);
		if (f.exists()) findPreference(key).setSummary(f.getName());
	}
	
	/**
	 * Végig megy az összes FilePreference kulcson, lekéri a fájl útvonalakat és megjeleníti a fájl nevét.
	 */
	@SuppressWarnings("deprecation")
	private void initFilePreferences() {
		for (int i = 0; i < KEYS.size(); i++) {
			String key = KEYS.get(KEYS.keyAt(i));
			String path = getPreferenceManager().getSharedPreferences().getString(key, null);
			if (path != null) {
				showFileName(key, path);
			}
		}
	}
	
	/**
	 * A szöveg alapú beviteli mezőkre "maszkot" állít be.
	 */
	@SuppressWarnings("deprecation")
	private void initEditTextPreferences() {
		EditTextPreference etpPort = (EditTextPreference)findPreference("port");
		etpPort.getEditText().addTextChangedListener(TW_PORT);
		etpPort.setOnPreferenceChangeListener(CL_PORT);
		EditTextPreference etpAddress = (EditTextPreference)findPreference("address");
		etpAddress.getEditText().addTextChangedListener(TW_ADDRESS);
		etpAddress.setOnPreferenceChangeListener(CL_ADDRESS);
	}
	
	/**
	 * Figyelmezteti a felhasználót, hogy hibás a bevitt adat.
	 */
	private void showWarning() {
		Toast.makeText(SettingActivity.this, R.string.wrong_value, Toast.LENGTH_SHORT).show();
	}
	
}
