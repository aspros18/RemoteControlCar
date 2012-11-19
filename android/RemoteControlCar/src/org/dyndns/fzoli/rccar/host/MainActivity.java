package org.dyndns.fzoli.rccar.host;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Főablak.
 * Innen érhetőek el a beállítások, indítható ill. állítható le a program.
 * @author zoli
 */
public class MainActivity extends SherlockActivity {
	
	private Button btStart, btStop;
	private TextView tvMessage;
	private ArrowView arrow;
	
	private ConnectionBinder binder;
	private ServiceConnection conn;
	
	private static boolean wasRun = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btStart = (Button) findViewById(R.id.bt_start);
		btStop = (Button) findViewById(R.id.bt_stop);
		
		setRunning(wasRun || false);
		
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
		
		tvMessage = (TextView) findViewById(R.id.tv_message);
		arrow = (ArrowView) findViewById(R.id.arrow);
		
		tvMessage.setText("0 ; 0");
		
		arrow.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_UP) return repaintArrow(null, null);
				else return repaintArrow((int)e.getX(), (int)e.getY());
			}
			
		});
	}

	private boolean repaintArrow(Integer mx, Integer my) {
		if (binder == null) return false;
		if (mx == null || my == null) {
			arrow.setX(0);
			arrow.setY(0);
		}
		else {
			arrow.setRelativeX(mx);
			arrow.setRelativeY(my);
		}
		
		int x = arrow.getPercentX();
		int y = arrow.getPercentY();
		
		arrow.setPercentX((x > 0 ? 100 : x == 0 ? 0 : -100));
		//arrow.setPercentY((y > 0 ? 100 : y == 0 ? 0 : -100));
		x = arrow.getPercentX();
		//y = arrow.getPercentY();
		
		if (binder.getX() != x || binder.getY() != y) {
			binder.setX(x, false);
			binder.setY(y, false);
			tvMessage.setText(x + " ; " + y);
		}
		return true;
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
		if (b) bindService();
		else unbindService();		
		btStart.setEnabled(!b);
		btStop.setEnabled(b);
		wasRun = b;
	}
	
	private void bindService() {
		unbindService();
		startService(new Intent(this, ConnectionService.class));
		conn = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName n) {
				binder = null;
				if (conn != null) bindService();
			}
			
			@Override
			public void onServiceConnected(ComponentName n, IBinder b) {
				binder = (ConnectionBinder) b;
				binder.setListener(new ConnectionBinder.Listener() {
					
					@Override
					public void onChange(int x, int y) {
						repaintArrow(x, y);
					}
					
				});
			}
			
		};
		bindService(new Intent(this, ConnectionService.class), conn, Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);
	}
	
	private void unbindService() {
		if (conn != null) {
			if (binder != null) {
				binder.setListener(null);
				binder = null;
			}
			ServiceConnection tmp = conn;
			conn = null;
			unbindService(tmp);
			stopService(new Intent(this, ConnectionService.class));
		}
	}
	
}
