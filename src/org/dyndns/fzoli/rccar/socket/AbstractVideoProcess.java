package org.dyndns.fzoli.rccar.socket;

import net.sf.jipcam.axis.MjpegFrame;
import net.sf.jipcam.axis.MjpegInputStream;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * MJPEG-folyam feldolgozó a vezérlő klienshez és a híd szerverhez.
 * @author zoli
 */
public abstract class AbstractVideoProcess extends AbstractSecureProcess {

    /**
     * Biztonságos MJPEG-folyam feldolgozó inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public AbstractVideoProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * A kiolvasott képkocka feldolgozása.
     * @param fr a kiolvasott képkocka, ami null is lehet, ha nem sikerült kiolvasni
     */
    protected abstract void processFrame(MjpegFrame fr);
    
    /**
     * Folyamatosan olvassa a bejövő folyamot és dekódolja az MJPEG képkockákat, amit a {@link #processFrame(MjpegFrame)} metódus dolgoz fel.
     */
    @Override
    public final void run() {
        try {
            MjpegFrame fr;
            MjpegInputStream mjpegin = new MjpegInputStream(getSocket().getInputStream()); // MJPEG-folyam dekódoló inicializálása
            while(!getSocket().isClosed()) { // amíg van adat, ...
                try {
                    fr = mjpegin.readMjpegFrame(); // ... addig olvas ...
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    sleep(5);
                    fr = null; // ... és hiba esetén nincs képkocka
                }
                processFrame(fr);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
