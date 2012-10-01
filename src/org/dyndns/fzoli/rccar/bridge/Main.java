package org.dyndns.fzoli.rccar.bridge;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.GeneralSecurityException;
import org.apache.commons.ssl.KeyMaterial;
import org.apache.commons.ssl.SSLServer;
import org.apache.commons.ssl.TrustMaterial;
import static org.dyndns.fzoli.rccar.UIUtil.*;

/**
 * A híd indító osztálya.
 * @author zoli
 */
public class Main {
    
    /**
     * A híd konfigurációja.
     */
    private static final Config CONFIG = Config.getInstance();
    
    /**
     * Általános rendszerváltozók.
     */
    private static final String LS = System.getProperty("line.separator");
    
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
        setSystemLookAndFeel();
        if (CONFIG.isCorrect()) try {
            final ServerSocket ss = createServerSocket();
            //TODO: feldolgozás
        }
        catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
        else {
            final StringBuilder msg = new StringBuilder();
            if (CONFIG.isNew()) {
                msg.append("A konfigurációs fájlokat létrehoztam.").append(LS)
                   .append("Kérem, állítsa be őket megfelelően!").append(LS).append(LS)
                   .append("Konfig fájl helye: ").append(Config.FILE_CONFIG).append(LS)
                   .append("Hosts fájl helye: ").append(Config.FILE_HOSTS);
                alert("Üzenet", msg.toString(), System.out);
            }
            else {
                msg.append("Nem megfelelő konfiguráció!").append(LS).append(LS);
                if (CONFIG.isConfigFileCorrect()) {
                    msg.append("Legalább egy hosztot adjon meg a ").append(Config.FILE_HOSTS).append(" fájlban.");
                }
                else {
                    msg.append("A ").append(Config.FILE_CONFIG).append(" fájl hibásan van paraméterezve:").append(LS);
                    if (CONFIG.getPort() == null) msg.append("- Adjon meg érvényes portot.").append(LS);
                    if (CONFIG.getCAFile() == null) msg.append("- Adjon meg létező ca fájl útvonalat.").append(LS);
                    if (CONFIG.getCertFile() == null) msg.append("- Adjon meg létező cert fájl útvonalat.").append(LS);
                    if (CONFIG.getKeyFile() == null) msg.append("- Adjon meg létező key fájl útvonalat.").append(LS);
                }
                alert("Hiba", msg.toString(), System.err);
                System.exit(1);
            }
        }
    }
    
}
