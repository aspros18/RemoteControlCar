package org.dyndns.fzoli.rccar.host;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Főablak.
 * Innen érhetőek el a beállítások, indítható ill. állítható le a program.
 */
public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
