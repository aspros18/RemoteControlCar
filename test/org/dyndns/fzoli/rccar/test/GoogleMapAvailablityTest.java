package org.dyndns.fzoli.rccar.test;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Teszt annak kiderítésére, hogy elérhető-e a Google.
 * @author zoli
 */
public class GoogleMapAvailablityTest {

    /**
     * A kért webszerverhez próbál meg kapcsolódni és megadja, hogy sikerült-e.
     * A metódus nem tölti le a weboldalt, a kapcsolat sikeres létrehozása után
     * azonnal bezárja a kialakított kapcsolatot és igazzal tér vissza.
     * Ha nem sikerül a kapcsolatot létrehozni, mert a cím nem érhető el, vagy
     * nem sikerül a megadott időkorláton belül a kapcsolat létrehozása,
     * akkor hamissal tér vissza.
     * @param urlToCheck a weboldal pontos címe protokollal együtt
     * @param connTimeout a kapcsolat létrehozására megadott időkorlát
     */
    public static boolean checkAvailablity(String urlToCheck, int connTimeout) {
        URL url;
        HttpURLConnection conn;
        InputStream in = null;
        boolean result = true;
        try {
            url = new URL(urlToCheck);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(connTimeout);
            in = conn.getInputStream();
        }
        catch (Exception e) {
            result = false;
        }
        try {
            if (in != null) in.close();
        }
        catch (Exception ex) {
            ;
        }
        return result;
    }
    
    /**
     * A teszt 5 másodpercenként értékelődik ki, így legrosszabb esetben is
     * 15 másodpercenként kiíródik, hogy elérhető-e a Google webszervere.
     */
    public static void main(String[] args) throws Exception {
        while (true) {
            boolean chk = checkAvailablity("http://www.google.com", 10000);
            System.out.println(new Date() + " - " + chk);
            Thread.sleep(5000);
        }
    }
    
}
