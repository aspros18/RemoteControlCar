package org.dyndns.fzoli.rccar.controller;

import java.io.File;

/**
 * A vezérlő konfigurációját tölti be és menti el a felhasználó könyvtárába egy fájlba.
 * Ha a fájl nem létezik, létrehozza az alapértelmezett adatokkal.
 * @author zoli
 */
public class Config {
    
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
    public File getCa() {
        return ca;
    }

    /**
     * Az a tanúsítvány, amivel a program kapcsolódik a szerverhez.
     */
    public File getCert() {
        return cert;
    }

    /**
     * A tanúsítvány titkos kulcsa.
     */
    public File getKey() {
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
        return true; //TODO
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
    public void setCa(File ca) {
        this.ca = ca;
    }

    /**
     * Beállítja a használandó tanúsítványt.
     */
    public void setCert(File cert) {
        this.cert = cert;
    }

    public void setKey(File key) {
        this.key = key;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
    
    /**
     * A szerializált fájlból beolvassa az adatokat.
     * Ha nem létezik a fájl, az alapértelmezett adatokkal tér vissza.
     */
    public static Config read() {
        return null; //TODO
    }
    
    /**
     * A konfigurációt tartalmazó objektumot fájlba szerializálja.
     * Ha a fájl nem létezik, szerializálás előtt létrehozza.
     */
    public static void save(Config config) {
        ; //TODO
    }
    
}
