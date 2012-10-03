package org.dyndns.fzoli.socket.handler;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

/**
 * Segédosztály a SecureHandler interfész implementálásához.
 * @author zoli
 */
class SecureHandlerUtil {
    
    /**
     * A titkosított kommunikáció ezen oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     * @throws SecureHandlerException ha a tanúsítvány hibás
     */
    public static String getLocalCommonName(SSLSocket socket) {
        try {
            checkSession(socket);
            return getCommonName(socket.getSession().getLocalCertificates()[0]);
        }
        catch (CertificateException | CertificateEncodingException ex) {
            throw new SecureHandlerException(ex);
        }
    }
    
    /**
     * A titkosított kommunikáció másik oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     * @throws SecureHandlerException ha nem megbízható a kapcsolat vagy a tanúsítvány hibás
     */
    public static String getRemoteCommonName(SSLSocket socket) {
        try {
            checkSession(socket);
            return getCommonName(socket.getSession().getPeerCertificates()[0]);
        }
        catch (SSLPeerUnverifiedException | CertificateException | CertificateEncodingException ex) {
            throw new SecureHandlerException(ex);
        }
    }
    
    /**
     * Az SSLSocket munkamenetének ellenőrzése.
     * @throws SecureHandlerException ha a munkamenet nem érvényes
     */
    private static void checkSession(SSLSocket socket) {
        if (socket == null || socket.getSession() == null || socket.getSession().getLocalCertificates() == null || (!socket.getSession().isValid())) throw new SecureHandlerException("Invalid certificate. Please, check your CA.");
    }
    
    /**
     * A tanúsítvány CN mezőjét adja vissza.
     * @throws CertificateException ha a tanúsítvány nem X509 szabvány szerint kódolt
     * @throws CertificateEncodingException ha a tanúsítvány nem X509 szabvány szerint kódolt
     */
    private static String getCommonName(Certificate cert) throws CertificateException, CertificateEncodingException {
        String certdata = getPrincipal(cert);
        int cnstart = certdata.indexOf("CN=") + 3; // "CN=" résztől ...
        int cnstop = certdata.indexOf(',', cnstart); // ... a vesszőig ...
        if (cnstop == -1) cnstop = certdata.length(); // ... vagy ha nincs vessző, a végéig ...
        return certdata.substring(cnstart, cnstop); // ... kérem a string tartalmát, ami a tanúsítványban szereplő Common Name (CN)
    }
    
    /**
     * A tanúsítvány alapadatait adja vissza.
     * @throws CertificateException ha a tanúsítvány nem X509 szabvány szerint kódolt
     * @throws CertificateEncodingException ha a tanúsítvány nem X509 szabvány szerint kódolt
     */
    private static String getPrincipal(Certificate cert) throws CertificateException, CertificateEncodingException {
        return X509Certificate.getInstance(cert.getEncoded()).getSubjectDN().getName();
    }
    
}