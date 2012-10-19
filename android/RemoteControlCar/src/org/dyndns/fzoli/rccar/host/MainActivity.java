package org.dyndns.fzoli.rccar.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

/**
 * Főablak.
 * Innen érhetőek el a beállítások, indítható ill. állítható le a program.
 */
public class MainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final ArrowView arrow = (ArrowView) findViewById(R.id.arrow);
		arrow.setOnTouchListener(new View.OnTouchListener() {
			
			private int mX = 0, mY = 0;
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_UP) {
					arrow.setX(0);
					arrow.setY(0);
				}
				else {
					arrow.setRelativeX((int)e.getX());
					arrow.setRelativeY((int)e.getY());
				}
				int x = arrow.getPercentX();
				int y = arrow.getPercentY();
				if (mX != x || mY != y) {
					mX = x;
					mY = y;
					Log.i("test", x + ";" + y);
				}
				return true;
			}
			
		});
	}

	/**
	 * Amint hivatkoznak a menüre, az activity_main.xml fájl alapján létrejön és megjelenik.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
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
				startActivity(new Intent(this, SettingActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}

}
