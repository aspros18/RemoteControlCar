package com.ramdroid.adbtoggle.accesslib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

/** A library to access the USB debug settings using the ADB Toggle app. */
public class AdbToggleAccess {

	private static final String TAG = "AdbToggleAccess";
	
	private static final String ENABLE_USB_DEBUG = "com.ramdroid.adbtoggle.ENABLE_USB_DEBUG";
	private static final String DISABLE_USB_DEBUG = "com.ramdroid.adbtoggle.DISABLE_USB_DEBUG";
	private static final String USB_DEBUG_STATUS = "com.ramdroid.adbtoggle.USB_DEBUG_STATUS";
	
	//private static final String PERMISSION_ADB_TOGGLE = "ramdroid.permission.ADB_TOGGLE";
	
	/** An interface to get status results from ADB Toggle. */
	public interface OnAdbToggleListener {
		/** Says if USB debug settings have been toggled successfully.
		 * 
		 * @param successful True if toggled successfully.
		 */
		public void onFinished(boolean successful);
	}
	
	private Context mContext;
	private OnAdbToggleListener mListener;
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
    	
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "unregisterReceiver");
			mContext.unregisterReceiver(mReceiver);
			if (mListener != null) {
				mListener.onFinished(intent.getBooleanExtra("successful", false));
			}
		}
    	
    };
    
    /**
     * This function does not really install ADB Toggle! It will 
     * only guide the user to the Google Play download page.
     * 
     * @param context Context of your activity/service/...
     */
    public static void install(Context context) {
		Uri uri = Uri.parse("market://details?id=com.ramdroid.adbtoggle");  
		Intent it = new Intent(Intent.ACTION_VIEW, uri);  
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it); 
    }
    
    /** Do a rudimentary check to see if ADB Toggle is installed.
     * 
     * This function does not test if ADB Toggle is installed as system app.
     * It does only check if the app is available on the device.
     * If ADB Toggle is not installed then use the function install() to
     * guide the user to the Google Play download page.
     * 
	 * @param context Context of your activity/service/...
     * @return True if installed.
     */
    public static boolean isInstalled(Context context) {
    	return isAvailable(context);
    }
    
    /** Check if USB debug settings are enabled.
     * 
     * @param context Context of your activity/service/...
     * @return True if enabled.
     */
    public static boolean isEnabled(Context context) {
    	boolean isEnabled = true;
		String Result = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ADB_ENABLED);
		Log.d(TAG, "ADB_ENABLED " + Result);
		if (Result == null || !Result.equals("1")) {
			isEnabled = false;
		}
		return isEnabled;
    }
	
    /** Turn on USB debug settings.
     * 
     * The listener is optional but highly recommended if you need USB debug settings to be enabled in your next step!
     * 
     * @param context Context of your activity/service/...
     * @param listener Receive status result. .
     * @return True if succeeded.
     */
	public boolean enable(Context context, OnAdbToggleListener listener) {
		mContext = context;	
		mListener = listener;
		return toggle(true);
	}
	
	/** Turn off USB debug settings.
	 * 
	 * The listener is optional but highly recommended if you need USB debug settings to be disabled in your next step!
	 * 
	 * @param context Context of your activity/service/...
	 * @param listener Receive status result.
	 * @return True if succeeded.
	 */
	public boolean disable(Context context, OnAdbToggleListener listener) {
		mContext = context;
		mListener = listener;
		return toggle(false);
	}
	
	private boolean toggle(boolean state) {
		boolean result = isAvailable(mContext);
		if (result) {
			// create receiver to read response
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(USB_DEBUG_STATUS);  

	        Log.d(TAG, "registerReceiver");
	        mContext.registerReceiver(mReceiver, filter);
	        
	        // send toggle command
			Intent i = new Intent(state ? ENABLE_USB_DEBUG : DISABLE_USB_DEBUG);
			mContext.sendBroadcast(i);//, PERMISSION_ADB_TOGGLE);
		}
		
		return result;
	}

	private static boolean isAvailable(Context context) {
		boolean result = true;//checkPermission(context);
		if (result) {
			// is AdbToggle installed?
			PackageManager pm = context.getPackageManager();
			Intent i = pm.getLaunchIntentForPackage("com.ramdroid.adbtoggle");
			result = (i != null);
		}
		return result;
	}
	
	/*
	private static boolean checkPermission(Context context) {
		boolean result = (context.checkCallingOrSelfPermission(PERMISSION_ADB_TOGGLE) == PackageManager.PERMISSION_GRANTED );
		Log.d(TAG, "PERMISSION_ADB_TOGGLE" + (result ? " " : " not ") + "granted!");
		return result;
	}
	*/
}