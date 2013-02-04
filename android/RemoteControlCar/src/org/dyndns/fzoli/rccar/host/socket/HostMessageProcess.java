package org.dyndns.fzoli.rccar.host.socket;

import java.io.InvalidClassException;
import java.io.Serializable;
import java.util.Date;
import java.util.Iterator;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicle;
import org.dyndns.fzoli.rccar.model.Command;
import org.dyndns.fzoli.rccar.model.PartialBaseData;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.rccar.model.host.HostData.PointPartialHostData.PointData;
import org.dyndns.fzoli.rccar.model.host.HostData.PointPartialHostData.PointType;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;

/**
 * Üzenet küldő és fogadó.
 * A telefon GPS pozícióját, gyorsulását és a mágneses térerősségét figyeli és ha változás áll fenn,
 * üzenetet küld a hídnak a változásokról vagy változásról (attól függően, hogy egy vagy több adat változott).
 * A telefonon futó program nem foglalkozik azzal, miért van ezekre az adatokra szükség, mert a híd dolga az
 * adatok feldolgozása és a kinyert információ közlése az összes vezérlő kliensnek.
 * Az osztály az IOIO által vezérelt áramkör feszültség változását is figyeli és az akkumulátor százalékos
 * töltöttségét elküldi a hídnak. Ez azonban már feldolgozott adat, a híd egyszerűen továbbítja a vezérlő programoknak.
 * A program beállítások menüjében beállítható a frissítési időköz adatforgalom takarékosság céljából.
 * A frissítési időköz a telefon szenzoraira és az akkumulátor-szint változásra vonatkozik.
 */
public class HostMessageProcess extends MessageProcess {

	/**
	 * A szolgáltatás referenciája, hogy lehessen a változásról értesíteni.
	 */
	private final ConnectionService SERVICE;
	
	/**
	 * Ennyi időközönként küld a kliens adatmódosulásról jelzést.
	 */
	private final int REFRESH_INTERVAL;
	
	/**
	 * A már megfelelő GPS pozíció pontosság méterben megadva.
	 * Értéke: 50 méter
	 */
	private final float FINE_ACCURACY = 50;
	
	/**
	 * A mágneses-térerősség és a nehézségi-erő szenzorokhoz lehet vele hozzáférni.
	 */
	private final SensorManager sensorManager;
	
	/**
	 * A GPS-pozícióhoz lehet vele hozzáférni.
	 */
	private final LocationManager locationManager;
	
	/**
	 * Megadja, hogy a szenzor elérhető-e.
	 */
	private final boolean availableDirection, availableLocation;
	
	/**
	 * A mágneses-térerősség és a nehézségi-erő szenzorokhoz tartozó eseményfigyelő.
	 * Feladata, hogy közölje a szerverrel a naprakész szenzoradatokat.
	 */
	private final SensorEventListener sensorEventListener = new SensorEventListener() {

		/**
		 * A szenzoradatok pontossága nem érdekes.
		 */
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			;
		}

		/**
		 * Ha változott egy szenzoradat, frissíti a modelben azt és elküldi az üzenetet a Hídnak, ha itt az ideje.
		 */
		@Override
		public void onSensorChanged(SensorEvent event) {
			Point3D p = new Point3D(event.values[0], event.values[1], event.values[2]);
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				getHostData().setGravitationalField(p);
			}
			else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				getHostData().setMagneticField(p);
			}
			fireSensorChanged();
		}
		
	};

	/**
	 * Egy olyan GPS szolgáltató neve, ami ingyenes és megfelelő pontosságú.
	 */
	private String provider;
	
	/**
	 * Az utolsó GPS pozíció.
	 * API bugfix: Ahhoz kell, hogy megállapítható legyen, van-e még GPS jel.
	 */
	private Location mLastLocation;
	
	/**
	 * Az utolsó GPS pozíció beállításának ideje.
	 * API bugfix: Ahhoz kell, hogy megállapítható legyen, van-e még GPS jel.
	 */
	private long mLastLocationMillis;
	
	/**
	 * Az eseményfigyelő segítségével megállapítható, hogy van-e GPS jel.
	 * Az API erre régebben lehetőséget adott a {@link LocationListener} interfészben,
	 * de az újabb rendszereken már nem hívódik meg a {@link LocationListener#onStatusChanged(String, int, Bundle)} metódus.
	 */
	private final GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		
		/**
		 * Ha a GPS státusza megváltozott, lefut a metódus és eldönti, hogy van-e GPS jel,
		 * és beállítja, majd elküldi a hídnak az információt.
		 */
		@Override
		public void onGpsStatusChanged(int event) {
			switch (event) {
				case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
					Iterator<GpsSatellite> sats = locationManager.getGpsStatus(null).getSatellites().iterator();
					int count = 0;
					while (sats.hasNext()) {
						sats.next();
						count++;
					}
					Log.i(ConnectionService.LOG_TAG, "satellite count: " + count);
					if (count < 3) {
						sendUp2Date(false);
					}
					else if (mLastLocation != null) {
						boolean isGPSFix = SystemClock.elapsedRealtime() - mLastLocationMillis < 5000;
						if (!isGPSFix) {
							locationManager.requestLocationUpdates(provider, 1000, 10f, locationListener); // helyzetfrissítés újrahívása
							// sendUp2Date(false); // nincs rá szükség, mert a helyzetfrissítőben meghívódik és pontosság alapján dől el
						}
						else {
							sendUp2Date(mLastLocation.getAccuracy() <= FINE_ACCURACY);
						} 
					}
					else {
						sendUp2Date(false);
					}
					break;
				case GpsStatus.GPS_EVENT_FIRST_FIX:
//					sendUp2Date(true); // nem minden esetre igaz
					Log.i(ConnectionService.LOG_TAG, "GPS first fix");
					break;
			}
		}
		
	};
	
	
	
	/**
	 * A GPS-hez tartozó eseményfigyelő.
	 * Feladata, hogy közölje a szerverrel a jármű pozícióját és azt is, hogy naprakész-e a GPS adat.
	 */
	private final LocationListener locationListener = new LocationListener() {
		
		/**
		 * A GPS engedélyezése esetén nem kell tenni semmit, mert nem fontos jelzés.
		 */
		@Override
		public void onProviderEnabled(String provider) {
			Log.i(ConnectionService.LOG_TAG, "GPS enabled");
			gpsEnabled = true;
		}
		
		/**
		 * A GPS kikapcsolásakor menteni és jelezni kell, hogy a GPS adatok nem naprakészek.
		 */
		@Override
		public void onProviderDisabled(String provider) {
			Log.i(ConnectionService.LOG_TAG, "GPS disabled");
			gpsEnabled = false;
			sendUp2Date(false);
		}
		
		/**
		 * Ez a metódus hasznavehetetlen, mert Android 1.6 felett soha nem fut le.
		 * A metódust a {@link HostMessageProcess#gpsStatusListener} váltja le.
		 */
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
//			sendUp2Date(status == LocationProvider.AVAILABLE);
		}
		
		/**
		 * Ha a pozíció megváltozott, menti és jelzi azt a szervernek, ha itt az ideje.
		 * Mivel nem minden esetben fut le az {@link #onStatusChanged(String, int, Bundle)} metódus,
		 * ezért itt is üzenhet a kliens a hídnak az adatok naprakészségéről.
		 */
		@Override
		public void onLocationChanged(Location location) {
			if (location == null) return;
			// a gpsStatusListener-nek az alábbi változók beállítása
			mLastLocationMillis = SystemClock.elapsedRealtime();
			mLastLocation = location;
			
			Log.i(ConnectionService.LOG_TAG, "speed: " + (location.getSpeed() * 3.6) + " km/h" + "; accuracy: " + location.getAccuracy() + " m");
			
			// up2date frissítése és küldése, ha változott
			sendUp2Date(location.getAccuracy() <= FINE_ACCURACY);
			
			// sebesség, pozíció elmentése és üzenet küldése a Hídnak
			getHostData().setSpeed((double) location.getSpeed());
			getHostData().setGpsPosition(new Point3D(location.getLatitude(), location.getLongitude(), location.getAltitude()));
			fireSensorChanged();
		}
		
	};
	
	/**
	 * A járműhöz tartozó eseményfigyelő.
	 * Ha az akkumulátor-szint megváltozik, közli azt a szerverrel, ha itt az ideje.
	 */
	private final Vehicle.Callback vehicleCallback = new Vehicle.Callback() {
		
		/**
		 * Az utolsó üzenet elküldési ideje.
		 */
		private Date sendDate = null;
		
		/**
		 * Az akkumulátor-szint küldése a szervernek, ha itt az ideje.
		 */
		@Override
		public void onBatteryLevelChanged(Integer level) {
			Date now = new Date();
			if (loaded && (sendDate == null || now.getTime() - sendDate.getTime() >= REFRESH_INTERVAL)) {
				sendDate = now;
				sendMessage(new HostData.BatteryPartialHostData(level));
			}
		}
		
	};
	
	/**
	 * A szenzorok eseménykezelőit regisztráló szál.
	 * Azért, hogy a LocationListener regisztrálható legyen, olyan szálban kell a metódust futtatni, mely Looper thread.
	 * A HandlerThread egy egyszerű Looper Thread implementáció az Android API-ban.
	 * Amikor a szál megkezdi futását, létrejön a szálhoz a Looper és lefut az onLooperPrepared metódus, melyben a szenzorok
	 * eseménykezelői regisztrálódnak és ez után a Looper megkezdi futását, ami eredménye, hogy az események célba jutnak.
	 */
	private final HandlerThread sensorThread = new HandlerThread("sensor thread", android.os.Process.THREAD_PRIORITY_BACKGROUND) {
		
		protected void onLooperPrepared() {
			SERVICE.getVehicle().setCallback(vehicleCallback);
			if (availableLocation) {
				locationManager.addGpsStatusListener(gpsStatusListener);
				final Criteria criteria = new Criteria();
			    criteria.setCostAllowed(false);
			    criteria.setAccuracy(Criteria.ACCURACY_FINE);
			    provider = locationManager.getBestProvider(criteria, true);
				locationManager.requestLocationUpdates(provider, 1000, 10f, locationListener);
			}
			if (availableDirection) {
				Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		     	Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		     	sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		     	sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
			}
		};
		
	};
	
	/**
	 * Megadja, hogy a kezdeti szenzoradatok be lettek-e olvasva és el lett-e küldve az adatmodel a szervernek.
	 * Addig, amíg ez nem történik meg, a szenzoradatok módosulásáról nem küld jelzést a kliens.
	 */
	private boolean loaded;
	
	/**
	 * A legutolsó Point3D adatok elküldésének idejét adja meg ezredmásodpercben.
	 * Mivel már a 0. ezredmásodperc több éve elmúlt, nem okoz gondot a kezdeti 0 érték.
	 */
	private long fireTime;
	
	/**
	 * Az utóljára elküldött sebesség.
	 */
	private Double lastSpeed;
	
	/**
	 * Megadja, hogy a GPS-vevő be van-e kapcsolva.
	 */
	private boolean gpsEnabled;
	
	public HostMessageProcess(ConnectionService service, SecureHandler handler) {
		super(handler);
		SERVICE = service;
		REFRESH_INTERVAL = SERVICE.getConfig().getRefreshInterval();
		locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE);
		sensorManager = (SensorManager)service.getSystemService(Context.SENSOR_SERVICE);
		availableLocation = locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER);
		availableDirection = !sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty() && !sensorManager.getSensorList(Sensor.TYPE_GRAVITY).isEmpty();
		gpsEnabled = SERVICE.isGpsEnabled();
		Log.i(ConnectionService.LOG_TAG, "location supported: " + availableLocation + "; direction supported: " + availableDirection);
	}
	
	/**
	 * Amint kapcsolódott a hídhoz az alkalmazás, a szenzorok figyelése megkezdődik.
	 * Amint az akkumulátor-szint, mágneses-térerősség és nehézségi gyorsulás
	 * első adatai megérkeztek és beállítódtak a jármű modelében, a feldolgozó elküldi a jármű adatait.
	 * Ekkor a híd az online járművek listájába beteszi a jármű modelt a kezdőadatokkal.
	 * Ettől kezdve már csak a részadatok küldése szükséges a megadott időközönként.
	 * A regisztrált szenzorok eseményfigyelői továbbá is frissítik a helyi model adatait és amikor ideje küldeni,
	 * a szenzorok adatai el lesznek küldve, ha valamelyikük megváltozott.
	 * A telefon három szenzorának változásai függenek a frissítési időköztől és részmodelben küldődnek el.
	 * Az akkumulátor-szint változása viszont nem függ a frissítési időköztől, hanem amint a százalékos érték megváltozik,
	 * azonnal külön részmodelben jut el az információ a hídhoz.
	 */
	@Override
	protected void onStart() {
		loaded = false;
		sensorThread.start();
		while ((availableDirection && (getHostData().getGravitationalField() == null || getHostData().getMagneticField() == null)) || (getHostData().isVehicleConnected() != null && getHostData().isVehicleConnected() && getHostData().getBatteryLevel() == null)) {
			sleep(100);
		}
		SERVICE.getBinder().sendHostData(this);
		loaded = true;
	}
	
	/**
	 * Ha a kapcsolat bezárul a híddal, akkor nem kell tovább figyelni a szenzoradatok változását,
	 * ezért az eseménykezelők leregisztrálódnak, a szenzoradatok nullázódnak és a Looper Thread kilép.
	 */
	@Override
	protected void onStop() {
		sensorThread.getLooper().quit();
		SERVICE.getBinder().removeSender(this);
		SERVICE.getVehicle().setCallback(null);
		if (availableLocation) {
			locationManager.removeUpdates(locationListener);
			locationManager.removeGpsStatusListener(gpsStatusListener);
		}
		if (availableDirection) {
			sensorManager.unregisterListener(sensorEventListener);
		}
		getHostData().clear();
	}
	
	/**
	 * A híd által küldött üzenetek feldolgozása.
	 * Ha az üzenet a HostData részadata, akkor frissíti a HostData változóit.
	 * Ha parancs érkezett, átadja a parancsot a ConnectionBinder objektumnak.
	 * @param msg az üzenet
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void onMessage(Serializable msg) {
		if (msg instanceof PartialBaseData) {
			SERVICE.getBinder().updateHostData((PartialBaseData<HostData, ?>) msg);
		}
		else if (msg instanceof Command) {
			SERVICE.getBinder().onCommand((Command) msg);
		}
	}
	
	/**
	 * Ha üzenetküldés vagy fogadás közben hiba történik, ez a metódus fut le.
	 * Itt csak egyetlen hiba az említésre méltó:
	 * eltérő kliens és szerver verzió, tehát nem kompatibilis a kliens a szerverrel, nem lehet üzenni
	 * A szolgáltatás univerzális kapcsolódás hibakezelő metódusát hívja meg a kivételnek megfelelően.
	 * A hibakezelő metódus megjeleníti az értesítést a felületen és a hibától függően reagál.
	 * Az OTHER hibakategória esetén nem jelenik meg értesítés a felületen.
	 * Ha a hibakategórió fatális hiba, nem lesz megismételve a kapcsolódás.
	 */
	@Override
	protected void onException(Exception ex) {
		ConnectionError err = null;
		try {
			throw ex;
		}
		catch (InvalidClassException e) {
			Log.e(ConnectionService.LOG_TAG, "bridge is not compatible with this client", e);
			err = ConnectionError.WRONG_CLIENT_VERSION;
		}
		catch (Exception e) {
			err = ConnectionError.OTHER;
			Log.i(ConnectionService.LOG_TAG, "unknown error", e);
		}
		SERVICE.onConnectionError(err);
	}

	private HostData getHostData() {
		return SERVICE.getBinder().getHostData();
	}
	
	/**
	 * Menti és elküldi az up2date üzenetet a hídnak, ha a megadott érték eltér a jelenlegi értéktől.
	 */
	private void sendUp2Date(boolean up2date) {
		up2date &= gpsEnabled; // ha a gps nincs engedélyezve, biztos, hogy nincs jel
		if (getHostData().isUp2Date() != up2date) {
			getHostData().setUp2Date(up2date);
			Log.i(ConnectionService.LOG_TAG, "up2date: " + up2date);
			sendMessage(new HostData.BooleanPartialHostData(up2date, HostData.BooleanPartialHostData.BooleanType.UP_2_DATE));
		}
	}
	
	/**
	 * A megváltozott szenzoradatokat egyetlen üzenetbe tömörítve elküldi.
	 * A nem változott adatokra nem hoz létre egyetlen objektumot sem fölöslegesen, hanem megvizsgálja, mi változott / mi nem
	 * és az alapján létrehozza az üzenetet, amit el is küld a hídnak.
	 */
	private void fireSensorChanged() {
		final long time = new Date().getTime();
		if (loaded &&  time - fireTime >= REFRESH_INTERVAL) {
			fireTime = time;
			if (lastSpeed == null || !lastSpeed.equals(getHostData().getSpeed())) {
				lastSpeed = getHostData().getSpeed();
				if (lastSpeed != null) sendMessage(new HostData.SpeedPartialHostData(lastSpeed));
			}
			int change = 0;
			if (getHostData().getGravitationalField() != null && !getHostData().getGravitationalField().equals(getHostData().getPreviousGravitationalField())) {
				change += 1;
				getHostData().setGravitationalField(getHostData().getGravitationalField());
			}
			if (getHostData().getMagneticField() != null && !getHostData().getMagneticField().equals(getHostData().getPreviousMagneticField())) {
				change += 2;
				getHostData().setMagneticField(getHostData().getMagneticField());
			}
			if (getHostData().getGpsPosition() != null && !getHostData().getGpsPosition().equals(getHostData().getPreviousGpsPosition())) {
				change += 4;
				getHostData().setGpsPosition(getHostData().getGpsPosition());
			}
			switch (change) {
				case 1:
					sendMessage(new HostData.PointPartialHostData(
						new PointData(getHostData().getGravitationalField(), PointType.GRAVITATIONAL_FIELD)
					));
					break;
				case 2:
					sendMessage(new HostData.PointPartialHostData(
						new PointData(getHostData().getMagneticField(), PointType.MAGNETIC_FIELD)
					));
					break;
				case 3:
					sendMessage(new HostData.PointPartialHostData(
						new PointData(getHostData().getGravitationalField(), PointType.GRAVITATIONAL_FIELD),
						new PointData(getHostData().getMagneticField(), PointType.MAGNETIC_FIELD)
					));
					break;
				case 4:
					sendMessage(new HostData.PointPartialHostData(
						new PointData(getHostData().getGpsPosition(), PointType.GPS_POSITION)
					));
					break;
				case 5:
					sendMessage(new HostData.PointPartialHostData(
						new PointData(getHostData().getGravitationalField(), PointType.GRAVITATIONAL_FIELD),
						new PointData(getHostData().getGpsPosition(), PointType.GPS_POSITION)
					));
					break;
				case 6:
					sendMessage(new HostData.PointPartialHostData(
						new PointData(getHostData().getMagneticField(), PointType.MAGNETIC_FIELD),
						new PointData(getHostData().getGpsPosition(), PointType.GPS_POSITION)
					));
					break;
				case 7:
					sendMessage(new HostData.PointPartialHostData(
						new PointData(getHostData().getGravitationalField(), PointType.GRAVITATIONAL_FIELD),
						new PointData(getHostData().getMagneticField(), PointType.MAGNETIC_FIELD),
						new PointData(getHostData().getGpsPosition(), PointType.GPS_POSITION)
					));
			}
		}
	}
	
}
