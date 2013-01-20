package org.dyndns.fzoli.rccar.host.vehicle.impl;

import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

import org.dyndns.fzoli.rccar.host.ConnectionService;
import org.dyndns.fzoli.rccar.host.vehicle.AbstractVehicle;

/**
 * PWM alapú járművezérlő teszt.
 * Használatával nem csak logikai érték alapján vezérelhető a jármű,
 * hanem pontosan megadható a jármű sebessége és iránya.
 * Az osztály mivel teszt jellegű, nincs konkrét járműre hangolva.
 * A prototípus autó paramétereire van állítva jelenleg,
 * mert csak az az egy jármű áll rendlekezésemre.
 * Amint lesz a tulajdonomban egy komolyabb, gyors RC,
 * ezen osztály alapján megírom az arra optimalizált jármű osztályt.
 */
public class PWMVehicle extends AbstractVehicle {

	/**
	 * PWM kimenet a precíz vezérléshez.
	 */
	private PwmOutput outLeft, outRight, outFront, outBack;
	
	/**
	 * A jármű vezérléséhez használt láb azonosítója.
	 */
	protected int PIN_FRONT = 11, PIN_BACK = 10, PIN_LEFT = 13, PIN_RIGHT = 12, OUT_FREQ = 1000;
	
	/**
	 * Konstruktor.
	 * @param service szolgáltatás a vezérlőjelet tartalmazó objektum referenciájának eléréséhez
	 */
	public PWMVehicle(ConnectionService service) {
		super(service);
	}

	/**
	 * Megadja, hogy támogatva van-e a precíz kormányzás.
	 * @return a precíz vezérlés támogatott, ezért mindig false
	 */
	@Override
	public boolean isFullX() {
		return false;
	}

	/**
	 * Megadja, hogy támogatva van-e a precíz gázadás.
	 * @return a precíz vezérlés támogatott, ezért mindig false
	 */
	@Override
	public boolean isFullY() {
		return false;
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
	 * @return 2.7 V
	 */
	@Override
	public float getMaxVoltage() {
		return 2.7f;
	}
	
	/**
	 * Minimum feszültséghatár akkumulátor-szint becsélésre.
	 * @return 2.35 V
	 */
	@Override
	public float getMinVoltage() {
		return 2.35f;
	}
	
	/**
	 * A kapcsolat létrejötte után a PWM kimenetek megszerzése.
	 * A lábak használata:
	 * - 11-es, előremenet
	 * - 10-es, hátramenet
	 * - 13-es, balra kanyarodás
	 * - 12-mas jobbra kanyarodás
	 * A 10 és 11 valamint 12 és 13 soha nem lehet aktív egyszerre.
	 */
	@Override
	protected void setup() throws ConnectionLostException, InterruptedException {
		super.setup();
		outFront = ioio_.openPwmOutput(PIN_FRONT, OUT_FREQ);
		outBack = ioio_.openPwmOutput(PIN_BACK, OUT_FREQ);
		outLeft = ioio_.openPwmOutput(PIN_LEFT, OUT_FREQ);
		outRight = ioio_.openPwmOutput(PIN_RIGHT, OUT_FREQ);
	}
	
	/**
	 * A metódus, mint egy ciklus, állandóan ismétlődik, amíg van kapcsolat az IC-vel.
	 * Az aktuális vezérlőjel alapján beállítja a PWM kimeneteket és vár 20 ezredmásodpercet.
	 */
	@Override
	public void loop() throws ConnectionLostException, InterruptedException {
		handle(getX(), outLeft, outRight);
		handle(getY(), outBack, outFront);
		super.loop();
	}
	
	/**
	 * A jel alapján átváltja a két kimenetet úgy, hogy egy időben egyszerre a két kimenet soha nem aktív.
	 * @param sign a vezérlőjel, ami -100 és 100 között értelmezendő.
	 * @param outMinus az a digitális kimenet, mely a negatív vezérlőjel esetén aktív
	 * @param outPlus az a digitális kimenet, mely a nullánál nagyobb vezérlőjel esetén aktív
	 */
	private void handle(int sign, PwmOutput outMinus, PwmOutput outPlus) throws ConnectionLostException {
		if (sign == 0) { // nulla esetén mindkét kimenet logikai hamis és itt a sorrend is tetszőleges
			outMinus.setDutyCycle(0.0f);
			outPlus.setDutyCycle(0.0f);
		}
		else if (sign < 0) { // negatív szám esetén
			outPlus.setDutyCycle(0.0f); // elsőként a pozitív kimenet logikai hamis
			outMinus.setDutyCycle((float)(-1 * sign / 100.0)); // végül a negatív kimenet a jel alapján beállítódik
		}
		else { // pozitív szám esetén
			outMinus.setDutyCycle(0.0f); // elsőként a negatív kimenet logikai hamis
			outPlus.setDutyCycle((float)(sign / 100.0)); // végül a pozitív kimenet a jel alapján beállítódik
		}
	}
	
}
