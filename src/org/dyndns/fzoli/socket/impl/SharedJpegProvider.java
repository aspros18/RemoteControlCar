package org.dyndns.fzoli.socket.impl;

import java.io.OutputStream;
import org.dyndns.fzoli.socket.JpegProvider;

/**
 * Megosztott MJPEG streamelő.
 * Mindegyik objektum ugyan azt a képkocka objektumot használja.
 * @author zoli
 */
public class SharedJpegProvider extends JpegProvider{

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
    
}
