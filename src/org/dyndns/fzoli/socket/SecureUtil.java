package org.dyndns.fzoli.socket;

import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.security.cert.X509Certificate;

/**
 * Segédosztály a SecureProcess interfész implementálásához.
 * @author zoli
 */
public class SecureUtil {
    
    /**
     * A titkosított kommunikáció másik oldalán álló gép tanúsítványának CN mezőjét adja vissza.
     * @throws SecureProcessException ha nem megbízható a kapcsolat
     */
    public static String getCommonName(SSLSocket socket) {
        try {
            X509Certificate[] certs = socket.getSession().getPeerCertificateChain(); // tanúsítványok lekérése
            String certdata = certs[0].getSubjectDN().getName(); // az első tanúsítvány adatainak megszerzése (az első tanúsítvány a peer tanúsítványa, a második és az utána lévők a CA tanúsítványok)
            int cnstart = certdata.indexOf("CN=") + 3; // "CN=" résztől ...
            int cnstop = certdata.indexOf(',', cnstart); // ... a vesszőig ...
            return certdata.substring(cnstart, cnstop); // ... kérem a string tartalmát, ami a tanúsítványban szereplő Common Name (CN)
        }
        catch (SSLPeerUnverifiedException ex) {
            throw new SecureProcessException(ex);
        }
    }
    
}
