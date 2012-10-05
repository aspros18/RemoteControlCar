package org.dyndns.fzoli.rccar.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A vezérlő konfigurációját tölti be és menti el a felhasználó könyvtárába egy fájlba.
 * Ha a fájl nem létezik, létrehozza az alapértelmezett adatokkal.
 * @author zoli
 */
public class Config implements Serializable {
    
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
     * Az a fájl, amelybe a szerializálás történik.
     */
    private static final File STORE = new File(System.getProperty("user.home"), "mobile-rc.ser");
    
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
    public String getAddress() {
        return address;
    }

    /**
     * A szerver portja.
     */
    public int getPort() {
        return port;
    }

    /**
     * Az egyetlen megbízható tanúsítvány-kiállító.
     */
    public File getCAFile() {
        return ca;
    }

    /**
     * Az a tanúsítvány, amivel a program kapcsolódik a szerverhez.
     */
    public File getCertFile() {
        return cert;
    }

    /**
     * A tanúsítvány titkos kulcsa.
     */
    public File getKeyFile() {
        return key;
    }

    /**
     * A tanúsítvány jelszava.
     */
    public char[] getPassword() {
        return password;
    }

    /**
     * Megmondja, hogy a konfiguráció megegyezik-e az alapértelmezettel.
     */
    public boolean isDefault() {
        return DEFAULT.address.equals(address) &&
               DEFAULT.port == port &&
               DEFAULT.ca.equals(ca) &&
               DEFAULT.cert.equals(cert) &&
               DEFAULT.key.equals(key) &&
               new String(DEFAULT.password).equals(new String(password));
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
     * @return true, ha sikerült a szerializálás, egyébként false
     */
    public static boolean save(Config config) {
        try {
            FileOutputStream fos = new FileOutputStream(STORE, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(config);
            oos.flush();
            oos.close();
            fos.close();
            return true;
        }
        catch (Exception ex) {
            return false;
        }
    }
    
}
