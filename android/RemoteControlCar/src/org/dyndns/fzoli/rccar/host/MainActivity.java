package org.dyndns.fzoli.rccar.host;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import org.dyndns.fzoli.rccar.host.socket.UninspectedHostVideoProcess;

import android.annotation.SuppressLint;
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
	 * Feszültség-szint megjelenítéséhez debuggolás céljára.
	 */
	private TextView tvVoltage;
	
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
	 * A feszültség értéket két tizedes pontosságúra formázza.
	 */
	private static final DecimalFormat DF_VOLTAGE = new DecimalFormat("#.00");
	
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
		tvVoltage = (TextView) findViewById(R.id.tv_voltage);
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
	 * Ha az Activity megjelenik, megnézi, a szolgáltatás futása alatt keletkezett-e fatális hiba.
	 * Ha igen, akkor azonnal leállítja a szolgáltatás futását. Ezzel a fatális hiba is törlődik,
	 * így újra csak akkor lesz fatális hiba státusz, ha újra fatális hiba keletkezik a futás alatt.
	 * Fatális hibaüzenetre kattintva az Activity hívása történik meg, ezzel megjelenítve azt,
	 * így fatális hibaüzenetre kattintva végülis a szolgáltatás leáll, amint megjelenik a felület.
	 */
	@Override
	protected void onResume() {
		super.onResume();
		if (ConnectionService.isFatal()) setRunning(false);
	}
	
	/**
	 * Amikor a nézet megsemmisül, a szolgáltatással megszűnik a kapcsolat, de a szolgáltatás fut tovább.
	 * Ez maga után vonja azt is, hogy a Binder objektumból kikerül az eseménykezelő,
	 * így legközelebb amikor a felület újra regisztrálja eseménykezelőjét, friss adatokhoz fog jutni,
	 * mert az elmaradt eseményeket is megkapja utólag. Pl. folyamatban van-e a kapcsolódás a híddal
	 * Ha a szolgáltatás nem aktív, az ablak bezárásával az IP Webcam alkalmazás is bezárul.
	 * A tudatos felhasználó így egyszerre zárhatja be mindkét alkalmazást, de ha tudtában van annak, hogy
	 * hamarosan újra el fogja indítani a szolgáltatást, akkor dönthet úgy, hogy az Activity bezárása helyett
	 * egyszerűen csak háttérbe teszi azt és akkor nem zárul be az IP Webcam alkalmazás, így gyorsabb lesz a
	 * kapcsolódás a hídhoz és a felhasználónak se jön fel újra az IP Webcam Activity.
	 */
	@Override
	protected void onDestroy() {
		unbindService(false);
		if (!ConnectionService.isStarted(this) || ConnectionService.isSuspended()) {
			UninspectedHostVideoProcess.stopIPWebcamActivity(this);
		}
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
		
		if (binder.getHostData().isFullX()) { // ha az X tengely csak a végleteket engedi
			arrow.setPercentX(x > 0 ? 100 : x == 0 ? 0 : -100); // véglet beállítása
			x = arrow.getPercentX(); // százalékos érték frissítése
		}
		if (binder.getHostData().isFullY()) { // ugyan ez Y tengelyre...
			arrow.setPercentY(y > 0 ? 100 : y == 0 ? 0 : -100);
			y = arrow.getPercentY();
		}
		
		if (binder.getX() != x || binder.getY() != y) { // ha az előző érték egyike eltér
			binder.setXY(x, y, null); // vezérlőjel frissítése, nem küld üzenetet a Hídnak!
		}
		
		setXYText(x, y); // felületen megjelenő szöveg cseréje
		
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
		if (running) { // ha futást kértek
			if (ConnectionService.isOfflineMode(this) || config.isCorrect()) { // és jó a konfig
				refreshStartStop(running, save); // felület és változók frissítése
				disableButton(btStop, 1000); // a leállítás 1 másodpercre inaktív
				bindService(); // akkor kapcsolódás a szolgáltatáshoz és elindítása
			}
			else {
				if (!ConnectionService.isStarted(this)) { // ha rossz a konfig (vagy az adb inaktív) és nem fut a szolgáltatás
					Toast.makeText(this, /*config.isCorrect() ? R.string.set_adb : */R.string.set_config, Toast.LENGTH_SHORT).show(); // figyelmeztetés megjelenítése
					disableButton(btStart, 2000); // 2 másodperc az Android API-ban definiált rövid üzenet ideje, ennyi időre inaktív a start gomb
				}
				else { // ha rossz a konfig és fut a szolgáltatás (pl. valójában jók a beállítások, el is indult a service sikeresen, de aztán az USB háttértárat aktiválták és a telefon nem tudja olvasni az SD-kártyát)
					refreshStartStop(true, false); // felület és változók frissítése
				}
			}
		}
		else { // ha leállítást kértek
			refreshStartStop(running, save); // felület és változók frissítése
			unbindService(); // kapcsolat megszakítása és szolgáltatás leállítása
		}
	}
	
	/**
	 * A megadott gomb inaktiválása a megadott időre.
	 * @param bt a gomb
	 * @param time a megadott idő ezredmásodpercben
	 */
	private void disableButton(final Button bt, int time) {
		bt.setEnabled(false); // gomb disabled, amíg az idő le nem tellik
		TIMER_TOAST.schedule(new TimerTask() {
			
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						bt.setEnabled(true); // letelt az idő, a gomb újra fogad eseményt
					}
					
				});
			}
			
		}, time);
	}
	
	/**
	 * A szolgáltatás elindítása vagy leállítása előtt a felület módosítása és a szolgáltatás változóinak előkészítése.
	 * @param running true esetén indítás, egyébként leállítás kerül lefutásra a metódus után
	 * @param save true esetén frissül a beállításokban a service állapota
	 */
	private void refreshStartStop(boolean running, boolean save) {
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
	
	/**
	 * A szolgáltatás elindítása és kapcsolódás a szolgáltatáshoz.
	 * A kapcsolat kialakulása után eseménykezelő beállítása a felület naprakészsége érdekében.
	 * Ha megszakad a kapcsolat a szolgáltatással, magától újrakapcsolódik az Activity.
	 */
	@SuppressLint("InlinedApi")
	private void bindService() {
		unbindService(false); // kísérlet a szolgáltatás leválasztására, ha véletlen már van kialakított kapcsolat
		startService(new Intent(this, ConnectionService.class)); // szolgáltatás elindítása a kapcsolódás előtt
		conn = new ServiceConnection() {
			
			/**
			 * Ha a szolgáltatással megszűnik a kapcsolat, újrakapcsolódás, de csak akkor, ha nem kérték a leállást.
			 */
			@Override
			public void onServiceDisconnected(ComponentName n) {
				binder = null; // mivel már nincs kapcsolat, a binder használata nem megbízható
				if (conn != null) bindService(); // ha nem kérésre állt le a szolgáltatás, rekurzív újrahívás
			}
			
			/**
			 * Ha a szolgáltatással létrejött a kapcsolat:
			 * - offline állapot változó frissítése
			 * - felület újrarajzolása aktuális adatokkal
			 * - eseménykezelő regisztrálása
			 */
			@Override
			public void onServiceConnected(ComponentName n, IBinder b) {
				binder = (ConnectionBinder) b; // a binder mostantól használható
				offlineMode = ConnectionService.isOfflineMode(MainActivity.this); // offline mód van?
				repaintArrow(binder.getX(), binder.getY(), true, true); // újrarajzolás friss adatokkal
				binder.setListener(new ConnectionBinder.Listener() {
					
					/**
					 * Kapcsolódásjelző dialógus.
					 */
					private ProgressDialog dialogConn;
					
					/**
					 * Ha a vezérlőjel változott, újrarajzolja a grafikus komponensét.
					 */
					@Override
					public void onArrowChange(final int x, final int y) {
						runOnUiThread(new Runnable() { // a felület módosítása csak a felület szálán lehetséges
							
							@Override
							public void run() {
								repaintArrow(x, y, true, true); // a felület szálán újrarajzolás
							}
							
						});
					}
					
					/**
					 * Frissíti a feszültség-szintet a felületen.
					 */
					@Override
					public void onVoltageChanged(final Float voltage) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								setVoltageText(voltage);
							}
							
						});
					}
					
					@Override
					public void onConnectionStateChange(final boolean connecting) {
						runOnUiThread(new Runnable() { // itt nem lenne kötelező a felület szálát használni, de akkor aszinkron lenne és beragadhatna a dialógus
							
							@Override
							public void run() {
								if (connecting && dialogConn == null) { // ha kapcsolódás van
									// dialógus létrehozása és megjelenítése
									dialogConn = ProgressDialog.show(MainActivity.this, getString(R.string.title_connecting), getString(R.string.message_connecting), true, true, new DialogInterface.OnCancelListener() {
										
										/**
										 * Ha a dialógus ablakot megszüntetik a telefon vissza gombjának megnyomásával, a szolgáltatás leáll.
										 */
										@Override
										public void onCancel(DialogInterface dialog) {
											setRunning(false);
										}
											
									});
								}
								if (!connecting && dialogConn != null) { // ha eltüntetést kértek
									dialogConn.dismiss(); // dialógus megszüntetése ...
									dialogConn = null; // ... és referencia megszüntetése
								}
							}
						});
					}
					
				});
			}
			
		};
		// kapcsolódás a szolgáltatáshoz úgy, hogy a felületnél fontosabb prioritásban működjön, így memóriahiány esetén a felület hal meg előbb
		bindService(new Intent(this, ConnectionService.class), conn, Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);
	}
	
	/**
	 * Eseménykezelő levétele, kapcsolat megszakítása a szolgáltatással és annak leállítása.
	 */
	private void unbindService() {
		unbindService(true);
	}
	
	/**
	 * A feszültség-szint megjelenítése a felületen.
	 * Null paraméter esetén vagy leállított szolgáltatás esetén nem jelenik meg szöveg.
	 */
	private void setVoltageText(Float voltage) {
		tvVoltage.setText(binder == null || voltage == null ? "" : (DF_VOLTAGE.format(voltage) + " V"));
	}
	
	/**
	 * Eseménykezelő levétele, kapcsolat megszakítása a szolgáltatással és annak leállítása, ha kérik.
	 * @param stop true esetén leáll a service, egyébként fut tovább, csak a kapcsolat szűnik meg
	 */
	private void unbindService(boolean stop) {
		if (conn != null) { // ha van miről lekapcsolódni
			if (binder != null) { // ha regisztrálva van az eseménykezelő
				binder.setListener(null); // annak eltávolítása
				repaintArrow(0, 0, true, true); // alapállapotba helyezi a felületet
				binder = null; // jelzés a GC-nek és a programnak, hogy már nincs eseménykezelő
				setVoltageText(null); // feszültség-szint elrejtése
			}
			ServiceConnection tmp = conn; // a kapcsolat ideignlenes referenciája
			conn = null; // az osztályváltozó nullázása, hogy újra ne fusson le a metódus és jelzés a GC-nek
			unbindService(tmp); // kapcsolat megszakítása a szolgáltatással
		}
		if (stop) { // ha kérik, szolgáltatás leállítása
			stopService(new Intent(this, ConnectionService.class));
		}
	}
	
}
