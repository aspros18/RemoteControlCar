package org.dyndns.fzoli.socket.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.dyndns.fzoli.socket.JpegProvider;

/**
 * Megosztott MJPEG streamelő.
 * Mindegyik objektum ugyan azt a képkocka objektumot használja.
 * @author zoli
 */
public class SharedJpegProvider extends JpegProvider {

    /**
     * A megosztott képkockákat tároló map.
     */
    private static final Map<String, byte[]> FRAMES = Collections.synchronizedMap(new HashMap<String, byte[]>());
    
    /**
     * Konstruktor.
     * @param key a folyam azonosító
     * @param out az MJPEG kimenő folyam
     */
    public SharedJpegProvider(String key, OutputStream out) {
        super(key, out);
    }

    /**
     * A JPEG képkocka adatát adja vissza bájt tömbben.
     * Mindegyik objektumhoz ugyan az a képkocka referencia tartozik.
     * @param key a folyam azonosító
     */
    @Override
    protected byte[] getFrame(String key) {
        return FRAMES.get(key);
    }

    /**
     * A JPEG képkocka adatát állítja be.
     * Mindegyik objektumhoz ugyan az a képkocka referencia tartozik.
     * @param key a folyam azonosító
     * @param frame a képkocka adata bájt tömbben
     */
    @Override
    protected void setFrame(String key, byte[] frame) {
        setSharedFrame(key, frame);
    }
    
    /**
     * A JPEG képkocka adatát állítja be.
     * @param key a folyam azonosító
     * @param frame a képkocka adata bájt tömbben
     */
    public static void setSharedFrame(String key, byte[] frame) {
        FRAMES.put(key, frame);
    }
    
    /**
     * MJPEG folyamot küld a kimenetre.
     * @param key a folyam azonosító
     * @param out a kimenet
     */
    public static void handleConnection(String key, OutputStream out) throws Exception {
        new SharedJpegProvider(key, out).handleConnection();
    }
    
    /**
     * Teszt szál készítése.
     */
    private static void createTest() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    new SharedJpegProvider("", new ByteArrayOutputStream() {

                        @Override
                        public void write(byte[] b) throws IOException {
                            System.out.print(new String(b));
                            super.write(b);
                        }
                        
                    }).handleConnection();
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
        }).start();
    }
    
    /**
     * Teszt.
     */
    public static void main(String[] args) throws Exception {
        createTest();
        Thread.sleep(3000);
        SharedJpegProvider.setSharedFrame("", "a".getBytes());
        createTest();
        Thread.sleep(1000);
        SharedJpegProvider.setSharedFrame("", "a".getBytes());
        Thread.sleep(2000);
        SharedJpegProvider.setSharedFrame("", "b".getBytes());
        Thread.sleep(1000);
        SharedJpegProvider.setSharedFrame("", null);
        Thread.sleep(2000);
        SharedJpegProvider.setSharedFrame("", "b".getBytes());
        System.exit(0);
    }
    
}
