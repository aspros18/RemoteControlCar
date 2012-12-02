package org.dyndns.fzoli.rccar.host;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Főablak.
 * Innen érhetőek el a beállítások, indítható ill. állítható le a program.
 * @author zoli
 */
public class MainActivity extends SherlockActivity {
	
	/**
	 * A beállítások ablakot meghívó kérés azonosítója.
	 */
	private static final int REQ_SETTING = 0;
	
	/**
	 * A Toast eltűnésének jelzésére használatos időzítő.
	 * Ha a konfiguráció helytelen, a start gombra kattintva Toast üzenet jelzi,
	 * hogy nem indítható a szolgáltatás, mert nem teljes a konfiguráció.
	 * Amíg a Toast látható, a start gomb inaktív.
	 * Az újra aktiválásához kell az időzítő.
	 */
	private final Timer TIMER_TOAST = new Timer();
	
	/**
	 * Start/Stop gomb a service elindítására/leállítására.
	 */
	private Button btStart, btStop;
	
	/**
	 * A százalékban megadott vezérlőjel szöveges komponense.
	 */
	private TextView tvX, tvY;
	
	/**
	 * A százalékban megadott vezérlőjel grafikus komponense.
	 * Segítségével offline módban a jármű közvetlenül vezérelhető.
	 */
	private ArrowView arrow;
	
	/**
	 * A szolgáltatással teremtett kapcsolat.
	 * A Binder objektum ezen keresztül jut el a felülethez.
	 */
	private ServiceConnection conn;
	
	/**
	 * A Binder objektum.
	 * A felület üzenhet a szolgáltatásnak
	 * és a szolgáltatás eseményt küldhet a felületnek.
	 */
	private ConnectionBinder binder;
	
	/**
	 * Megadja, hogy offline mód van-e beállítva.
	 * A szolgáltatással való kapcsolatfelvétel után állítódik be.
	 */
	private boolean offlineMode;
	
	/**
	 * A hídhoz való kapcsolódáshoz szükséges paraméterek.
	 * Itt csak ellenőrzés cééjából használatos.
	 * Ha a konfiguráció helytelen, a szolgáltatás nem indítható el.
	 */
	private Config config;
	
	/**
	 * Amikor a felület létrejön, beállítódik a nézet,
	 * a nézet komponenseinek referenciái ismertté válnak,
	 * az alapbeállítás megtörténik és az eseménykezelők regisztrálódnak.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); // ős hívása elsőként, hogy az összes metódus használható legyen
		setContentView(R.layout.activity_main); // felület generálása XML fájlból
		// a felület komponenseinek referenciáinak megszerzése:
		btStart = (Button) findViewById(R.id.bt_start); 
		btStop = (Button) findViewById(R.id.bt_stop);
		arrow = (ArrowView) findViewById(R.id.arrow);
		tvX = (TextView) findViewById(R.id.tv_x);
		tvY = (TextView) findViewById(R.id.tv_y);
		config = ConnectionService.createConfig(this); // konfiguráció betöltése
		
		setXYText(0, 0); // kezdetben a jármű nem mozog és egyenesben áll a kormánya
		
		setRunning(ConnectionService.isStarted(this) || false); // a szolgáltatás elindítása, ha az el volt indítva és a felület frissítése
		
		btStart.setOnClickListener(new View.OnClickListener() {
			
			/**
			 * A szolgáltatás elindítása ha a start gombra kattintottak.
			 */
			@Override
			public void onClick(View v) {
				setRunning(true);
			}
			
		});
		
		btStop.setOnClickListener(new View.OnClickListener() {
			
			/**
			 * A szolgáltatás leállítása ha a stop gombra kattintottak.
			 */
			@Override
			public void onClick(View v) {
				setRunning(false);
			}
			
		});
		
		arrow.setOnTouchListener(new View.OnTouchListener() {
			
			/**
			 * Ha a felhasználó elengedte az érintőképernyőt, alapesetbe állítás,
			 * egyébként a koordináta alapján vezérlőjel beállítása.
			 * Ezek után újrarajzolás. (az eredeti funkció)
			 */
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_UP) return repaintArrow(null, null, false, false);
				else return repaintArrow((int)e.getX(), (int)e.getY(), false, false);
			}
			
		});
	}
	
	/**
	 * Amikor a nézet megsemmisül, a szolgáltatással megszűnik a kapcsolat.
	 * Ez maga után vonja azt is, hogy a Binder objektumból kikerül az eseménykezelő,
	 * így legközelebb amikor a felület újra regisztrálja eseménykezelőjét, friss adatokhoz fog jutni,
	 * mert az elmaradt eseményeket is megkapja utólag. Pl. folyamatban van-e a kapcsolódás a híddal
	 */
	@Override
	protected void onDestroy() {
		unbindService(false);
		super.onDestroy();
	}
	
	/**
	 * Az x és y érték szövegének frissítése.
	 */
	private void setXYText(int x, int y) {
		tvX.setText(Integer.toString(x));
		tvY.setText(Integer.toString(y));
	}
	
	/**
	 * A vezérlőjelet jelző komponens beállítása és újrarajzolása.
	 * @param mx az X érték, null esetén alapbeállítás
	 * @param my az Y érték, null esetén alapbeállítás
	 * @param percent true esetén az x és y érték százalékban értendő, egyébként "egér" koordináta.
	 * @param force true esetén ellenőrzés néékül beállítódik a két érték
	 */
	private boolean repaintArrow(Integer mx, Integer my, boolean percent, boolean force) {
		if (mx == null || my == null) { // ha alapbeállítás kell
			if (binder == null) {
				return false; // nincs min beállítani, kilépés
			}
			arrow.setX(0); // X és Y nulla
			arrow.setY(0);
		}
		else { // ha nem alapbeállítás kell
			if (binder == null) {
				return false; // nincs min beállítani, kilépés
			}
			if (!force && (!offlineMode || !binder.getService().isVehicleConnected())) {
				return false; // csak offline módban állítható és a járműnek is kapcsolódva kell lennie
			}
			if (percent) { // százalékos értékmegadás
				arrow.setPercentX(mx);
				arrow.setPercentY(my);
			}
			else { // egér pozíció alapú értékmegadás
				arrow.setRelativeX(mx);
				arrow.setRelativeY(my);
			}
		}
		
		// az újonan beállított értékek lekérése százalékban
		int x = arrow.getPercentX();
		int y = arrow.getPercentY();
		
		if (binder.isFullX()) { // ha az X tengely csak a végleteket engedi
			arrow.setPercentX(x > 0 ? 100 : x == 0 ? 0 : -100); // véglet beállítása
			x = arrow.getPercentX(); // százalékos érték frissítése
		}
		if (binder.isFullY()) { // ugyan ez Y tengelyre...
			arrow.setPercentY(y > 0 ? 100 : y == 0 ? 0 : -100);
			y = arrow.getPercentY();
		}
		
		if (binder.getX() != x || binder.getY() != y) { // ha az előző érték egyike eltér
			binder.setX(x, false); // vezérlőjel frissítése
			binder.setY(y, false);
			setXYText(x, y); // felületen megjelenő szöveg cseréje
		}
		
		return true; // sikeres beállítás
	}
	
	/**
	 * Amint hivatkoznak a menüre, az activity_main.xml fájl alapján létrejön és megjelenik.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * Ha a menüben a beállításokat választották, megjelenik a {@code SettingActivity}.
	 * @see SettingActivity
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_settings:
				setRunning(false, false);
				startActivityForResult(new Intent(this, SettingActivity.class), REQ_SETTING);
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * Ha visszatért a felhasználó a beállításokból, ha futott előtte a szolgáltatás, újból elindul.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQ_SETTING:
				if (ConnectionService.isStarted(this)) setRunning(true);
		}
	}
	
	/**
	 * Elindítja vagy leállítja a szolgáltatást.
	 */
	private void setRunning(boolean running) {
		setRunning(running, true);
	}
	
	/**
	 * Elindítja vagy leállítja a szolgáltatást.
	 * Kérésre elmenti a tényt, hogy a szolgáltatás el lett indítva vagy le lett állítva.
	 * Mindezek után frissíti a start és stop gombok állapotát.
	 * Ha a konfiguráció helytelen, a szolgáltatás nem indul el.
	 * Ha a konfigurációba nem kerül mentésre az új állapot, akkor a memóriában mentődik el
	 * úgynevezett felfüggesztett módba kerül a szolgáltatás, és ekkor úgy viselkedik, mint ha nem futna.
	 * @param running true esetén indítás, egyébként leállítás
	 * @param save true esetén frissül a beállításokban a service állapota
	 */
	private void setRunning(boolean running, boolean save) {
		boolean changed = true; // kezdetben pozitívan állok hozzá és azt mondom, sikerül a beállítás
		if (running) { // ha futást kértek
			if (config.isCorrect()) { // és jó a konfig
				bindService(); // akkor kapcsolódás a szolgáltatáshoz és elindítása
			}
			else if (!ConnectionService.isStarted(this)) { // ha rossz a konfig és nem fut a szolgáltatás
				changed = false; // nem változik a beállítás ez esetben
				Toast.makeText(this, R.string.set_config, Toast.LENGTH_SHORT).show(); // figyelmeztetés megjelenítése
				btStart.setEnabled(false); // start gomb disabled, amíg a figyelmeztetés látható
				TIMER_TOAST.schedule(new TimerTask() {
					
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								btStart.setEnabled(true); // letelt az idő, a start gomb újra fogad eseményt
							}
						});
					}
					
				}, 2000); // 2 másodperc az Android API-ban definiált rövid üzenet ideje
			}
		}
		else { // ha leállítást kértek
			unbindService(); // kapcsolat megszakítása és szolgáltatás leállítása
		}
		if (changed) { // ha változott a beállítás
			btStart.setEnabled(!running); // start és stop gomb állapotának frissítése
			btStop.setEnabled(running);
			if (save) { // ha kérték, hogy legyen mentés
				ConnectionService.setSuspended(false); // a szolgáltatás felfüggesztésének kikapcsolása, hogy újra működhessen
				ConnectionService.setStarted(this, running); // az új érték mentése
			}
			else { // ha nem kértek mentést, egyszerű felfüggesztés illetve annak feloldása
				ConnectionService.setSuspended(!running);
			}
		}
	}
	
	/**
	 * Kapcsolódás a szolgáltatáshoz és annak elindítása.
	 * A kapcsolat kialakulása után eseménykezelő beállítása a felület naprakészsége érdekében.
	 * Ha megszakad a kapcsolat a szolgáltatással, magától újrakapcsolódik az Activity.
	 */
	private void bindService() {
		unbindService();
		startService(new Intent(this, ConnectionService.class));
		conn = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName n) {
				binder = null;
				if (conn != null) bindService();
			}
			
			@Override
			public void onServiceConnected(ComponentName n, IBinder b) {
				binder = (ConnectionBinder) b;
				offlineMode = ConnectionService.isOfflineMode(MainActivity.this);
				repaintArrow(binder.getX(), binder.getY(), true, true);
				binder.setListener(new ConnectionBinder.Listener() {
					
					private ProgressDialog dialog;
					
					@Override
					public void onArrowChange(final int x, final int y) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								repaintArrow(x, y, true, true);
							}
							
						});
					}
					
					@Override
					public void onConnectionStateChange(final boolean connecting) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								if (connecting) {
									dialog = ProgressDialog.show(MainActivity.this, getString(R.string.title_connecting), getString(R.string.message_connecting), true, true, new DialogInterface.OnCancelListener() {
											
										@Override
										public void onCancel(DialogInterface dialog) {
											setRunning(false);
										}
											
									});
								}
								else {
									dialog = null;
								}
							}
						});
					}
					
				});
			}
			
		};
		bindService(new Intent(this, ConnectionService.class), conn, Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);
	}
	
	private void unbindService() {
		unbindService(true);
	}
	
	private void unbindService(boolean stop) {
		if (conn != null) {
			if (binder != null) {
				binder.setListener(null);
				binder = null;
			}
			repaintArrow(0, 0, true, true);
			ServiceConnection tmp = conn;
			conn = null;
			unbindService(tmp);
			if (stop) stopService(new Intent(this, ConnectionService.class));
		}
	}
	
}
