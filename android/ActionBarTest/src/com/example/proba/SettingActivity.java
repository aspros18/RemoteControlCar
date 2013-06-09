package com.example.proba;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingActivity extends SherlockPreferenceActivity {
	
	@Override
	@TargetApi(11)
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			addPreferencesFromResource(R.xml.preferences);
		}
		else {
			getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
		}
	}
	
}
