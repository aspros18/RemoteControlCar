package org.dyndns.fzoli.socket.handler;

/**
 * A SecureHandler SSL műveletei közben fellépő kivétel.
 * @author zoli
 */
public class SecureHandlerException extends HandlerException {

    /**
     * Saját kivétel létrehozása saját üzenettel.
     */
    public SecureHandlerException(String message) {
        super(message);
    }

    /**
     * Már létező kivétel felhasználása.
     */
    public SecureHandlerException(Throwable cause) {
        super(cause);
    }

    /**
     * Már létező kivétel felhasználása saját üzenettel.
     */
    public SecureHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
