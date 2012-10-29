package org.dyndns.fzoli.android.ioio;

import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.IOIOLooperProvider;
import ioio.lib.util.android.IOIOAndroidApplicationHelper;
import android.content.Intent;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * A convenience class for easy creation of IOIO-based activities.
 * 
 * It is used by creating a concrete {@link Activity} in your application, which
 * extends this class. This class then takes care of proper creation and
 * abortion of the IOIO connection and of a dedicated thread for IOIO
 * communication.
 * <p>
 * The client should extend this class and implement
 * {@link #createIOIOLooper(String, Object)}, which should return an implementation of the
 * {@link IOIOLooper} interface.
 * (The first argument provided will contain the
 * connection class name, such as ioio.lib.impl.SocketIOIOConnection for a
 * connection established over a TCP socket (which is used over ADB). The second
 * argument will contain information specific to the connection type. For
 * example, in the case of {@link SocketIOIOConnection}, the second argument
 * will contain an {@link Integer} representing the local port number.)
 * In this implementation, the client implements
 * the {@link IOIOLooper#setup(ioio.lib.api.IOIO)} method, which gets called as
 * soon as communication with the IOIO is established, and the
 * {@link IOIOLooper#loop()} method, which gets called repetitively as long as
 * the IOIO is connected.
 * <p>
 * In addition, the {@link IOIOLooper#disconnected()} method may be overridden
 * in order to execute logic as soon as a disconnection occurs for whichever
 * reason. The {@link IOIOLooper#incompatible()} method may be overridden in
 * order to take action in case where a IOIO whose firmware is incompatible with
 * the IOIOLib version that application is built with.
 * <p>
 * In a more advanced use case, more than one IOIO is available. In this case, a
 * thread will be created for each IOIO, whose semantics are as defined above.
 * @author zoli
 */
public abstract class IOIOSherlockActivity extends SherlockActivity implements IOIOLooperProvider {
	
	private final IOIOAndroidApplicationHelper helper_ = new IOIOAndroidApplicationHelper(this, this);
	
	/**
	 * Subclasses should call this method from their own onCreate() if
	 * overloaded. It takes care of connecting with the IOIO.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		helper_.create();
	}
	
	/**
	 * Subclasses should call this method from their own onDestroy() if
	 * overloaded. It takes care of connecting with the IOIO.
	 */
	@Override
	protected void onDestroy() {
		helper_.destroy();
		super.onDestroy();
	}
	
	/**
	 * Subclasses should call this method from their own onStart() if
	 * overloaded. It takes care of connecting with the IOIO.
	 */
	@Override
	protected void onStart() {
		super.onStart();
		helper_.start();
	}
	
	/**
	 * Subclasses should call this method from their own onStop() if overloaded.
	 * It takes care of disconnecting from the IOIO.
	 */
	@Override
	protected void onStop() {
		helper_.stop();
		super.onStop();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_NEW_TASK) != 0) {
			helper_.restart();
		}
	}
	
	/**
	 * Subclasses must either implement this method or its other overload by
	 * returning an implementation of {@link IOIOLooper}. A dedicated thread
	 * will be created for each available IOIO, from which the
	 * {@link IOIOLooper}'s methods will be invoked. <code>null</code> may be
	 * returned if the client is not interested to create a thread for this
	 * IOIO.
	 * 
	 * @return An implementation of {@link IOIOLooper}, or <code>null</code> to
	 *         skip.
	 * @see BaseIOIOLooper
	 */
	@Override
	public abstract IOIOLooper createIOIOLooper(String connectionType, Object extra);
	
}
