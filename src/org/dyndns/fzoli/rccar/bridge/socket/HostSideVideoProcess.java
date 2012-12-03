package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.socket.handler.SecureHandler;
import org.dyndns.fzoli.socket.process.AbstractSecureProcess;

/**
 * A hoszt által streamelt MJPEG folyam fogadása
 * és továbbküldése képkockaként a vezérlő programoknak.
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

    @Override
    public void run() {
        try {
            getSocket().getInputStream().read();
        }
        catch (Exception ex) {
            ;
        }
    }
    
}
