package org.dyndns.fzoli.rccar.host;

import java.io.File;

import android.content.SharedPreferences;

public class Config implements org.dyndns.fzoli.rccar.clients.ClientConfig {

	private final SharedPreferences PREFERENCES;
	
	public Config(SharedPreferences preferences) {
		PREFERENCES = preferences;
	}
	
	@Override
	public boolean isCorrect() {
		return false;
	}
	
	@Override
	public String getAddress() {
		return PREFERENCES.getString("address", "10.0.2.2");
	}
	
	@Override
	public Integer getPort() {
		return Integer.parseInt(PREFERENCES.getString("port", "8443"));
	}
	
	@Override
	public char[] getPassword() {
		return PREFERENCES.getString("password", "").toCharArray();
	}
	
	@Override
	public File getCAFile() {
		return createFile("ca");
	}
	
	@Override
    public File getCertFile() {
    	return createFile("crt");
	}
    
	@Override
    public File getKeyFile() {
    	return createFile("key");
    }
    
    private File createFile(String key) {
    	String path = PREFERENCES.getString(key, null);
    	if (path == null) return null;
    	File file = new File(path);
    	if (!file.isFile()) return null;
    	return file;
    }
    
}
