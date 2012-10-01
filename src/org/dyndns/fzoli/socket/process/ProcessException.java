package org.dyndns.fzoli.socket.process;

/**
 * A Process adatfeldolgozása közben fellépő hibák kivétele.
 * @author zoli
 */
public class ProcessException extends RuntimeException {

    /**
     * Saját kivétel létrehozása saját üzenettel.
     */
    public ProcessException(String message) {
        super(message);
    }
    
    /**
     * Már létező kivétel felhasználása.
     */
    public ProcessException(Throwable cause) {
        super(cause);
    }

    /**
     * Már létező kivétel felhasználása saját üzenettel.
     */
    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
