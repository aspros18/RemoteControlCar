package org.dyndns.fzoli.socket.process;

import java.util.List;
import org.dyndns.fzoli.socket.handler.AbstractClientHandler;
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
     * Azokat az adatfeldolgozókat adja vissza, melyek még dolgoznak.
     */
    protected static List<Process> getProcesses() {
        return AbstractClientHandler.getProcesses();
    }
    
    /**
     * Azokat a biztonságos adatfeldolgozókat adja vissza, melyek még dolgoznak.
     */
    protected static List<SecureProcess> getSecureProcesses() {
        return AbstractSecureClientHandler.getSecureProcesses();
    }
    
    /**
     * Kapcsolatazonosító alapján megkeresi az adatfeldolgozót.
     * @param connectionId kapcsolatazonosító
     * @return null, ha nincs találat, egyébként adatfeldolgozó objektum
     */
    protected static Process findProcess(int connectionId) {
        return AbstractClientHandler.findProcess(connectionId);
    }
    
    /**
     * Kapcsolatazonosító alapján megkeresi az adatfeldolgozót.
     * @param connectionId kapcsolatazonosító
     * @param clazz az adatfeldolgozó típusa
     * @return null, ha nincs találat, egyébként adatfeldolgozó objektum
     */
    protected static <T extends Process> T findProcess(int connectionId, Class<T> clazz) {
        return AbstractClientHandler.findProcess(connectionId, clazz);
    }
    
}
