package org.dyndns.fzoli.rccar.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.dyndns.fzoli.resource.MD5Checksum;

/**
 * A vezérlő konfigurációját tölti be és menti el a felhasználó könyvtárába egy fájlba.
 * Ha a fájl nem létezik, létrehozza az alapértelmezett adatokkal.
 * @author zoli
 */
public class Config implements Serializable , org.dyndns.fzoli.rccar.clients.ClientConfig {
    
    /**
     * Változók deklarálása és az alapértelmezések beállítása.
     */
    private int port = 8443;
    private char[] password = {};
    private String address = "localhost";
    private File ca = new File("test-certs", "ca.crt");
    private File cert = new File("test-certs", "controller.crt");
    private File key = new File("test-certs", "controller.key");
    
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
        "ea122f0df9f0a7ea228464e3806e472c",
        "e49658f52980092bb5a78ba978b27a9c",
        "17a673395afab9ee50573e3f3be9fdeb",
        "c911aad008cd50994256b46806947d64",
        "3e73992bb74083036459c08ff2cb7212"
    };
    
    /**
     * Az a fájl, amelybe a szerializálás történik.
     */
    private static final File STORE = new File("controller.ser");
    
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
     */
    @Override
    public char[] getPassword() {
        return password;
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
     */
    public void setPassword(char[] password) {
        this.password = password;
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
            if (STORE.isFile()) {
                FileInputStream fis = new FileInputStream(STORE);
                ObjectInputStream oin = new ObjectInputStream(fis);
                Config config;
                try {
                    config = (Config) oin.readObject();
                }
                catch (Exception ex) {
                    config = new Config();
                    save(config);
                }
                oin.close();
                fis.close();
                return config;
            }
            else {
                return new Config();
            }
        }
        catch (Exception ex) {
            return new Config();
        }
    }
    
    /**
     * A konfigurációt tartalmazó objektumot fájlba szerializálja.
     * Ha a fájl nem létezik, szerializálás előtt létrehozza.
     * A jelszó nem kerül mentésre.
     * @return true, ha sikerült a szerializálás, egyébként false
     */
    public static boolean save(Config config) {
        try {
            char[] tmp = config.getPassword();
            config.setPassword(null);
            FileOutputStream fos = new FileOutputStream(STORE, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(config);
            oos.flush();
            oos.close();
            fos.close();
            config.setPassword(tmp);
            return true;
        }
        catch (Exception ex) {
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
    
}
