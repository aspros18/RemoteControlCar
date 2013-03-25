package org.dyndns.fzoli.rccar.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import org.dyndns.fzoli.rccar.ui.UIUtil;
import org.dyndns.fzoli.resource.MD5Checksum;
import org.dyndns.fzoli.ui.systemtray.SystemTrayIcon;
import org.dyndns.fzoli.ui.systemtray.TrayIcon;

/**
 * Ideiglenes konfiguráció a memóriában.
 */
class MemConfig {
    
    /**
     * Tanúsítvány jelszó.
     */
    private static char[] password;

    /**
     * Megadja a tanúsítvány jelszavát.
     * Ha az érték nincs megadva, akkor a paraméterben átadott értékkel tér vissza.
     */
    protected static char[] getPassword(char[] def) {
        if (password == null) return def;
        return password;
    }

    /**
     * Beállítja a tanúsítvány jelszavát.
     * Ha null érték adódik át, a tároló úgy veszi, nincs megadva a jelszó.
     */
    protected static void setPassword(char[] password) {
        MemConfig.password = password;
    }
    
}

/**
 * A vezérlő konfigurációját tölti be és menti el a felhasználó appdata könyvtárába egy fájlba.
 * Az alkalmazásindító-fájl mellé tehető egy {@code controller.ser} nevű fájl,
 * amiből az alapértelmezett Config objektum töltődik be, ha nem tartozik konfig a felhasználóhoz.
 * Ha a felhasználóhoz tartozik konfiguráció, akkor az alapértelmezett konfiguráció nem töltődik be.
 * @author zoli
 */
public class Config implements Serializable , org.dyndns.fzoli.rccar.clients.ClientConfig {
    
    /**
     * Változók deklarálása és az alapértelmezések beállítása.
     */
    private int port = 8443;
    private char[] password = null;
    private String address = "localhost";
    private File ca = new File("test-certs", "ca.crt");
    private File cert = new File("test-certs", "controller.crt");
    private File key = new File("test-certs", "controller.key");
    private Locale l;
    
    /**
     * Az alapértelmezett crt fájlok ujjlenyomatait tartalmazó tömb.
     * Arra kellenek, hogy detektálni lehessen, hogy az alapértelmezett crt fájl van-e használva.
     */
    private static final String[] DEFAULT_MD5_SUMS = {
        "cdd3bb2582891a0a3648df8bde333b01",
        "2d561463be9984ebb7e3f93637f5629f",
        "256799a47b53bbbdb3f47e13296e8982",
        "80ef24662cef2cda28776bbbe846d611",
        "5b3e0eba53a6911ac66b372e75028b9f",
        "213ecbe458669ef1d421c042fae71c3d",
        "f92448f855bf7c2b78c73108bb41103b",
        "012d65bb6f7ab6413a9f6f7fe4ba5741",
        "0f8fee741445bfc18e2fc7056e892cf0",
        "2f80d63448093efc150a061a87ea3ab6"
    };
    
    /**
     * Az alkalmazás adattárolásra használt könyvtára.
     */
    public static final File ROOT = new File(getUserDataFolder("Mobile-RC"));
    
    /**
     * Az a fájl, amelybe a szerializálás történik.
     */
    public static final File STORE_FILE = new File(ROOT, "controller.ser");
    
    /**
     * Az alapértelmezett konfigurációra mutató fájl.
     */
    private static final File DEF_FILE = UIUtil.createFile("controller.ser");
    
    /**
     * Az alapértelmezett konfiguráció.
     */
    private static final Config DEFAULT = new Config();
    
    /**
     * Új sor jel.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * Ez az osztály nem példányosítható kívülről és nem származhatnak belőle újabb osztályok.
     */
    private Config() {
    }

    /**
     * A szerver címe.
     */
    @Override
    public String getAddress() {
        return address;
    }

    /**
     * A szerver portja.
     */
    @Override
    public Integer getPort() {
        return port;
    }

    /**
     * Az egyetlen megbízható tanúsítvány-kiállító.
     */
    @Override
    public File getCAFile() {
        return getFile(ca);
    }

    /**
     * Az a tanúsítvány, amivel a program kapcsolódik a szerverhez.
     */
    @Override
    public File getCertFile() {
        return getFile(cert);
    }

    /**
     * A tanúsítvány titkos kulcsa.
     */
    @Override
    public File getKeyFile() {
        return getFile(key);
    }

    /**
     * A tanúsítvány jelszava.
     * Ha a jelszó nincs megadva, üres jelszóval tér vissza.
     */
    @Override
    public char[] getPassword() {
        char[] pass = MemConfig.getPassword(password);
        if (pass == null) return new char[] {};
        return pass;
    }

    /**
     * Megmondja, hogy a konfigurációban a jelszó be van-e állítva.
     */
    public boolean isPasswordStored() {
        return password != null;
    }
    
    /**
     * Ha mindkét objektum null, true;
     * Ha csak az egyik objektum null, false;
     * Ha a fenti két eset nem teljesül, akkor true, ha egyenlőek.
     */
    private boolean equals(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null ^ o2 == null) return false;
        return o1.equals(o2);
    }
    
    /**
     * Összehasonlítja a konfiguráció értékeit a paraméterben megadottakkal és ha a jelszó kivételével mind egyezik, igazzal tér vissza.
     */
    public boolean equals(String address, int port, File ca, File cert, File key) {
        return equals(address, port, ca, cert, key, getPassword());
    }
    
    /**
     * Összehasonlítja a konfiguráció értékeit a paraméterben megadottakkal és ha mind egyezik, igazzal tér vissza.
     */
    public boolean equals(String address, int port, File ca, File cert, File key, char[] password) {
        return equals(getAddress(), address) &&
               equals(getCAFile(), ca) &&
               equals(getCertFile(), cert) &&
               equals(getKeyFile(), key) &&
               equals(new String(getPassword()), new String(password)) &&
               getPort() == port;
    }
    
    /**
     * Két konfiguráció azonos, ha minden paraméterük megegyezik.
     */
    public boolean equals(Config o) {
        Config conf = (Config) o;
        return equals(conf.getAddress(), conf.getPort(), conf.getCAFile(), conf.getCertFile(), conf.getKeyFile(), conf.getPassword());
    }
    
    /**
     * Megmondja, hogy a konfiguráció megegyezik-e az alapértelmezettel.
     */
    public boolean isDefault() {
        return equals(DEFAULT) && isCertDefault();
    }

    /**
     * Megmondja, hogy az alapértelmezett tanúsítvány van-e beállítva.
     */
    public boolean isCertDefault() {
        try {
            String md5Sum = MD5Checksum.getMD5Checksum(getCertFile());
            for (String s : DEFAULT_MD5_SUMS) {
                if (s.equals(md5Sum)) return true;
            }
            return false;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
    /**
     * Megmondja hogy léteznek-e a tanúsítvány fájlok.
     */
    public boolean isFileExists() {
        return isCAFileExists() && isCertFileExists() && isKeyFileExists();
    }
    
    /**
     * Megmondja hogy a tanúsítvány-kiállító fájl létezik-e.
     */
    public boolean isCAFileExists() {
        return getCAFile() != null;
    }
    
    /**
     * Megmondja hogy a tanúsítvány fájl létezik-e.
     */
    public boolean isCertFileExists() {
        return getCertFile() != null;
    }
    
    /**
     * Megmondja hogy a tanúsítvány-kulcs fájl létezik-e.
     */
    public boolean isKeyFileExists() {
        return getKeyFile() != null;
    }
    
    /**
     * Megadja az alkalmazás nyelvét.
     */
    public Locale getLanguage() {
        return l == null ? Locale.getDefault() : l;
    }
    
    /**
     * Beállítja az alkalmazás nyelvét.
     */
    public void setLanguage(Locale l) {
        this.l = l;
    }
    
    /**
     * Beállítja a szerver címét.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Beállítja a szerver portját.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Beállítja az egyetlen megbízahtó tanúsítvány-kiállítót.
     */
    public void setCAFile(File ca) {
        this.ca = ca;
    }

    /**
     * Beállítja a használandó tanúsítványt.
     */
    public void setCertFile(File cert) {
        this.cert = cert;
    }

    /**
     * Beállítja a használandó tanúsítvány titkos kulcsát.
     */
    public void setKeyFile(File key) {
        this.key = key;
    }

    /**
     * Beállítja a használandó tanúsítvány jelszavát.
     * @param password a jelszó
     * @param save true esetén az objektum változójában tárolódik az érték, egyébként az ideiglenes konfigurációban
     */
    public void setPassword(char[] password, boolean save) {
        if (save) {
            this.password = password;
            MemConfig.setPassword(null);
        }
        else {
            MemConfig.setPassword(password);
        }
    }
    
    /**
     * Tesztelési célból értelmes szöveget generál a metódus az objektumnak.
     */
    @Override
    public String toString() {
        return "Address: " + getAddress() + LS +
               "Port: " + getPort() + LS +
               "CA file: " + getCAFile() + LS +
               "Cert file: " + getCertFile() + LS +
               "Key file:" + getKeyFile() + LS +
               "Password length: " + (getPassword() == null ? -1 : getPassword().length) + LS +
               "Default? " + isDefault();
    }
    
    /**
     * @return null, ha nem létezik a fájl, egyébként a megadott fájl
     */
    private static File getFile(File f) {
        return f.isFile() ? f : null;
    }
    
    /**
     * Gyártó metódus.
     * A szerializált fájlból beolvassa az adatokat.
     * Ha nem létezik a fájl vagy nem olvasható, az alapértelmezett adatokkal tér vissza.
     * Ha a fájl létezik, de nem Config objektum van benne, a fájl felülíródik.
     */
    public static Config getInstance() {
        try {
            if (STORE_FILE.isFile()) {
                return read(STORE_FILE, true);
            }
            else {
                if (DEF_FILE.isFile()) return read(DEF_FILE, false);
                return new Config();
            }
        }
        catch (Exception ex) {
            return new Config();
        }
    }
    
    /**
     * A kért fájlból megpróbálja létrehozni a konfigurációt.
     * @param f a fájl, amiből olvas
     * @param saveOnError ha olvasás közben hiba történt, írja-e felül a fájlt az alapértelmezéssel
     */
    private static Config read(File f, boolean saveOnError) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream oin = new ObjectInputStream(fis);
        Config config;
        try {
            config = (Config) oin.readObject();
        }
        catch (Exception ex) {
            config = new Config();
            if (saveOnError) save(config);
        }
        oin.close();
        fis.close();
        return config;
    }
    
    /**
     * A konfigurációt tartalmazó objektumot fájlba szerializálja.
     * Ha a fájl nem létezik, szerializálás előtt létrehozza.
     * @return true, ha sikerült a szerializálás, egyébként false
     */
    public static boolean save(Config config) {
        try {
            if (!ROOT.exists()) ROOT.mkdirs();
            FileOutputStream fos = new FileOutputStream(STORE_FILE, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(config);
            oos.flush();
            oos.close();
            fos.close();
            return true;
        }
        catch (Exception ex) {
            SystemTrayIcon.showMessage(Main.getString("error"), Main.getString("config_save_error1") + ' ' + Main.LS + Main.getString("config_save_error2"), TrayIcon.IconType.ERROR);
            return false;
        }
    }

    /**
     * Megadja, hogy a konfiguráció helyes-e.
     * A vezérlő program konfigurációja alapértelmezetten helyesen van beállítva és
     * a grafikus felület nem enged meg helytelen beállítást, ezért csak azt kell vizsgálni, hogy
     * a tanúsítvány-fájlok léteznek-e.
     * @return true esetén használható a konfiguráció
     */
    @Override
    public boolean isCorrect() {
        return isFileExists();
    }
    
    /**
     * Megadja a felhasználóhoz tartozó, adattárolásra használható könyvtárat.
     * @param name a kért könyvtár neve
     */
    private static String getUserDataFolder(String name) {
        String path;
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN")) {
            return org.appkit.osdependant.OSUtils.userDataFolder(name);
        }
        else if (OS.contains("MAC")) {
            path = System.getProperty("user.home") + File.separator + "Library" + File.separator + "Application Support";
        }
        else if (OS.contains("NUX")) {
            String cfg = System.getenv("XDG_CONFIG_HOME");
            if (cfg == null || cfg.isEmpty()) cfg = System.getProperty("user.home") + File.separator + ".config";
            path = cfg;
        }
        else {
            path = System.getProperty("user.dir");
        }
        return path + File.separator + name + File.separator;
    }
    
}
