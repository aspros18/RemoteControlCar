package org.dyndns.fzoli.socket.mjpeg.impl;

import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.dyndns.fzoli.socket.mjpeg.JpegProvider;

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
    
}
