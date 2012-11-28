package org.dyndns.fzoli.rccar.host;

import static org.dyndns.fzoli.android.preference.FilePreference.getPath;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.dyndns.fzoli.android.preference.TextWatcherAdapter;
import org.dyndns.fzoli.socket.SSLSocketUtil;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.widget.Toast;

/**
 * A beállításokat megjelenítő és módosító osztály, mely a preferences.xml fájlt használja.
 * A deprecated metódusokat a 16-os API-tól a {@code PreferenceFragment} osztály tartalmazza, de annak használatához minimum 11-es API kell.
 * Azért, hogy a 7-es API-n is elfusson a program, a régebbi rendszereken is működő metódusokat használom.
 * @see android.preference.PreferenceFragment
 * @author zoli
 */
public class SettingActivity extends SherlockPreferenceActivity {
	
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
	private static final TextWatcher TW_PORT = createNumberMaskWatcher(65535);
	
	/**
	 * Nem engedi meg, hogy a beírt érték 0 alá vagy 1000 felé menjen, miközben gépel a felhasználó.
	 * Ha pozitív szám lett megadva, a fölösleges nullákat is eltávolítja.
	 */
	private static final TextWatcher TW_REFRESH = createNumberMaskWatcher(1000);
	
	/**
	 * Még mielőtt a megváltozott szöveg elmentődne, megnézi, megfelel-e az intervallumnak (1 - 65535), és ha nem felel meg, az adat a szerkesztés előtti marad.
	 */
	private final OnPreferenceChangeListener CL_PORT = createNumberMaskChangeListener(1, 65535);
	
	/**
	 * Még mielőtt a megváltozott szöveg elmentődne, megnézi, megfelel-e az intervallumnak (0 - 1000), és ha nem felel meg, az adat a szerkesztés előtti marad.
	 */
	private final OnPreferenceChangeListener CL_REFRESH = createNumberMaskChangeListener(0, 1000);
	
	/**
	 * A cím ellenőrzésére használt regex.
	 */
	private static final Pattern PT_ADDRESS = Pattern.compile("^[a-z\\d]{1}[\\w\\.\\d]{0,18}[a-z\\d]{1}$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * A felhasználónév és jelszó ellenőrzésére használt regex.
	 */
	private static final Pattern PT_LOGIN = Pattern.compile("^[a-z_.\\d]{0,20}$", Pattern.CASE_INSENSITIVE);
	
	/**
	 * Szöveg alapú bevitelimező validáláshoz segédosztály.
	 */
	private static abstract class StringWatcherAdapter extends TextWatcherAdapter {
    	
		/**
		 * Az előző szöveg.
		 * Arra kell, hogy vissza lehessen állítani a hibás szöveget.
		 */
		private String tmp;

		@Override
		public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
			tmp = s.toString(); // még mielőtt változik a szöveg, elmentődik a régi
		}

		@Override
		public final void afterTextChanged(Editable s) {
			String text = s.toString(); // miután megváltozott a szöveg
			if (skipValidate(text)) return; // ha a szöveg nem felel meg a követelményeknek, de később még megfelelhet, nincs ellenőrzés 
			if (!isValid(text)) {
				resetText(s); // ha nem felel meg a szöveg a követelményeknek, visszaállítás
			}
		}

		/**
		 * Ha a szöveg nem felel meg a követelményeknek, de még megfelelhet, true.
		 */
		protected boolean skipValidate(String text) {
			return false;
		}
		
		/**
		 * Ha a szöveg megfelel a követelményeknek, true.
		 */
		protected abstract boolean isValid(String text);
		
		/**
		 * Visszaállítja a szöveget az előző állapotra, ha a szöveg eltérő.
		 */
		private void resetText(Editable s) {
			if (!s.toString().equals(tmp)) s.replace(0, s.length(), tmp);
		}

	};
	
	/**
	 * Felhasználónév és jelszó ellenőrzésre.
	 */
	private static final TextWatcher TW_LOGIN = new StringWatcherAdapter() {

		@Override
		protected boolean isValid(String text) {
			return PT_LOGIN.matcher(text).matches();
		}
		
	};
	
	/**
	 * Csak azokat a karaktereket engedi meg leütni, melyek biztosan használhatóak a címben.
	 */
	private static final TextWatcher TW_ADDRESS = new StringWatcherAdapter() {

		@Override
		protected boolean skipValidate(String text) {
			return text.length() < 2 || text.endsWith(".");
		}

		@Override
		protected boolean isValid(String text) {
			return PT_ADDRESS.matcher(text).matches();
		}

	};
	
	/**
	 * Ha a bejelentkezési adat hibás, nem menti a módosulást.
	 */
	private final OnPreferenceChangeListener CL_LOGIN = new OnPreferenceChangeListener() {

		public boolean onPreferenceChange(Preference preference, Object newValue) {
			boolean ok = PT_LOGIN.matcher(newValue.toString()).matches();
			if (!ok) showWarning();
			return ok;
		};

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
	 * Az activity megnyitásakor aktuális fájlok értékei.
	 */
	private final Map<String, String> OLD_FILES = new HashMap<String, String>();
	
	/**
	 * Ez a metódus fut le, amikor létrejön az Activity.
	 * - Legenerálódik a nézet a preferences.xml fájl alapján.
	 * - A fájlkereső opciók címsora alatt megjelennek az aktuálisan beállított fájlok nevei.
	 */
	@Override
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true); // vissza nyíl az ikon mellé
		addPreferencesFromResource(R.xml.preferences);
		initEditTextPreferences();
		initFilePreferences();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish(); // vissza nyílra kattintva, activity bezárása
		}
		return super.onOptionsItemSelected(item);
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
			String old_path = OLD_FILES.get(key);
			if (path != null && old_path != null && !old_path.equals(path)) {
				SSLSocketUtil.clearClientCache();
			}
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
				OLD_FILES.put(key, path);
			}
		}
	}
	
	/**
	 * A szöveg alapú beviteli mezőkre "maszkot" állít be.
	 */
	private void initEditTextPreferences() {
		initEditTextPreference("address", TW_ADDRESS, CL_ADDRESS);
		initEditTextPreference("port", TW_PORT, CL_PORT);
		initEditTextPreference("refresh_interval", TW_REFRESH, CL_REFRESH);
		initEditTextPreference("cam_port", TW_PORT, CL_PORT);
		initEditTextPreference("cam_user", TW_LOGIN, CL_LOGIN);
		initEditTextPreference("cam_password", TW_LOGIN, CL_LOGIN);
	}
	
	/**
	 * Egy konkrét szöveg alapú beviteli mezőt "maszkol".
	 * @param key az EditTextPreference kulcsa
	 * @param tw a szövegváltozás-figyelő
	 * @param cl a preference változásfigyelő
	 */
	@SuppressWarnings("deprecation")
	private void initEditTextPreference(String key, TextWatcher tw, OnPreferenceChangeListener cl) {
		EditTextPreference etp = (EditTextPreference)findPreference(key);
		etp.getEditText().addTextChangedListener(tw);
		etp.setOnPreferenceChangeListener(cl);
	}
	
	/**
	 * Figyelmezteti a felhasználót, hogy hibás a bevitt adat.
	 */
	private void showWarning() {
		Toast.makeText(SettingActivity.this, R.string.wrong_value, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Ellenőrző eseményfigyelő gyártása szám alapú bevitelre.
	 * @param min minimum érték
	 * @param max maximum érték
	 */
	private OnPreferenceChangeListener createNumberMaskChangeListener(final int min, final int max) {
		return new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				try {
					int port = Integer.parseInt(newValue.toString());
					boolean ok = port >= min && port <= max;
					if (!ok) showWarning();
					return ok;
				}
				catch (NumberFormatException ex) {
					showWarning();
					return false;
				}
			}
			
		};
	}
	
	/**
	 * Maszkolást végző változásfigyelő gyártása szám alapú bevitelhez.
	 * A minimum érték mindig nulla.
	 * @param max a maximum érték
	 */
	private static TextWatcher createNumberMaskWatcher(final int max) {
		return new TextWatcherAdapter() {
			
			@Override
			public void afterTextChanged(Editable s) {
				try {
					int port = Integer.parseInt(s.toString());
					if (port > max) setText(s, Integer.toString(max));
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
	}
	
}
