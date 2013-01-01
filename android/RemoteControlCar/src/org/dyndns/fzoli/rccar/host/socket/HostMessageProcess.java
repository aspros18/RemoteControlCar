package org.dyndns.fzoli.rccar.host.socket;

import java.io.InvalidClassException;
import java.util.Date;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.ConnectionService.ConnectionError;
import org.dyndns.fzoli.rccar.host.vehicle.Vehicle;
import org.dyndns.fzoli.rccar.model.Point3D;
import org.dyndns.fzoli.rccar.model.host.HostData;
import org.dyndns.fzoli.rccar.model.host.HostData.PartialHostData;
import org.dyndns.fzoli.rccar.model.host.HostData.PointPartialHostData.PointData;
import org.dyndns.fzoli.rccar.model.host.HostData.PointPartialHostData.PointType;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.impl.MessageProcess;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.HandlerThread;
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
 * A frissítési időköz a telefon szenzoraira vonatkozik, az akkumulátor-szint változást nem érinti,
 * mivel annak változása nagyon lassú folyamat (legalább 1 perc), de a szenzoradatok másodpercenként többször változhatnak.
 */
public class HostMessageProcess extends MessageProcess {

	/**
	 * A szolgáltatás referenciája, hogy lehessen a változásról értesíteni.
	 */
	private final ConnectionService SERVICE;
	
	private final SensorManager sensorManager;
	private final LocationManager locationManager;
	
	private final boolean availableDirection, availableLocation;
	
	private final SensorEventListener sensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			;
		}

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

	private final LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onProviderEnabled(String provider) {
			;
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			sendUp2Date(false);
		}
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			sendUp2Date(status == LocationProvider.AVAILABLE);
		}
		
		@Override
		public void onLocationChanged(Location location) {
			getHostData().setGpsPosition(new Point3D(location.getLongitude(), location.getLatitude(), location.getAltitude()));
			fireSensorChanged();
		}
		
	};
	
	private final Vehicle.Callback vehicleCallback = new Vehicle.Callback() {
		
		@Override
		public void onBatteryLevelChanged(Integer level) {
			sendMessage(new HostData.BatteryPartialHostData(level));
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
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, locationListener);
			}
			if (availableDirection) {
				Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		     	Sensor magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		     	sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		     	sensorManager.registerListener(sensorEventListener, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
			}
		};
		
	};
	
	private boolean loaded;
	
	private long fireTime;
	
	public HostMessageProcess(ConnectionService service, SecureHandler handler) {
		super(handler);
		SERVICE = service;
		locationManager = (LocationManager) service.getSystemService(Context.LOCATION_SERVICE);
		sensorManager = (SensorManager)service.getSystemService(Context.SENSOR_SERVICE);
		availableLocation = locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER);
		availableDirection = !sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).isEmpty() && !sensorManager.getSensorList(Sensor.TYPE_GRAVITY).isEmpty();
		Log.i(ConnectionService.LOG_TAG, "location: " + availableLocation + "; direction: " + availableDirection);
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
		SERVICE.getVehicle().setCallback(null);
		if (availableLocation) locationManager.removeUpdates(locationListener);
		if (availableDirection) sensorManager.unregisterListener(sensorEventListener);
		getHostData().clear();
	}
	
	/**
	 * A híd által küldött üzenetek feldolgozása.
	 * Ha az üzenet a HostData részadata, akkor frissíti a HostData változóit.
	 * @param msg az üzenet
	 */
	@Override
	protected void onMessage(Object msg) {
		if (msg instanceof HostData.PartialHostData) {
			SERVICE.getBinder().updateHostData((PartialHostData<?>) msg);
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
	
	private void sendUp2Date(boolean up2date) {
		getHostData().setUp2Date(up2date);
		sendMessage(new HostData.BooleanPartialHostData(up2date, HostData.BooleanPartialHostData.BooleanType.UP_2_DATE));
	}
	
	/**
	 * A megváltozott szenzoradatokat egyetlen üzenetbe tömörítve elküldi.
	 * A nem változott adatokra nem hoz létre egyetlen objektumot sem fölöslegesen, hanem megvizsgálja, mi változott / mi nem
	 * és az alapján létrehozza az üzenetet, amit el is küld a hídnak.
	 */
	private void fireSensorChanged() {
		final long time = new Date().getTime();
		if (loaded &&  time - fireTime >= SERVICE.getConfig().getRefreshInterval()) {
			fireTime = time;
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
