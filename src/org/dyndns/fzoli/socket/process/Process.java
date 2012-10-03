package org.dyndns.fzoli.socket.process;

import org.dyndns.fzoli.socket.Socketter;

/**
 * Interfész a socketen át adatfeldolgozást végző osztály írására.
 * @author zoli
 */
public interface Process extends Socketter {
    
    /**
     * Ez a metódus indítja el az adatfeldolgozást.
     */
    @Override
    public void run();
    
}
