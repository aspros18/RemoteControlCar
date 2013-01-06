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
    private String key;
    
    /**
     * Segédváltozó az aktuális képkocka újraküldéséhez.
     */
    private boolean resend = false;
    
    /**
     * Konstruktor.
     * @param key a folyam azonosító
     * @param out az MJPEG kimenő folyam
     */
    public JpegProvider(String key, OutputStream out) {
        if (out == null) throw new NullPointerException("Out parameter of JpegProvider can not be null");
        this.key = key;
        this.out = out;
    }

    /**
     * Megadja, hogy meg kell-e szakítani a streamelést.
     * @return true esetén megszakad a streamelés, egyébként folytatódik tovább
     */
    protected boolean isInterrupted() {
        return false;
    }
    
    /**
     * Folyam azonosító.
     */
    public String getKey() {
        return key;
    }

    /**
     * Folyam azonosító beállítása.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Újraküldi az aktuális képkockát, ha van képkocka és fut a kapcsolatkezelés.
     */
    public void resend() {
        resend = true;
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
     * Ha az újraküldés aktiválva van és van képkocka,
     * akkor azonnal visszatér az aktuális képkockával és kikapcsolja az újraküldést.
     * @param wait várja-e meg a következő képkockát létező adat esetén
     */
    private byte[] nextFrame(boolean wait) throws InterruptedException {
        String key = getKey();
        if (key == null || isInterrupted()) return null;
        byte[] frame;
        byte[] tmp = frame = getFrame(key);
        if ((wait && !resend) || tmp == null) {
            while (!isInterrupted() && (key = getKey()) != null && ((frame = getFrame(key)) == null || (tmp != null && (!resend && Arrays.equals(tmp, frame))))) {
                Thread.sleep(20);
            }
        }
        resend = false;
        return frame;
    }
    
    /**
     * Kivétel keletkezett a ciklusban a kimenetre írás közben.
     * A metódus eredetileg false értékkel tér vissza, ezzel a ciklus végetér.
     * @return true esetén folytatódik a ciklus, egyébként kilép a ciklusból
     */
    protected boolean onException(Exception ex) {
        return false;
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
        while (!isInterrupted()) {
            if (frame != null) {
                try {
                    out.write((
                        "--BoundaryString\r\n" +
                        "Content-type: image/jpg\r\n" +
                        "Content-Length: " +
                        frame.length +
                        "\r\n\r\n").getBytes());
                    out.write(frame);
                    out.write("\r\n\r\n".getBytes());
                    out.flush();
                }
                catch (Exception ex) {
                    if (!onException(ex)) break;
                }
            }
            else {
                Thread.sleep(20);
            }
            frame = nextFrame(true);
        }
    }

}
