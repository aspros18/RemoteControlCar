package org.dyndns.fzoli.socket.process;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import org.apache.commons.ssl.KeyMaterial;
import org.apache.commons.ssl.SSLClient;
import org.apache.commons.ssl.SSLServer;
import org.apache.commons.ssl.TrustMaterial;

/**
 * Segédosztály a SecureProcess interfész implementálásához és az SSLSocketek létrehozásához.
 * @author zoli
 */
public class SecureUtil {
    
    /**
     * SSL Server socket létrehozása.
     * @param port a szerver portja, amin hallgat
     * @param ca annak a CA-nak a tanúsítványa, amely az egyetlen megbízható tanúsítvány kiállító
     * @param crt az egyetlen megbízható CA által kiállított tanúsítvány
     * @param key a tanúsítvány titkos kulcsa
     * @param passwd a használandó tanúsítvány jelszava, ha van, egyébként null
     */
    public static SSLServerSocket createServerSocket(int port, File ca, File crt, File key, char[] passwd) throws GeneralSecurityException, IOException {
        if (passwd == null) passwd = new char[] {}; // ha nincs jelszó megadva, üres jelszó létrehozása
        SSLServer server = new SSLServer(); // SSL szerver socket létrehozására kell
        server.setKeyMaterial(new KeyMaterial(crt, key, passwd)); //publikus és privát kulcs megadása a kapcsolathoz
        server.setTrustMaterial(new TrustMaterial(ca)); // csak a saját CA és az ő általa kiállított tanúsítványok legyenek megbízhatóak
        server.setCheckHostname(false); // a hostname kivételével minden más ellenőrzése, amikor a kliens kapcsolódik
        server.setCheckExpiry(true);
        server.setCheckCRL(true); 
        return (SSLServerSocket) server.createServerSocket(port); // server socket létrehozása
    }
    
    /**
     * SSL kliens socket létrehozása és kapcsolódás a szerverhez.
     * @param port a szerver portja, amin hallgat
     * @param ca annak a CA-nak a tanúsítványa, amely az egyetlen megbízható tanúsítvány kiállító
     * @param crt az egyetlen megbízható CA által kiállított tanúsítvány
     * @param key a tanúsítvány titkos kulcsa
     * @param passwd a használandó tanúsítvány jelszava, ha van, egyébként null
     */
    public static SSLSocket createClientSocket(String host, int port, File ca, File crt, File key, char[] passwd) throws GeneralSecurityException, IOException {
        if (passwd == null) passwd = new char[] {}; // ha nincs jelszó megadva, üres jelszó létrehozása
        SSLClient client = new SSLClient(); // SSL kliens socket létrehozására kell
        client.setKeyMaterial(new KeyMaterial(crt, key, passwd)); //publikus és privát kulcs megadása a kapcsolathoz
        client.setTrustMaterial(new TrustMaterial(ca)); // csak a megadott CA és az ő általa kiállított tanusítványok legyenek megbízhatóak
        client.setCheckHostname(false); // hostname ellenőrzés kikapcsolása, minden más engedélyezése
        client.setCheckExpiry(true);
        client.setCheckCRL(true);
        SSLSocket s = (SSLSocket) client.createSocket(host, port); // kliens socket létrehozása és kapcsolódás
        return s;
    }
    
    /**
     * A titkosított kommunikáció ezen oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     * @throws SecureProcessException ha a tanúsítvány hibás
     */
    public static String getLocalCommonName(SSLSocket socket) {
        try {
            checkSession(socket);
            return getCommonName(socket.getSession().getLocalCertificates()[0]);
        }
        catch (CertificateException | CertificateEncodingException ex) {
            throw new SecureProcessException(ex);
        }
    }
    
    /**
     * A titkosított kommunikáció másik oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     * @throws SecureProcessException ha nem megbízható a kapcsolat vagy a tanúsítvány hibás
     */
    public static String getRemoteCommonName(SSLSocket socket) {
        try {
            checkSession(socket);
            return getCommonName(socket.getSession().getPeerCertificates()[0]);
        }
        catch (SSLPeerUnverifiedException | CertificateException | CertificateEncodingException ex) {
            throw new SecureProcessException(ex);
        }
    }
    
    /**
     * Az SSLSocket munkamenetének ellenőrzése.
     * @throws SecureProcessException ha a munkamenet nem érvényes
     */
    private static void checkSession(SSLSocket socket) {
        if (!socket.getSession().isValid()) throw new SecureProcessException("Invalid certificate. Please, check your CA.");
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
