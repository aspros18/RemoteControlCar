package org.dyndns.fzoli.rccar.host;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Jármű vezérlőparancs megjelenítő nyíl.
 * Tesztelés idejére a jármű irányítására is használatos.
 * @author zoli
 */
public class ArrowView extends View {

	/**
	 * A vonalak vastagsága.
	 */
	private final int stroke = 2;
	
	/**
	 * Sokat használt méretek és koordináták.
	 */
	private int s, s2, s10, s20, a, b;
	
	/**
	 * Koordináta érték pixelben.
	 */
	private int x = 0, y = 0;
	
	/**
	 * A nyil útvonala.
	 */
	private Path borderPath;
	
	/**
	 * Ecset a rajzoláshoz.
	 */
	private Paint borderPaint, mainPaint;
	
	public ArrowView(Context context) {
		super(context);
		initView();
	}
	
	public ArrowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	public ArrowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}
	
	/**
	 * Az irány százalékban kifejezve.
	 */
	public int getPercentX() {
		return createPercent(x);
	}
	
	/**
	 * A sebesség százalékban kifejezve.
	 */
	public int getPercentY() {
		return createPercent(y);
	}
	
	/**
	 * Megadja a maximum pixelt az adott irányba.
	 * @param positive true, ha az irány pozitív
	 */
	private int getMax(boolean positive) {
		if (positive) return (int)(a - 1.5 * stroke);
		else return (int)(-1 * a + 1.5 * stroke);
	}
	
	/**
	 * A paramétert ellenőrzi és javítja azt, ha kell.
	 * A metódus nem engedi a maximum fölé a koordinátákat.
	 * @param i ellenőrizendő érték
	 * @return az ellenőrzött és javított koordináta
	 */
	private int chkMax(int i) {
		if (i == 0) return 0;
		if (i > getMax(true)) return getMax(true);
		if (i < getMax(false)) return getMax(false);
		return i;
	}
	
	/**
	 * Beállítja a pixelben megadott irányt és újrarajzolja a nyilat.
	 */
	public void setX(int x) {
		this.x = chkMax(x);
		invalidate();
	}
	
	/**
	 * Beállítja a pixelben megadott sebességet és újrarajzolja a nyilat.
	 */
	public void setY(int y) {
		this.y = chkMax(y);
		invalidate();
	}
	
	/**
	 * Beállítja a százalékban megadott irányt és újrarajzolja a nyilat.
	 */
	public void setPercentX(int x) {
		setX(fromPercent(x));
	}
	
	/**
	 * Beállítja a százalékban megadott sebességet és újrarajzolja a nyilat.
	 */
	public void setPercentY(int y) {
		setY(fromPercent(y));
	}
	
	/**
	 * Az egér pozíciója alapján kikalkulálja a pixelben megadott irányt és alkalmazza azt.
	 */
	public void setRelativeX(int x) {
		int s = x > s2 ? s10 : 0;
		x = x - a - s + 1;
		if (!(x <= 0 ^ s != 0)) x = 0;
		setX(x);
	}
	
	/**
	 * Az egér pozíciója alapján kikalkulálja a pixelben megadott sebességet és alkalmazza azt.
	 */
	public void setRelativeY(int y) {
		int s = y > s2 ? s10 : 0;
		y = a - y + s - 1;
		if (y <= 0 ^ s != 0) y = 0;
		setY(y);
	}
	
	/**
	 * Százalékból képez pixelt.
	 * @param i érték százalékban
	 * @return érték pixelben
	 */
	private int fromPercent(int i) {
		return (int) Math.round(getMax(true) * (i / 100.0));
	}
	
	/**
	 * Pixelből képez százalékot.
	 * @param i érték pixelben
	 * @return érték százalékban
	 */
	private int createPercent(int i) {
		return (int) Math.round(100 * i / (double) getMax(true));
	}
	
	/**
	 * A komponens méretének megállapítása és a hely lefoglalása.
	 * A komponens a felületen a lehető legnagyobb négyzetet foglalja le.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) { 
		int measuredWidth = measure(widthMeasureSpec);
		int measuredHeight = measure(heightMeasureSpec);
		
		int d = Math.min(measuredWidth, measuredHeight);
		setMeasuredDimension(d, d);
		initPath();
	}
	
	private int measure(int measureSpec) {
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.UNSPECIFIED) {
			// Alapértelmezett méret 200 pixel 
			return 200;
		}
		else {
			// A teljes hely kitöltése
			return specSize;
		}
	}
	
	/**
	 * Ecsetek inicializálása.
	 */
	private void initView() {
		borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // élsimítás bekapcsolva
		borderPaint.setStyle(Paint.Style.STROKE); // vonalat rajzol
		borderPaint.setColor(Color.BLACK); // fekete színű vonallal
		borderPaint.setStrokeWidth(stroke); // vonal vastagság beállítása
		
		mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // élsimítás bekapcsolva
		mainPaint.setStyle(Paint.Style.FILL); // teljes kitöltés
		mainPaint.setColor(Color.GREEN); // zöld színnel
	}
	
	/**
	 * Gyakran használt értékek beállítása és a nyíl megrajzolása.
	 */
	private void initPath() {
		s = getMeasuredWidth(); // a négyzet oldalainak teljes hossza
		s2 = s / 2; // a méret fele
		s10 = s / 10; // a méret tizede
		s20 = s / 20; // a méret huszada
		a = s2 - s20; // a középső négyzet bal felső koordinátája
		b = s2 + s20; // a középső négyzet jobb alsó koordinátája
		
		borderPath = new Path();
		borderPath.moveTo(stroke, a); // az első vonalat innen kezdi el húzni
		borderPath.lineTo(a, a); // az első vonal idáig húzódik és a következő innen kezdődik
		borderPath.lineTo(a, stroke);
		borderPath.lineTo(b, stroke);
		borderPath.lineTo(b, a);
		borderPath.lineTo(s - stroke, a);
		borderPath.lineTo(s - stroke, b);
		borderPath.lineTo(b, b);
		borderPath.lineTo(b, s - stroke);
		borderPath.lineTo(a, s - stroke);
		borderPath.lineTo(a, b);
		borderPath.lineTo(stroke, b);
		borderPath.close(); // az utolsó vonalvégződésből az első vonal kezdetébe húz egy vonalat, ezzel bezárul az útvonal
	}
	
	/**
	 * A komponens kirajzolása aktuális adatok alapján.
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(a, a, b, b, mainPaint); // a középső téglalap mindig látható
		if (x > 0) canvas.drawRect(b, a, b + x, b, mainPaint); // x tengelyen jobbra rajzol
		if (x < 0) canvas.drawRect(a + x, a, a, b, mainPaint); // x tengelyen balra rajzol
		if (y > 0) canvas.drawRect(a, a - y, b, a, mainPaint); // y tengelyen felfelé rajzol
		if (y < 0) canvas.drawRect(a, b, b, s - a - y, mainPaint); // y tengelyen lefelé rajzol
		canvas.drawPath(borderPath, borderPaint); // a kitöltés megrajzolása után a keret megrajzolása
	}
	
}
