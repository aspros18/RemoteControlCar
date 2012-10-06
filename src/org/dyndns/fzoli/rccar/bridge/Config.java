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
 * A híd konfigurációját tölti be a bridge.conf fájlból.
 * Ha a konfig fájl nem létezik, létrehozza az alapértelmezett konfiggal.
 * A konfig fájlban # karakterrel lehet kommentezni a sorokban.
 * A fölösleges szóközöket az objektumot létrehozó metódus levágja.
 * @author zoli
 */
public class Config {
    
    private Map<String, String> values;
    
    /**
     * Az értékeket tartalmazó felsorolás kulcsai, amit a program használ.
     */
    public static final String KEY_PORT = "port",
                               KEY_CA = "ca",
                               KEY_CERT = "cert",
                               KEY_KEY = "key",
                               KEY_PASSWORD = "password",
                               KEY_ROOT = "root";
    
    /**
     * Általános rendszerváltozók.
     */
    private static final String LS = System.getProperty("line.separator"),
                                UD = System.getProperty("user.dir");
    
    /**
     * A soronként érvényes megjegyzés karakter.
     */
    private static final String CC = "#";
    
    /**
     * A konfig fájl eléréséhez létrehozott objektum.
     */
    public static final File FILE_CONFIG = new File(UD, "bridge.conf");
    
    /**
     * Az alapértelmezett fájl tartalma.
     */
    private static final String DEFAULT_CONFIG =
            CC + " Ez a fájl a távirányítós autókat vezérlő telefonokat (host) és a számítógépen futó vezérlő programokat (controller) összekötő szerver (híd) konfigurációs állománya." + LS +
            KEY_PORT + " 8443 " + CC + " az a TCP port, amin a szerver figyel" + LS +
            KEY_CA + ' ' + new File("test-certs", "ca.crt") + ' ' + CC + " a tanúsítványokat kiállító CA tanúsítvány-fájl" + LS +
            KEY_CERT + ' ' + new File("test-certs", "bridge.crt") + ' ' + CC + " a szerver tanúsítvány-fájl" + LS +
            KEY_KEY + ' ' + new File("test-certs", "bridge.key") + ' ' + CC + " a szerver titkos kulcsa" + LS +
            CC + ' ' + KEY_PASSWORD + " optional_cert_password " + CC + " a szerver tanúsítványának jelszava, ha van" + LS +
            CC + ' ' + KEY_ROOT + " optional_admin_cert_common_name " + CC + " kitüntetett tanúsítvány, amivel rendszergazdaként használható a program";
    
    /**
     * Ideignlenes jelszó a memóriában.
     */
    private char[] password = null;
    
    /**
     * Ez az osztály nem példányosítható kívülről és nem származhatnak belőle újabb osztályok.
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
            int port = Integer.parseInt(getValues().get(KEY_PORT));
            if (port < 1 || port > 65535) return null;
            return port;
        }
        catch (NullPointerException ex) {
            return null;
        }
        catch (NumberFormatException ex) {
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
     * A CA által kiállított érvényes tanúsítvány helye.
     * @return fájl vagy NULL, ha a fájl nem létezik
     */
    public File getCertFile() {
        return createFile(KEY_CERT);
    }
    
    /**
     * A konfigurációs fájl key paramétere.
     * A CA által kiállított érvényes tanúsítvány titkos kulcsának helye.
     * @return fájl vagy NULL, ha a fájl nem létezik
     */
    public File getKeyFile() {
        return createFile(KEY_KEY);
    }
    
    /**
     * Az opcionális tanúsítvány jelszó értékét adja vissza.
     * Ha nincs megadva, üres karakterlánccal tér vissza.
     */
    public char[] getPassword() {
        if (password != null) return password;
        if (getValues() == null || getValues().get(KEY_PASSWORD) == null) return new char[] {};
        return getValues().get(KEY_PASSWORD).toCharArray();
    }
    
    /**
     * A memóriában tárolja a megadott jelszót.
     */
    public void setPassword(char[] password) {
        this.password = password;
    }
    
    /**
     * Az opcionális rendszergazda tanúsítványában szereplő CN mező értékét adja vissza.
     * Ez a felhasználó a szerver rendszergazdáját jelöli és minden műveletre fel van hatalmazva.
     * Ha nincs megadva, üres szöveggel tér vissza és nem lesz a szervernek rendszergazdája.
     */
    public String getRootName() {
        if (getValues() == null) return null;
        String name = getValues().get(KEY_ROOT);
        if (name == null) return "";
        return name;
    }
    
    /**
     * A konfigurációs fájl paraméterei és azok értékei.
     * @return A paramétereket tartalmazó felsorolás vagy NULL, ha a konfig fájl nem létezik.
     */
    public Map<String, String> getValues() {
        return values;
    }

    /**
     * @return true, ha minden érték meg van adva és érvényes, egyébként false
     */
    public boolean isCorrect() {
        return getPort() != null &&
               getCAFile() != null &&
               getCertFile() != null &&
               getKeyFile() != null;
    }
    
    /**
     * @return true, ha a konfig fájl most lett létrehozva
     */
    public boolean isNew() {
        return getValues() == null;
    }
    
    /**
     * Tesztelési célból értelmes szöveget generál a metódus az objektumnak.
     */
    @Override
    public String toString() {
        return "Port: " + getPort() + LS +
               "CA file: " + getCAFile() + LS +
               "Cert file: " + getCertFile() + LS +
               "Key file:" + getKeyFile() + LS +
               "Password length: " + (getPassword() == null ? -1 : getPassword().length) + LS +
               "Root name: " + getRootName() + LS +
               "Correct? " + isCorrect();
    }
    
    /**
     * Gyártó metódus.
     * @throws RuntimeException ha bármi hiba történik. Pl. nincs olvasási/írási jog
     */
    public static Config getInstance() {
        Config config = new Config();
        List<String> conf = read(FILE_CONFIG, DEFAULT_CONFIG);
        if (conf != null) {
            int ind;
            String key, val;
            config.values = new HashMap<String, String>();
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
            List<String> ls = new ArrayList<String>();
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            while ((ln = in.readLine()) != null) {
                ln = ln.trim();
                if (ln.startsWith(CC)) continue;
                ind = ln.indexOf(CC);
                if (ind != -1) ln = ln.substring(0, ind);
                if (!ln.isEmpty()) ls.add(ln);
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
        if (getValues() == null) return null;
        String path = getValues().get(key);
        if (path == null) return null;
        File f = new File(path);
        if (!f.exists() || !f.isFile()) return null;
        return f;
    }
    
}
