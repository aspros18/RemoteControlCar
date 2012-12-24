package org.dyndns.fzoli.socket.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.dyndns.fzoli.socket.JpegProvider;

/**
 * Megosztott MJPEG streamelő.
 * Mindegyik objektum ugyan azt a képkocka objektumot használja.
 * @author zoli
 */
public class SharedJpegProvider extends JpegProvider {

    /**
     * Megosztott képkocka.
     */
    private static byte[] frame;
    
    /**
     * Konstruktor.
     * @param out az MJPEG kimenő folyam
     */
    public SharedJpegProvider(OutputStream out) {
        super(out);
    }

    /**
     * A JPEG képkocka adatát adja vissza bájt tömbben.
     * Mindegyik objektumhoz ugyan az a képkocka referencia tartozik.
     */
    @Override
    protected byte[] getFrame() {
        return frame;
    }

    /**
     * A JPEG képkocka adatát állítja be.
     * Mindegyik objektumhoz ugyan az a képkocka referencia tartozik.
     * @param frame a képkocka adata bájt tömbben
     */
    @Override
    protected void setFrame(byte[] frame) {
        setSharedFrame(frame);
    }
    
    /**
     * A JPEG képkocka adatát állítja be.
     * @param frame a képkocka adata bájt tömbben
     */
    public static void setSharedFrame(byte[] frame) {
        SharedJpegProvider.frame = frame;
    }
    
    /**
     * Teszt szál készítése.
     */
    private static void createTest() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    new SharedJpegProvider(new ByteArrayOutputStream() {

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
        SharedJpegProvider.setSharedFrame("a".getBytes());
        createTest();
        Thread.sleep(1000);
        SharedJpegProvider.setSharedFrame("a".getBytes());
        Thread.sleep(2000);
        SharedJpegProvider.setSharedFrame("b".getBytes());
        Thread.sleep(1000);
        SharedJpegProvider.setSharedFrame(null);
        Thread.sleep(2000);
        SharedJpegProvider.setSharedFrame("b".getBytes());
        System.exit(0);
    }
    
}
