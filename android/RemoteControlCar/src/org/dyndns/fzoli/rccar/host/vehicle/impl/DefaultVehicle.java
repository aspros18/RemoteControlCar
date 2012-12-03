package org.dyndns.fzoli.rccar.host.vehicle.impl;

import ioio.lib.api.DigitalOutput;
//import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

import org.dyndns.fzoli.rccar.host.ConnectionBinder;
import org.dyndns.fzoli.rccar.host.vehicle.AbstractVehicle;

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
	 * Konstruktor.
	 * @param binder a vezérlőjelet tartalmazó objektum referenciája
	 */
	public DefaultVehicle(ConnectionBinder binder) {
		super(binder);
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
		outFront = ioio_.openDigitalOutput(10, false);
		outBack = ioio_.openDigitalOutput(11, false);
		outLeft = ioio_.openDigitalOutput(12, false);
		outRight = ioio_.openDigitalOutput(13, false);
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
		Thread.sleep(20);
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