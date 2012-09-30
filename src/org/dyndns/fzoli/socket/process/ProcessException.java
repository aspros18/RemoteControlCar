package org.dyndns.fzoli.socket.process;

/**
 * A Process adatfeldolgozása közben fellépő hibák kivétele.
 * @author zoli
 */
public class ProcessException extends RuntimeException {

    public ProcessException(Throwable cause) {
        super(cause);
    }
    
}
