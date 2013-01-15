package org.dyndns.fzoli.rccar.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/**
 * Teszt arra, hogy a Java Date osztálya figyelembe veszi-e az eltérő időzónákat.
 * Ehhez egy fájlba mentem az időt, aztán átállítom az időzónát és újra futtatva
 * a programot megnézem az ementett dátumot, hogy követi-e az időzóna változását.
 * 
 * A teszt eredménye: igen, követi.
 * Tehát a szerver időt fogom a klienseken mutatni a chatüzenetek érkezéséhez,
 * mert ha a szerver ideje pontosan be van állítva, akkor minden kliensen
 * a pontos helyi időt fogja mutatni a program.
 * 
 * Első futás kimenete GMT+1 időzóna alatt:
 *   old: null
 *   now: Tue Jan 15 14:33:12 CET 2013
 * Második futás kimenete GMT+2 időzóna alatt:
 *   old: Tue Jan 15 15:33:12 EET 2013
 *   now: Tue Jan 15 15:33:28 EET 2013
 * Harmadik futása újra GMT+1 időzóna alatt:
 *   old: Tue Jan 15 14:33:28 CET 2013
 *   now: Tue Jan 15 14:34:09 CET 2013
 * 
 * @author zoli
 */
public class DateTest {
    
    private static final File STORE = new File("date.ser");
    
    public static void main(String[] args) {
        System.out.println("old: " + read());
        Date d = new Date();
        System.out.println("now: " + d);
        save(d);
    }
    
    public static Date read() {
        try {
            if (STORE.isFile()) {
                FileInputStream fis = new FileInputStream(STORE);
                ObjectInputStream oin = new ObjectInputStream(fis);
                Date d;
                try {
                    d = (Date) oin.readObject();
                }
                catch (Exception ex) {
                    return null;
                }
                oin.close();
                fis.close();
                return d;
            }
            else {
                return null;
            }
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public static boolean save(Date d) {
        try {
            FileOutputStream fos = new FileOutputStream(STORE, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(d);
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
