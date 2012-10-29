package org.dyndns.fzoli.rccar.host;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;

import org.dyndns.fzoli.android.ioio.IOIOSherlockActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Főablak.
 * Innen érhetőek el a beállítások, indítható ill. állítható le a program.
 * @author zoli
 */
public class MainActivity extends IOIOSherlockActivity {
	
	private Button btStart, btStop;
	private boolean running = false;
	
	private int mX = 0, mY = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		btStart = (Button) findViewById(R.id.bt_start);
		btStop = (Button) findViewById(R.id.bt_stop);
		btStart.setEnabled(true);
		btStop.setEnabled(false);
		btStart.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setRunning(true);
			}
			
		});
		btStop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setRunning(false);
			}
			
		});
		
		final TextView tvMessage = (TextView) findViewById(R.id.tv_message);
		final ArrowView arrow = (ArrowView) findViewById(R.id.arrow);
		tvMessage.setText("0 ; 0");
		arrow.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (!running) return false;
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
				
				arrow.setPercentX((x > 0 ? 100 : x == 0 ? 0 : -100));
				arrow.setPercentY((y > 0 ? 100 : y == 0 ? 0 : -100));
				x = arrow.getPercentX();
				y = arrow.getPercentY();
				
				if (mX != x || mY != y) {
					mX = x;
					mY = y;
					tvMessage.setText(x + " ; " + y);
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
				startActivity(new Intent(this, SettingActivity.class));
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setRunning(boolean b) {
		running = b;
		btStart.setEnabled(!b);
		btStop.setEnabled(b);
	}

	@Override
	public IOIOLooper createIOIOLooper(String connectionType, Object extra) {
		return new BaseIOIOLooper() {
			
			private DigitalOutput led_out;
			
			@Override
			protected void setup() throws ConnectionLostException, InterruptedException {
				led_out = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
			}
			
			/**
			 * Ez alapján arra jutottam, hogy ez a metódus, mint egy ciklus, állandóan ismétlődik, amíg van kapcsolat az IC-vel.
			 * A beépített LED, akkor világít, ha a digitális kimenetre logikai hamis van küldve, egyébként nem világít.
			 * Ezért, amikor megnyitom a digitális kimenetet, a kezdőérték true, hogy ne villágítson a led a loop metódus meghívása előtt.
			 * A LED világítása csak akkor változik meg a tesztben, ha nincs kanyarodás.
			 * Tehát a LED akkor kezd el világítani, ha nincs kanyarodás + teljes előre menet van,
			 * és akkor alszik el, ha nincs kanyarodás + nem teljes előre menet van.
			 */
			@Override
			public void loop() throws ConnectionLostException, InterruptedException {
				if (mX == 0) led_out.write(mY != 100);
				Thread.sleep(20);
			}
			
		};
	}

}
