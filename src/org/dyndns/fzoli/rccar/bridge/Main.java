package org.dyndns.fzoli.rccar.bridge;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;
import org.apache.commons.ssl.KeyMaterial;
import org.apache.commons.ssl.SSLServer;
import org.apache.commons.ssl.TrustMaterial;

/**
 * A híd indító osztálya.
 * @author zoli
 */
public class Main {
    
    /**
     * A híd konfigurációja.
     */
    private static final Config CONFIG = Config.getConfig();
    
    /**
     * SSL Server socket létrehozása a konfig fájl alapján.
     * @param port szerver portja
     */
    private static ServerSocket createServerSocket() throws IOException, GeneralSecurityException {
        SSLServer server = new SSLServer(); // SSL szerver socket létrehozására kell
        server.setKeyMaterial(new KeyMaterial(CONFIG.getCertFile(), CONFIG.getKeyFile(), CONFIG.getPassword())); //publikus és privát kulcs megadása a kapcsolathoz
        server.setTrustMaterial(new TrustMaterial(CONFIG.getCAFile())); // a saját CA (és az ő általa kiállított tanúsítványok) legyen megbízható csak (testserver és testclient tanúsítványok)
        server.setCheckHostname(false); // a hostname kivételével minden más ellenőrzése, amikor a kliens kapcsolódik
        server.setCheckExpiry(true);
        server.setCheckCRL(true); 
        return server.createServerSocket(CONFIG.getPort()); // server socket létrehozása
    }
    
    /**
     * A híd main metódusa.
     */
    public static void main(String[] args) {
        if (CONFIG.isCorrect()) try {
            ServerSocket ss = createServerSocket();
            //TODO: feldolgozás
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        else {
            System.err.println("Nem megfelelő konfiguráció."); //TODO: beszédesebb hibaüzenet
        }
    }
    
}
