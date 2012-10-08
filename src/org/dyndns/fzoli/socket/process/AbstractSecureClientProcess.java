package org.dyndns.fzoli.socket.process;

import org.dyndns.fzoli.socket.handler.AbstractSecureClientHandler;

/**
 * A socketen át biztonságos adatfeldolgozást végző osztály alapja kliens oldalra.
 * @author zoli
 */
public abstract class AbstractSecureClientProcess extends AbstractSecureProcess {

    /**
     * Biztonságos adatfeldolgozó inicializálása.
     * @param handler Biztonságos kliens oldali kapcsolatfeldolgozó, ami létrehozza ezt az adatfeldolgozót.
     * @throws NullPointerException ha handler null
     */
    public AbstractSecureClientProcess(AbstractSecureClientHandler handler) {
        super(handler);
    }

    /**
     * @return Biztonságos kliens oldali kapcsolatfeldolgozó, ami létrehozta ezt az adatfeldolgozót.
     */
    @Override
    public AbstractSecureClientHandler getHandler() {
        return (AbstractSecureClientHandler) super.getHandler();
    }
    
    /**
     * Kapcsolatazonosító alapján megkeresi az adatfeldolgozót.
     * @param connectionId kapcsolatazonosító
     * @return null, ha nincs találat, egyébként adatfeldolgozó objektum
     */
    protected Process findProcess(int connectionId) {
        return getHandler().findProcess(connectionId);
    }
    
}
