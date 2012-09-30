package org.dyndns.fzoli.socket;

/**
 * A SecureProcess SSL műveletei közben fellépő hibák kivétele.
 * @author zoli
 */
public class SecureProcessException extends ProcessException {

    public SecureProcessException(Throwable cause) {
        super(cause);
    }
    
}
