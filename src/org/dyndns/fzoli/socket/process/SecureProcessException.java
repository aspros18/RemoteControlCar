package org.dyndns.fzoli.socket.process;

/**
 * A SecureProcess SSL műveletei közben fellépő hibák kivétele.
 * @author zoli
 */
public class SecureProcessException extends ProcessException {

    /**
     * Saját kivétel létrehozása saját üzenettel.
     */
    public SecureProcessException(String message) {
        super(message);
    }

    /**
     * Már létező kivétel felhasználása.
     */
    public SecureProcessException(Throwable cause) {
        super(cause);
    }

    /**
     * Már létező kivétel felhasználása saját üzenettel.
     */
    public SecureProcessException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
