package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.socket.AbstractVideoProcess;
import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.mjpeg.impl.SharedJpegProvider;
import org.dyndns.fzoli.socket.mjpeg.jipcam.MjpegFrame;

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
    protected void processFrame(MjpegFrame fr) throws Exception {
        if (fr == null) return; // ha nincs képkocka, kilépés
        SharedJpegProvider.setSharedFrame(getRemoteCommonName(), fr.getJpegBytes()); // aktuális JPEG képkocka tárolása
    }
    
}
