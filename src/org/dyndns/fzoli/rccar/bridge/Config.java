package org.dyndns.fzoli.rccar.bridge;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A híd konfigurációját tölti be a két konfig fájlból.
 * Ha a konfig fájl nem létezik, létrehozza az alapértelmezett konfiggal.
 * A konfig fájlban # karakterrel lehet kommentezni a sorokban.
 * A fölösleges szóközöket az objektumot létrehozó metódus levágja.
 * 
 * A két konfig fájl nevei:
 * - bridge.conf
 * - hosts.txt
 * 
 * @author zoli
 */
public class Config {
    
    private Map<String, String> values;
    private List<String> hosts;
    
    /**
     * Az értékeket tartalmazó felsorolás kulcsai, amit a program használ.
     */
    public static final String KEY_PORT = "port",
                               KEY_CA = "ca",
                               KEY_CERT = "cert",
                               KEY_KEY = "key";
    
    private static final String LS = System.getProperty("line.separator"),
                                UD = System.getProperty("user.dir");
    
    /**
     * A konfig fájlok eléréséhez létrehozott objektumok.
     */
    private static final File FILE_CONFIG = new File(UD, "bridge.conf"),
                              FILE_HOSTS = new File(UD, "hosts.txt");
    
    /**
     * Az alapértelmezett hosts.txt fájl tartalma.
     */
    private static final String DEFAULT_HOSTS =
            "# Ebben a fájlban kell felsorolni a távirányítós autót vezérlő telefonok tanusítványának Common Name (CN) mezőit." + LS +
            "# Soronként csak egy CN mező adható meg!" + LS + LS;
    
    /**
     * Az alapértelmezett bridge.conf fájl tartalma.
     */
    private static final String DEFAULT_CONFIG =
            "# Ez a fájl a távirányítós autót vezérlő telefonokat a számítógépen futó vezérlő programokkal összekötő szerver konfigurációs állománya." + LS +
            KEY_PORT + " 8443 # az a TCP port, amin a szerver hallgat" + LS +
            KEY_CA + " /path/to/ca.crt # a tanusítványokat kiállító CA tanusítvány-fájl" + LS +
            KEY_CERT + " /path/to/name.crt # a szerver tanusítvány-fájl" + LS +
            KEY_KEY + " /path/to/name.key # a szerver titkos kulcsa" + LS;
    
    /**
     * Ez az osztály nem példányosítható és nem származhatnak belőle újabb osztályok.
     */
    private Config() {
    }

    /**
     * A konfigurációs fájl port paramétere.
     * A SocketServer ezen a porton figyel.
     * @return port vagy kivétel esetén NULL
     */
    public Integer getPort() {
        try {
            return Integer.parseInt(getValues().get(KEY_PORT));
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * A konfigurációs fájl ca paramétere.
     * Ez az egyelten CA, amit a szerver és kliens socketek elfogadnak.
     * @return fájl vagy NULL, ha a fájl nem létezik
     */
    public File getCAFile() {
        return createFile(KEY_CA);
    }
    
    /**
     * A konfigurációs fájl cert paramétere.
     * A CA által kiállított érvényes tanusítvány helye.
     * @return fájl vagy NULL, ha a fájl nem létezik
     */
    public File getCertFile() {
        return createFile(KEY_CERT);
    }
    
    /**
     * A konfigurációs fájl key paramétere.
     * A CA által kiállított érvényes tanusítvány titkos kulcsának helye.
     * @return fájl vagy NULL, ha a fájl nem létezik
     */
    public File getKeyFile() {
        return createFile(KEY_CERT);
    }
    
    /**
     * A konfigurációs fájl paraméterei és azok értékei.
     * @return A paramétereket tartalmazó felsorolás vagy NULL, ha a konfig fájl nem létezik.
     */
    public Map<String, String> getValues() {
        return values;
    }

    /**
     * Az autót vezérlő telefonok tanusítványainak CN mezői.
     * @return A hostokat tartalmazó lista vagy NULL, ha a konfig fájl nem létezik.
     */
    public List<String> getHosts() {
        return hosts;
    }
    
    /**
     * Gyártó metódus.
     */
    public static Config getConfig() {
        Config config = new Config();
        config.hosts = read(FILE_HOSTS, DEFAULT_HOSTS);
        List<String> conf = read(FILE_CONFIG, DEFAULT_CONFIG);
        if (conf != null) {
            int ind;
            String key, val;
            config.values = new HashMap<>();
            for (String ln : conf) {
                ind = ln.indexOf(" ");
                if (ind == -1) continue;
                key = ln.substring(0, ind);
                val = ln.substring(ind).trim();
                config.values.put(key, val);
            }
        }
        return config;
    }
    
    /**
     * A megadott szövegfájlt beolvasó metódus.
     * @param f a beolvasandó fájl
     * @param def ha a fájl nem létezik, létrejön a fájl a paraméter tartalmával.
     * @return Sorokat tartalmazó lista vagy NULL, ha a fájl nem létezik.
     * @throws RuntimeException ha bármi hiba történik. Pl. nincs olvasási/írási jog
     */
    private static List<String> read(File f, String def) {
        try {
            int ind;
            String ln;
            List<String> ls = new ArrayList<>();
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            while ((ln = in.readLine()) != null) {
                ln = ln.trim();
                if (ln.startsWith("#")) continue;
                ind = ln.indexOf("#");
                if (ind != -1) ln = ln.substring(0, ind);
                ls.add(ln);
            }
            in.close();
            return ls;
        }
        catch (FileNotFoundException ex) {
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f)));
                out.write(def, 0, def.length());
                out.flush();
                out.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return null;
    }
    
    /**
     * Útvonal alapján fájl objektumot ad vissza.
     * Ha a fájl nem létezik, vagy nem fájl, akkor NULL értékkel tér vissza.
     */
    private File createFile(String key) {
        String path = getValues().get(key);
        if (path == null) return null;
        File f = new File(path);
        if (!f.exists() || !f.isFile()) return null;
        return f;
    }
    
}