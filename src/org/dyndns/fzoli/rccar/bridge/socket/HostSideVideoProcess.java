package org.dyndns.fzoli.rccar.bridge.socket;

import net.sf.jipcam.axis.MjpegFrame;
import net.sf.jipcam.axis.MjpegInputStream;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.impl.SharedJpegProvider;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * A hoszt által streamelt MJPEG folyam fogadása
 * és beállítása a képkockákat tároló {@link SharedJpegProvider} osztályban, amit
 * a vezérlő programoknak való MJPEG streameléshez használ a szerver.
 * @author zoli
 */
public class HostSideVideoProcess extends AbstractSecureProcess {

    /**
     * Biztonságos MJPEG stream fogadó inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public HostSideVideoProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * Folyamatosan olvassa a bejövő folyamot és dekódolja az MJPEG képkockákat, és a legutolsó képkockát tárolja.
     */
    @Override
    public void run() {
        try {
            MjpegFrame fr;
            MjpegInputStream mjpegin = new MjpegInputStream(getSocket().getInputStream()); // MJPEG-folyam dekódoló inicializálása
            while((fr = mjpegin.readMjpegFrame()) != null) { // amíg van adat, addig olvas
                //TODO: teszt1 helyére a remote name
                SharedJpegProvider.setSharedFrame("teszt1", fr.getJpegBytes()); // aktuális JPEG képkocka tárolása
            }
        }
        catch (Exception ex) {
            ;
        }
    }
    
}
