package org.dyndns.fzoli.socket.handler;

/**
 * A Handler adatfeldolgozása közben fellépő kivétel.
 * @author zoli
 */
public class HandlerException extends RuntimeException {

    /**
     * Saját kivétel létrehozása saját üzenettel.
     */
    public HandlerException(String message) {
        super(message);
    }
    
    /**
     * Már létező kivétel felhasználása.
     */
    public HandlerException(Throwable cause) {
        super(cause);
    }

    /**
     * Már létező kivétel felhasználása saját üzenettel.
     */
    public HandlerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
