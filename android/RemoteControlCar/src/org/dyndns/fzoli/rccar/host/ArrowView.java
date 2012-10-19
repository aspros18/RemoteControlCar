package org.dyndns.fzoli.rccar.host;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class ArrowView extends View {

	private final int stroke = 2;
	
	private int s, s2, s10, s20, a, b;
	private int x = 0, y = 0;
	
	private Path borderPath;
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

	public int getPercentX() {
		return createPercent(x);
	}
	
	public int getPercentY() {
		return createPercent(y);
	}
	
	public void setX(int x) {
		if (x > a - stroke) x = a - stroke;
		if (x < -1 * a) x = -1 * a;
		this.x = x;
		invalidate();
	}
	
	public void setY(int y) {
		if (y > a - stroke) y = a - stroke;
		if (y < -1 * (a - stroke)) y = -1 * (a - stroke);
		this.y = y;
		invalidate();
	}
	
	public void setRelativeX(int x) {
        int s = x > s2 ? s10 + stroke - 1 : (2 * stroke) - 1;
        x = x + (-1 * (a - stroke) - s);
        if (!(x <= 0 ^ s != (2 * stroke) - 1)) x = 0;
        setX(x);
    }
	
	public void setRelativeY(int y) {
		int s = y > s2 ? s10 + stroke - 1 : stroke - 1;
        y = (a - stroke) - y + s;
        if (y <= 0 ^ s != stroke - 1) y = 0;
        setY(y);
    }
	
	private int createPercent(int i) {
        int s = 100 * i / (a - stroke);
        if (s > 100) s = 100;
        if (s < -100) s = -100;
        return s;
    }
	
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
	
	private void initView() {
		borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setColor(Color.BLACK);
		borderPaint.setStrokeWidth(stroke);
		
		mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mainPaint.setStyle(Paint.Style.FILL);
		mainPaint.setColor(Color.GREEN);
	}
	
	private void initPath() {
		s = getMeasuredWidth();
		s2 = s / 2;
		s10 = s / 10;
		s20 = s / 20;
		a = s2 - s20;
		b = s2 + s20;
		
		borderPath = new Path();
		borderPath.moveTo( stroke     , a          );
		borderPath.lineTo( a          , a          );
		borderPath.lineTo( a          , stroke     );
		borderPath.lineTo( b          , stroke     );
		borderPath.lineTo( b          , a          );
		borderPath.lineTo( s - stroke , a          );
		borderPath.lineTo( s - stroke , b          );
		borderPath.lineTo( b          , b          );
		borderPath.lineTo( b          , s - stroke );
		borderPath.lineTo( a          , s - stroke );
		borderPath.lineTo( a          , b          );
		borderPath.lineTo( stroke     , b          );
		borderPath.close();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawRect(a, a, b, b, mainPaint);
		if (x > 0) canvas.drawRect(b,  a, b + x, b, mainPaint);
		if (x < 0) canvas.drawRect(stroke + a + x,  a, a, b, mainPaint);
		if (y > 0) canvas.drawRect(a,  (a - stroke) - (y - stroke), b, a, mainPaint);
		if (y < 0) canvas.drawRect(a,  b, b, (s - stroke - ((a - stroke) + y)), mainPaint);
		canvas.drawPath(borderPath, borderPaint);
	}
	
}
