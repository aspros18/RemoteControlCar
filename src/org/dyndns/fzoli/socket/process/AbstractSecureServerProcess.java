package org.dyndns.fzoli.socket.process;

import org.dyndns.fzoli.socket.handler.AbstractSecureServerHandler;

/**
 * A socketen át biztonságos adatfeldolgozást végző osztály alapja szerver oldalra.
 * @author zoli
 */
public abstract class AbstractSecureServerProcess extends AbstractSecureProcess {

    /**
     * Biztonságos adatfeldolgozó inicializálása.
     * @param handler Biztonságos szerver oldali kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public AbstractSecureServerProcess(AbstractSecureServerHandler handler) {
        super(handler);
    }

    /**
     * @return Biztonságos szerver oldali kapcsolatfeldolgozó, ami létrehozta ezt az adatfeldolgozót.
     */
    @Override
    public AbstractSecureServerHandler getHandler() {
        return (AbstractSecureServerHandler) super.getHandler();
    }
    
}
