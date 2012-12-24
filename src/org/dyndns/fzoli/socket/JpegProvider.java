package org.dyndns.fzoli.socket;

import java.io.OutputStream;
import java.util.Arrays;

/**
 * MJPEG streamelő.
 * @author zoli
 */
public abstract class JpegProvider {

    /**
     * MJPEG kimenő folyam.
     */
    private final OutputStream out;
    
    /**
     * Folyam azonosító.
     */
    private final String key;
    
    /**
     * Konstruktor.
     * @param key a folyam azonosító
     * @param out az MJPEG kimenő folyam
     */
    public JpegProvider(String key, OutputStream out) {
        if (key == null || out == null) throw new NullPointerException("Parameters of JpegProvider can not be null");
        this.key = key;
        this.out = out;
    }

    /**
     * A JPEG képkocka adatát adja vissza bájt tömbben.
     * Az utód osztály eldöntheti, hogy honnan szerzi meg ezt az adatot.
     * @param key a folyam azonosító
     */
    protected abstract byte[] getFrame(String key);
    
    /**
     * A JPEG képkocka adatát állítja be.
     * Ezzel a {@code getFrame(boolean)} metódus befejezi a várakozást.
     * Az utód osztály eldöntheti, hogy hol tárolja ezt az adatot.
     * @param key a folyam azonosító
     * @param frame a képkocka adata bájt tömbben
     */
    protected abstract void setFrame(String key, byte[] frame);
    
    /**
     * Egy képkockát ad vissza.
     * Ha még nincs egy képkocka se beállítva, mindenképpen megvárja.
     * Ha már van képkocka beállítva, paramétertől függ, hogy vár-e a következőre.
     * Ezzel a megoldással az esetleg lassú kapcsolattal rendelkező kliensek nem húzzák
     * vissza a gyorsabb kapcsolattal rendelkezőket.
     * Ennek ára az, hogy van egy FPS limit a {@code Thread.sleep(int)} metódus miatt.
     * @param wait várja-e meg a következő képkockát létező adat esetén
     */
    private byte[] nextFrame(boolean wait) throws InterruptedException {
        byte[] frame;
        byte[] tmp = frame = getFrame(key);
        if (wait || tmp == null) {
            while ((frame = getFrame(key)) == null || (tmp != null && Arrays.equals(tmp, frame))) {
                Thread.sleep(30);
            }
        }
        return frame;
    }
    
    /**
     * MJPEG folyamot küld a kimenetre.
     * Forrás: http://www.damonkohler.com/2010/10/mjpeg-streaming-protocol.html
     */
    public void handleConnection() throws Exception {
        out.write((
            "HTTP/1.0 200 OK\r\n" +
            "Server: " + System.getProperty("os.name") + "-" + System.getProperty("os.version") + "\r\n" +
            "Connection: close\r\n" +
            "Max-Age: 0\r\n" +
            "Expires: 0\r\n" +
            "Cache-Control: no-cache, private\r\n" + 
            "Pragma: no-cache\r\n" + 
            "Content-Type: multipart/x-mixed-replace; " +
            "boundary=--BoundaryString\r\n\r\n").getBytes());
        byte[] frame = nextFrame(false);
        while (true) {
            out.write((
                "--BoundaryString\r\n" +
                "Content-type: image/jpg\r\n" +
                "Content-Length: " +
                frame.length +
                "\r\n\r\n").getBytes());
            out.write(frame);
            out.write("\r\n\r\n".getBytes());
            out.flush();
            frame = nextFrame(true);
        }
    }

}
