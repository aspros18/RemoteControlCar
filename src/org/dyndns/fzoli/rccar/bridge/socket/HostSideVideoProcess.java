package org.dyndns.fzoli.rccar.bridge.socket;

import net.sf.jipcam.axis.MjpegFrame;
import org.dyndns.fzoli.rccar.socket.AbstractVideoProcess;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.mjpeg.impl.SharedJpegProvider;

/**
 * A hoszt által streamelt MJPEG folyam fogadása
 * és beállítása a képkockákat tároló {@link SharedJpegProvider} osztályban, amit
 * a vezérlő programoknak való MJPEG streameléshez használ a szerver.
 * @author zoli
 */
public class HostSideVideoProcess extends AbstractVideoProcess {

    /**
     * Biztonságos MJPEG stream fogadó inicializálása.
     * @param handler Biztonságos kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public HostSideVideoProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * A legutolsó MJPEG képkockát tárolja.
     */
    @Override
    protected void processFrame(MjpegFrame fr) {
        if (fr == null) return; // ha nincs képkocka, kilépés
        // TODO: teszt1 helyére a remote name
        SharedJpegProvider.setSharedFrame("teszt1", fr.getJpegBytes()); // aktuális JPEG képkocka tárolása
    }
    
}
