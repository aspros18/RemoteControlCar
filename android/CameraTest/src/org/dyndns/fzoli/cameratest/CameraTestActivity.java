package org.dyndns.fzoli.cameratest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

//class Preview extends SurfaceView implements SurfaceHolder.Callback {
//	
//	private Camera camera;
//	
//	public Preview(Context context) {
//		super(context);
//		getHolder().addCallback(this);
//		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//	}
//	
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
////		camera = Camera.open();
//		camera = com.pas.webcam.utils.l.a(getContext());
//		try {
//			File dir = new File("/mnt/sdcard/test/");
//			if (!dir.isDirectory()) dir.mkdirs();
////			camera.setDisplayOrientation(90);
//			camera.setPreviewDisplay(holder);
//			camera.setPreviewCallback(new Camera.PreviewCallback() {
//				
//				@Override
//				public void onPreviewFrame(byte[] data, Camera camera) {
//					try {
//						byte[] ret = com.pas.webcam.utils.YuvImage.Compress(data, 320, 240, 30);
//						Log.i("CameraPreview", "size: "+ret);
//					}
//					catch (Exception ex) {
//						Log.i("CameraPreview", "compasserr", ex);
//					}
////					Date d1 = new Date();
////					Camera.Parameters parameters = camera.getParameters();
////			        Size size = parameters.getPreviewSize();
////			        YuvImage image = new YuvImage(data, parameters.getPreviewFormat(), size.width, size.height, null);
////			        try { d1 = new Date();
////					Camera.Parameters parameters = camera.getParameters();
////			        Size size = parameters.getPreviewSize();
////			        YuvImage image = new YuvImage(data, parameters.getPreviewFormat(), size.width, size.height, null);
////			        try {
////			        	ByteArrayOutputStream storage = new ByteArrayOutputStream();
////			        	image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, storage);
////			        	Date d2 = new Date();
////			        	Log.i("CameraPreview", "image created " + (d2.getTime() - d1.getTime()));
////			        	data = storage.toByteArray();
////			        	storage.close();
////			        	
////			        	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
////			        	Bitmap img = Bitmap.createScaledBitmap(bitmap, 320, 240, true);
////			        	bitmap.recycle();
////			        	Date d3 = new Date();
////			        	Log.i("CameraPreview", "image resized " + (d3.getTime() - d2.getTime()));
////			        	
////			        	File file = new File(String.format("/mnt/sdcard/test/%d.jpg", System.currentTimeMillis()));
////			        	FileOutputStream fileout = new FileOutputStream(file);
////			        	img.compress(CompressFormat.JPEG, 20, fileout);
////			        	Log.i("CameraPreview", "image wrote " + (new Date().getTime() - d3.getTime()));
////					}
////					catch (Exception e) {
////						e.printStackTrace();
////					}
////					Preview.this.invalidate();
////			        	ByteArrayOutputStream storage = new ByteArrayOutputStream();
////			        	image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 100, storage);
////			        	Date d2 = new Date();
////			        	Log.i("CameraPreview", "image created " + (d2.getTime() - d1.getTime()));
////			        	data = storage.toByteArray();
////			        	storage.close();
////			        	
////			        	Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
////			        	Bitmap img = Bitmap.createScaledBitmap(bitmap, 320, 240, true);
////			        	bitmap.recycle();
////			        	Date d3 = new Date();
////			        	Log.i("CameraPreview", "image resized " + (d3.getTime() - d2.getTime()));
////			        	
////			        	File file = new File(String.format("/mnt/sdcard/test/%d.jpg", System.currentTimeMillis()));
////			        	FileOutputStream fileout = new FileOutputStream(file);
////			        	img.compress(CompressFormat.JPEG, 20, fileout);
////			        	Log.i("CameraPreview", "image wrote " + (new Date().getTime() - d3.getTime()));
////					}
////					catch (Exception e) {
////						e.printStackTrace();
////					}
////					Preview.this.invalidate();
//				}
//				
//			});
//		}
//		catch (Exception ex) {
//			Log.i("CameraPreview", "error", ex);
//		}
//	}
//	
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
//		Camera.Parameters parameters = camera.getParameters();
//		parameters.setPreviewSize(w, h);
////		parameters.setPictureFormat(ImageFormat.JPEG);
////		parameters.setPictureSize(320, 240);
////		parameters.setJpegQuality(30);
//		parameters.setPreviewFpsRange(20, 33);
//		camera.setParameters(parameters);
//		camera.startPreview();
//	}
//	
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		camera.stopPreview();
//		camera = null;
//	}
//	
//}

/**
 * Ez az osztály paraméterül kap egy Socketet, amit fel kell dolgoznia egy külön szálban.
 * Mind a kliens, mind a szerver ezt az osztályt használja adat fogadására és küldésére.
 */
abstract class AbstractProcess implements Runnable {
    
    /**
     * A kommunikációt lebonyolító socket.
     */
    private final Socket s;
    
    public AbstractProcess(Socket s) {
        this.s = s;
    }

    /**
     * @return kommunikációt lebonyolító socket
     */
    protected Socket getSocket() {
        return s;
    }

}

/**
 * A kliens oldali socket feldolgozó.
 * A szerverhez kapcsolódás után elkéri a kapcsolatazonosítót és konzolra kijelzi.
 * Ez után kapcsolódik egy jelszóvédett HTTP szerverhez, ami localhoston fut és továbbítja az MJPEG streamet a szerverhez.
 */
class MJPEGClientProcess extends AbstractProcess {

    /**
     * @param s A kommunikációt lebonyolító socket.
     */
    public MJPEGClientProcess(Socket s) {
        super(s);
    }

    /**
     * A külön szálban ez a metódus fut le.
     */
    @Override
    public void run() {
        try {
            InputStream in = getSocket().getInputStream(); // bemenet megszerzése
            int pid = in.read(); // kapcsolatazonosító megszerzése a szervertől
            System.out.println("Connection ID: " + pid);
            OutputStream out = getSocket().getOutputStream(); // kimenet megszerzése
            
            HttpURLConnection conn = (HttpURLConnection) new URL("http://127.0.0.1:8080/videofeed").openConnection(); //kapcsolat objektum létrehozása
            conn.setRequestMethod("GET"); // GET metódus beállítása

            // név és jelszó beállítása
            String userpass = "fzoli" + ":" + "asdfgh";
            
            String basicAuth = "Basic " + new String(android.util.Base64.encode(userpass.getBytes(), android.util.Base64.DEFAULT));
            conn.setRequestProperty ("Authorization", basicAuth);

            // most, hogy minden be van állítva, kapcsolódás
            conn.connect();
            InputStream urlin = conn.getInputStream(); // mjpeg stream megszerzése

            try {
                int length;
                byte[] buffer = new byte[2048]; // 2 kilóbájt buffer gyors FPS érdekében
                while((length = urlin.read(buffer)) != -1) { // addig amíg van adat, olvasás
                    out.write(buffer, 0, length); // a teljes bufferelt adat elküldése
                }
            }
            catch (Exception ex) { // bármi hiba van az MJPEG stream közben, nem érdekel
            	Log.i("test", "err", ex);
            }

            in.close(); // kapcsolat lezárása
            out.close(); 
        }
        catch (Exception ex) {
            ex.printStackTrace(); // megint csak sok sok hiba lehetséges, amiről már jó tudni
        }
    }

}

/**
 * @author zoli
 */
abstract class AbstractMJPEGClient {
    
    /**
     * A kapcsolat megteremtéséhez kliens oldali socket.
     */
    private final Socket s;
    
    /**
     * MJPEG socket kliens.
     * @param port a szerver portja
     */
    public AbstractMJPEGClient(int port) throws Exception {
        s = createSocket(port);
    }
    
    /**
     * A socket kliens futtatása.
     */
    public void run() {
        new Thread(new MJPEGClientProcess(s)).start(); // feldolgozás új szálban
    }
    
    public void interrupt() throws IOException {
    	s.close();
    }
    
    /**
     * Socket létrehozása és kapcsolódás a szerverhez.
     * @param port szerver portja
     */
    protected abstract Socket createSocket(int port) throws Exception;
    
}

/**
 * A titkosítatlan kliens.
 */
class MJPEGClient extends AbstractMJPEGClient {

    /**
     * MJPEG socket kliens.
     * @param port a szerver portja
     */
    public MJPEGClient(int port) throws Exception {
        super(port);
    }

    /**
     * Titkosítatlan Socket létrehozása és kapcsolódás a szerverhez.
     * @param port szerver portja
     */
    @Override
    protected Socket createSocket(int port) throws Exception {
        return new Socket("192.168.10.1", port);
    }
    
}

public class CameraTestActivity extends Activity {
    
	MJPEGClient client;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
//    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//        Preview preview = new Preview(this);
//        ((FrameLayout) findViewById(R.id.preview)).addView(preview);
        try {
			client = new MJPEGClient(12345);
			client.run();
		}
        catch (Exception e) {
        	Log.i("test", "err", e);
		}
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	try {
			client.interrupt();
		}
    	catch (Exception e) {
    		Log.i("test", "err", e);
		}
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
}