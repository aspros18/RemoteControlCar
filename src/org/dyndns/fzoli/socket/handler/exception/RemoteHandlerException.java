package org.dyndns.fzoli.socket.handler.exception;

/**
 * A távoli eszközön keletkezett kivétel.
 * @author zoli
 */
public class RemoteHandlerException extends HandlerException {

    /**
     * Saját kivétel létrehozása saját üzenettel.
     * Az üzenet tartalma bármi lehet, ami nem a rendben jelzés: {@code HandlerException.VAL_OK}
     */
    public RemoteHandlerException(String message) {
        super(message);
    }

    /**
     * Már létező kivétel felhasználása.
     */
    public RemoteHandlerException(Throwable cause) {
        super(cause);
    }

    /**
     * Már létező kivétel felhasználása saját üzenettel.
     * Az üzenet tartalma bármi lehet, ami nem rendben jelzés.
     */
    public RemoteHandlerException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
