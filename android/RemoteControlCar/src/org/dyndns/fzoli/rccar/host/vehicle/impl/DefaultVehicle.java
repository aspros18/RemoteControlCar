package org.dyndns.fzoli.rccar.host.vehicle.impl;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.exception.ConnectionLostException;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.vehicle.AbstractVehicle;
//import ioio.lib.api.PwmOutput;

/**
 * A prototípus autó vezérlője.
 * Egyik irányba se támogatott a precíz vezérlés.
 * Minden olcsóbb távirányítós autó ugyan ezen a módon működik.
 * @author zoli
 */
public class DefaultVehicle extends AbstractVehicle {
	
//	/**
//	 * Teszt precíz vezérlésre.
//	 */
//	private PwmOutput pwm;
	
	/**
	 * Digitális kimenet a vezérléshez.
	 */
	private DigitalOutput outLeft, outRight, outFront, outBack;
	
	/**
	 * A jármű vezérléséhez használt láb száma.
	 */
	protected int PIN_FRONT = 10, PIN_BACK = 11, PIN_LEFT = 12, PIN_RIGHT = 13;
	
	/**
	 * Konstruktor.
	 * @param service szolgáltatás a vezérlőjelet tartalmazó objektum referenciájának eléréséhez
	 */
	public DefaultVehicle(ConnectionService service) {
		super(service);
	}
	
	/**
	 * Megadja, melyik lábat kell használni a feszültségméréshez.
	 * Az akkumulátor-szint mérése a 33-as lábon történik.
	 */
	@Override
	public int getBatteryPin() {
		return 33;
	}
	
	/**
	 * Maximum feszültséghatár akkumulátor-szint becsélésre.
	 */
	@Override
	public float getMaxVoltage() {
		return 5.0f;
	}
	
	/**
	 * Minimum feszültséghatár akkumulátor-szint becsélésre.
	 */
	@Override
	public float getMinVoltage() {
		return 1.2f;
	}

	/**
	 * Megadja, hogy támogatva van-e a precíz kormányzás.
	 * @return nem támogatott a precíz vezérlés, ezért mindig true
	 */
	@Override
	public boolean isFullX() {
		return true;
	}

	/**
	 * Megadja, hogy támogatva van-e a precíz gázadás.
	 * @return nem támogatott a precíz vezérlés, ezért mindig true
	 */
	@Override
	public boolean isFullY() {
		return true;
	}
	
	/**
	 * A kapcsolat létrejötte után a digitális kimenetek megszerzése.
	 * A lábak használata:
	 * - 10-es, előremenet
	 * - 11-es, hátramenet
	 * - 12-es, balra kanyarodás
	 * - 13-mas jobbra kanyarodás
	 * A 10 és 11 valamint 12 és 13 soha nem lehet aktív egyszerre.
	 */
	@Override
	protected void setup() throws ConnectionLostException, InterruptedException {
		super.setup();
		outFront = ioio_.openDigitalOutput(PIN_FRONT, false);
		outBack = ioio_.openDigitalOutput(PIN_BACK, false);
		outLeft = ioio_.openDigitalOutput(PIN_LEFT, false);
		outRight = ioio_.openDigitalOutput(PIN_RIGHT, false);
//		pwm = ioio_.openPwmOutput(14, 1000);
	}
	
	/**
	 * A metódus, mint egy ciklus, állandóan ismétlődik, amíg van kapcsolat az IC-vel.
	 * Az aktuális vezérlőjel alapján beállítja a digitális kimeneteket és vár 20 ezredmásodpercet.
	 */
	@Override
	public void loop() throws ConnectionLostException, InterruptedException {
		handle(getX(), outLeft, outRight);
		handle(getY(), outBack, outFront);
//		pwm.setDutyCycle((float)(getY() / 100.0));
		super.loop();
	}
	
	/**
	 * A jel alapján átváltja a két kimenetet úgy, hogy egy időben egyszerre a két kimenet soha nem aktív.
	 * @param sign a vezérlőjel, ami -100 és 100 között értelmezendő. Jelen esetben -100, 0 és 100 lehet.
	 * @param outMinus az a digitális kimenet, mely a negatív vezérlőjel esetén aktív
	 * @param outPlus az a digitális kimenet, mely a nullánál nagyobb vezérlőjel esetén aktív
	 */
	private void handle(int sign, DigitalOutput outMinus, DigitalOutput outPlus) throws ConnectionLostException {
		if (sign == 0) { // nulla esetén mindkét kimenet logikai hamis és itt a sorrend is tetszőleges
			outMinus.write(false);
			outPlus.write(false);
		}
		else if (sign < 0) { // negatív szám esetén
			outPlus.write(false); // elsőként a pozitív kimenet logikai hamis
			outMinus.write(true); // végül a negatív kimenet logikai igaz
		}
		else { // pozitív szám esetén
			outMinus.write(false); // elsőként a negatív kimenet logikai hamis
			outPlus.write(true); // végül a pozitív kimenet logikai igaz
		}
	}
	
}
