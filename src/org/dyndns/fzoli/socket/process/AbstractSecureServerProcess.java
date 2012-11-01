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
    
    /**
     * Megkeresi az adatfeldolgozót a paraméterek alapján.
     * @param remoteName tanúsítvány common name
     * @param deviceId eszközazonosító
     * @param connectionId kapcsolatazonosító
     * @return null, ha nincs találat, egyébként adatfeldolgozó objektum
     */
    protected SecureProcess findProcess(String remoteName, int deviceId, int connectionId) {
        return findProcess(remoteName, deviceId, connectionId, SecureProcess.class);
    }
    
    /**
     * Megkeresi az adatfeldolgozót a paraméterek alapján.
     * @param remoteName tanúsítvány common name
     * @param deviceId eszközazonosító
     * @param connectionId kapcsolatazonosító
     * @param clazz az adatfeldolgozó típusa
     * @return null, ha nincs találat, egyébként adatfeldolgozó objektum
     */
    protected <T extends SecureProcess> T findProcess(String remoteName, int deviceId, int connectionId, Class<T> clazz) {
        return getHandler().findProcess(remoteName, deviceId, connectionId, clazz);
    }
    
}
