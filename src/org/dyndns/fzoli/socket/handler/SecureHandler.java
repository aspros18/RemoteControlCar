package org.dyndns.fzoli.socket.handler;

import java.util.List;
import org.dyndns.fzoli.socket.SecureSocketter;
import org.dyndns.fzoli.socket.process.SecureProcess;

/**
 * Biztonságos socketfeldolgozó implementálásához kliens és szerver oldalra.
 * A socket feldolgozása előtt az adatok alapján kiválasztja, melyik feldolgozót kell indítani.
 * @author zoli
 */
public interface SecureHandler extends Handler, SecureSocketter {
    
    /**
     * Azokat a biztonságos adatfeldolgozókat adja vissza, melyek még dolgoznak.
     */
    public List<SecureProcess> getSecureProcesses();
    
    /**
     * Igaz, ha ugyan azzal a tanúsítvánnyal és azonosítókkal rendelkezik a megadott feldolgozó.
     * @param handler a másik feldolgozó
     */
    public boolean isCertEqual(SecureHandler handler);
    
}
