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
     * Konstruktor.
     * @param out az MJPEG kimenő folyam
     */
    public JpegProvider(OutputStream out) {
        this.out = out;
    }

    /**
     * A JPEG képkocka adatát adja vissza bájt tömbben.
     * Az utód osztály eldöntheti, hogy honnan szerzi meg ezt az adatot.
     */
    protected abstract byte[] getFrame();
    
    /**
     * A JPEG képkocka adatát állítja be.
     * @param frame a képkocka adata bájt tömbben
     * Ezzel a {@code getFrame(boolean)} metódus befejezi a várakozást.
     * Az utód osztály eldöntheti, hogy hol tárolja ezt az adatot.
     */
    protected abstract void setFrame(byte[] frame);
    
    /**
     * Egy képkockát ad vissza.
     * Ha még nincs egy képkocka se beállítva, mindenképpen megvárja.
     * Ha már van képkocka beállítva, paramétertől függ, hogy vár-e a következőre.
     * @param wait várja-e meg a következő képkockát létező adat esetén
     */
    private byte[] getFrame(boolean wait) throws InterruptedException {
        byte[] frame;
        byte[] tmp = frame = getFrame();
        if (wait || tmp == null) {
            while ((frame = getFrame()) == null || (tmp != null && Arrays.equals(tmp, frame))) {
                Thread.sleep(30);
            }
        }
        return frame;
    }
    
    /**
     * MJPEG folyamot küld a kimenetre.
     * @param in JPEG képkockákat adagoló objektum
     * @param out a kimenet
     * Forrás: http://www.damonkohler.com/2010/10/mjpeg-streaming-protocol.html
     */
    public void handleConnection() throws Exception {
        out.write((
            "HTTP/1.0 200 OK\r\n" +
            "Server: ServerName\r\n" +
            "Connection: close\r\n" +
            "Max-Age: 0\r\n" +
            "Expires: 0\r\n" +
            "Cache-Control: no-cache, private\r\n" + 
            "Pragma: no-cache\r\n" + 
            "Content-Type: multipart/x-mixed-replace; " +
            "boundary=--BoundaryString\r\n\r\n").getBytes());
        byte[] frame = getFrame(false);
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
            frame = getFrame(true);
        }
    }

}
