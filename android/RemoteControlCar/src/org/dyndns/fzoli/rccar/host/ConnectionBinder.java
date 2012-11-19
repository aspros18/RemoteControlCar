package org.dyndns.fzoli.rccar.host;

import android.os.Binder;

public class ConnectionBinder extends Binder {
	
	public static interface Listener {
		public void onChange(int x, int y);
	}
	
	private int mX = 0, mY = 0;
	
	private Listener mListener;
	
	public int getX() {
		return mX;
	}
	
	public int getY() {
		return mY;
	}
	
	public void setX(int x) {
		setX(x, true);
	}
	
	public void setY(int y) {
		setY(y, true);
	}
	
	public void setX(int x, boolean fire) {
		mX = x;
		fireListener(fire);
	}
	
	public void setY(int y, boolean fire) {
		mY = y;
		fireListener(fire);
	}
	
	private void fireListener(boolean fire) {
		if (fire && isListener()) mListener.onChange(mX, mY);
	}
	
	public boolean isListener() {
		return mListener != null;
	}
	
	public void setListener(Listener listener) {
		mListener = listener;
	}
	
}
