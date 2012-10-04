package org.dyndns.fzoli.socket.handler;

/**
 * Akkor keletkezik ez a kivétel, ha ugyan azzal a tanúsítvánnyal többen is kapcsolódnak a szerverhez.
 * @author zoli
 */
public class MultipleCertificateException extends SecureHandlerException {

    public MultipleCertificateException(String s) {
        super(s);
    }

    public MultipleCertificateException(Throwable cause) {
        super(cause);
    }

    public MultipleCertificateException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
