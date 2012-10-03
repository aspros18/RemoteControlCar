package org.dyndns.fzoli.socket;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import org.apache.commons.ssl.KeyMaterial;
import org.apache.commons.ssl.SSLClient;
import org.apache.commons.ssl.SSLServer;
import org.apache.commons.ssl.TrustMaterial;

/**
 * Segédosztály az SSL Socketek létrehozásához.
 * @author zoli
 */
public class SSLSocketUtil {
    
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
    
}
