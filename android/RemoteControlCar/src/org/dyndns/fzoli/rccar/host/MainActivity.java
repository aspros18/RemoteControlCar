package org.dyndns.fzoli.rccar.host;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

/**
 * Főablak.
 * Innen érhetőek el a beállítások, indítható ill. állítható le a program.
 * @author zoli
 */
public class MainActivity extends SherlockActivity {
	
	private static final int REQ_SETTING = 0;
	
	private Button btStart, btStop;
	private TextView tvX, tvY;
	private ArrowView arrow;
	
	private ConnectionBinder binder;
	private ServiceConnection conn;
	private boolean offlineMode;
	
	private Config config;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btStart = (Button) findViewById(R.id.bt_start);
		btStop = (Button) findViewById(R.id.bt_stop);
		arrow = (ArrowView) findViewById(R.id.arrow);
		tvX = (TextView) findViewById(R.id.tv_x);
		tvY = (TextView) findViewById(R.id.tv_y);
		config = ConnectionService.createConfig(this);
		
		setXYText(0, 0);
		
		setRunning(ConnectionService.isStarted(this) || false);
		
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
		
		arrow.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_UP) return repaintArrow(null, null, false, false);
				else return repaintArrow((int)e.getX(), (int)e.getY(), false, false);
			}
			
		});
	}
	
	@Override
	protected void onDestroy() {
		unbindService(false);
		super.onDestroy();
	}
	
	private void setXYText(int x, int y) {
		tvX.setText(Integer.toString(x));
		tvY.setText(Integer.toString(y));
	}
	
	private boolean repaintArrow(Integer mx, Integer my, boolean percent, boolean force) {
		if (mx == null || my == null) {
			if (binder == null) {
				return false;
			}
			arrow.setX(0);
			arrow.setY(0);
		}
		else {
			if (binder == null) {
				return false;
			}
			if (!force && (!offlineMode || !binder.getService().isVehicleConnected())) {
				return false;
			}
			if (percent) {
				arrow.setPercentX(mx);
				arrow.setPercentY(my);
			}
			else {
				arrow.setRelativeX(mx);
				arrow.setRelativeY(my);
			}
		}
		
		int x = arrow.getPercentX();
		int y = arrow.getPercentY();
		
		if (binder.isFullX()) {
			arrow.setPercentX(x > 0 ? 100 : x == 0 ? 0 : -100);
			x = arrow.getPercentX();
		}
		if (binder.isFullY()) {
			arrow.setPercentY(y > 0 ? 100 : y == 0 ? 0 : -100);
			y = arrow.getPercentY();
		}
		
		if (binder.getX() != x || binder.getY() != y) {
			binder.setX(x, false);
			binder.setY(y, false);
			setXYText(x, y);
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
				setRunning(false, false);
				startActivityForResult(new Intent(this, SettingActivity.class), REQ_SETTING);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			case REQ_SETTING:
				if (ConnectionService.isStarted(this)) setRunning(true);
		}
	}
	
	private void setRunning(boolean running) {
		setRunning(running, true);
	}
	
	private final Timer TIMER_TOAST = new Timer();
	
	private void setRunning(boolean running, boolean save) {
		boolean changed = true;
		if (running) {
			if (config.isCorrect()) {
				bindService();
			}
			else if (!ConnectionService.isStarted(this)) {
				changed = false;
				Toast.makeText(this, R.string.set_config, Toast.LENGTH_SHORT).show();
				btStart.setEnabled(false);
				TIMER_TOAST.schedule(new TimerTask() {
					
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								btStart.setEnabled(true);
							}
						});
					}
					
				}, 2000);
			}
		}
		else {
			unbindService();
		}
		if (changed) {
			btStart.setEnabled(!running);
			btStop.setEnabled(running);
			if (save) {
				ConnectionService.setSuspended(false);
				ConnectionService.setStarted(this, running);
			}
			else {
				ConnectionService.setSuspended(!running);
			}
		}
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
				offlineMode = ConnectionService.isOfflineMode(MainActivity.this);
				repaintArrow(binder.getX(), binder.getY(), true, true);
				binder.setListener(new ConnectionBinder.Listener() {
					
					private ProgressDialog dialog;
					
					@Override
					public void onArrowChange(final int x, final int y) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								repaintArrow(x, y, true, true);
							}
							
						});
					}
					
					@Override
					public void onConnectionStateChange(final boolean connecting) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								if (dialog != null) {
									dialog.dismiss();
								}
								if (connecting) {
									dialog = ProgressDialog.show(MainActivity.this, getString(R.string.title_connecting), getString(R.string.message_connecting), true, true, new DialogInterface.OnCancelListener() {
											
										@Override
										public void onCancel(DialogInterface dialog) {
											setRunning(false);
										}
											
									});
								}
								else {
									dialog = null;
								}
							}
						});
					}
					
				});
			}
			
		};
		bindService(new Intent(this, ConnectionService.class), conn, Context.BIND_IMPORTANT | Context.BIND_ABOVE_CLIENT);
	}
	
	private void unbindService() {
		unbindService(true);
	}
	
	private void unbindService(boolean stop) {
		if (conn != null) {
			if (binder != null) {
				binder.setListener(null);
				binder = null;
			}
			repaintArrow(0, 0, true, true);
			ServiceConnection tmp = conn;
			conn = null;
			unbindService(tmp);
			if (stop) stopService(new Intent(this, ConnectionService.class));
		}
	}
	
}
