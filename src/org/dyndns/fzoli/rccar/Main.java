package org.dyndns.fzoli.rccar;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import static org.dyndns.fzoli.util.OSUtils.setApplicationName;
import static org.dyndns.fzoli.ui.UIUtil.setSystemLookAndFeel;
import static org.dyndns.fzoli.rccar.SplashScreenLoader.setSplashMessage;
import org.dyndns.fzoli.rccar.ui.UIUtil;
import static org.dyndns.fzoli.rccar.ui.UIUtil.initNativeInterface;
import static org.dyndns.fzoli.rccar.ui.UIUtil.runNativeEventPump;
import static org.dyndns.fzoli.util.MacApplication.setDockIcon;

/**
 * A Híd- vagy a vezérlő-alkalmazás elindító osztálya.
 * Ha a parancsorban megadott első paraméter <code>client</code>,
 * akkor a vezérlő indul el; ha <code>server</code>, akkor a Híd.
 * Ha további paraméterek is meg lettek adva, az első paraméter kivételével
 * minden további paraméter átadódik a meghívandó alkalmazásnak.
 * Ha nincs megadva paraméter, vagy az első paraméter fentiek egyikére
 * se illeszkedik, akkor a program megjeleníti az alkalmazásválasztó-ablakot.
 * Ha nincs grafikus felület, megkérdi, hogy induljon-e el a szerver alkalmazás.
 * (Ajánlott az alkalmazás-paraméter megadása, mert ha meg van adva,
 * gyorsabban indul az alkalmazás és a splash-screen sem tűnik el, tehát
 * a felhasználó részletes információt kap a program betöltése közben.)
 * Mivel mindkét alkalmazás fel van készítve a (gyorsabb, szebb) SWT alapú
 * komponensek használatára, a program indulása előtt inicializálódik az SWT,
 * ha az elérhető; ha nem érhető el, SWT nélkül fut tovább mindkét program.
 * (SWT nélkül nem érhető el a natív rendszerikon és a webböngésző, ezért
 * AWT alapú rendszerikon lesz használva és a térkép tiltás alá kerül.)
 * @author zoli
 */
public class Main {
    
    /**
     * Az alkalmazásválasztó szótára.
     */
    static ResourceBundle res;
    
    /**
     * Megadja a külső SWT-jarfájl elérhetőségét.
     * Elsőként az aktuális könyvtárban keresi, ha ott nem találja, akkor az alkalmazást tartalmazó könyvtárban keres.
     * Az SWT-jarfájloknak ezen a könyvtáron belül egy <code>lib</code> nevű könyvtárban kell lenniük a következő fájlnevek egyikével:
     * swt-win32.jar; swt-win64.jar; swt-linux32.jar; swt-linux64.jar; swt-osx32.jar; swt-osx64.jar
     * Ha a fentiek közül egyik fájl sem illik a rendszerhez, akkor egyszerűen az swt.jar nevű fájlt adja vissza, ha az létezik.
     * @return null, ha a fájl nem található; egyébként a platformfüggő jarfájl útvonala
     */
    private static File getSwtJar() {
        String osName = System.getProperty("os.name").toLowerCase();
        String swtFileNameArchPart = System.getProperty("os.arch").toLowerCase().contains("64") ? "64" : "32";
        String swtFileNameOsPart = osName.contains("win") ? "win" : osName.contains("mac") ? "osx" : osName.contains("linux") || osName.contains("nix") ? "linux" : null;
        String swtFileName = swtFileNameOsPart != null ? "swt-" + swtFileNameOsPart + swtFileNameArchPart + ".jar" : "swt.jar";
        File swtFile = new File("lib", swtFileName).getAbsoluteFile();
        if (!swtFile.exists()) {
            try {
                File srcFile = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                swtFile = new File(new File(srcFile.getParentFile(), "lib"), swtFileName);
                if (!swtFile.exists()) swtFile = null;
            }
            catch (Exception ex) {
                swtFile = null;
            }
        }
        return swtFile;
    }
    
    /**
     * Szótár betöltése.
     * A szótár a rendszer nyelvén töltődik be, de ha a rendszernyelvhez nem tartozik szótár,
     * akkor az angol nyelvű szótár töltődik be.
     */
    private static void loadLanguage() {
        res = UIUtil.createResource("org.dyndns.fzoli.rccar.l10n.chooser", Locale.getDefault());
    }
    
    /**
     * Hozzáadja a classpath paramétereihez a platformfüggő SWT-jarfájlt, ha az nincs még hozzáadva.
     * A jarfájlok a <code>lib</code> könyvtáron belül vannak keresve.
     * @see #getSwtJar()
     */
    private static void loadSWT() {
        boolean available;
        try {
            Class.forName("org.eclipse.swt.SWT", false, Main.class.getClassLoader());
            available = true; // sikerült az osztály elérése, már elérhető az SWT
        }
        catch (ClassNotFoundException ex) {
            available = false; // még nem érhető el az SWT
        }
        if (!available) { // SWT-jarfájl útvonalának megadása, ha még nem érhető el
            File swtJar = getSwtJar(); // útvonal megszerzése
            if (swtJar == null) return; // ha az SWT-jarfájl nem található, kilépés
            try { // kísérlet a classpath kibővítésére
                URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class});
                method.setAccessible(true);
                method.invoke(sysloader, new Object[] {swtJar.toURI().toURL()});
            }
            catch (Throwable t) { // hiba esetén nincs mit tenni, a program SWT nélkül fut tovább
                ;
            }
        }
    }
    
    /**
     * A megadott alkalmazás indítása.
     * @param app az alkalmazás: <code>client</code> vagy <code>server</code>
     * @param args az alkalmazásnak átadandó paraméterek
     */
    private static boolean runApp(String app, String[] args) {
        // ha a kliens indítását kérték, kliens indítása paraméterátadással
        if ("client".equalsIgnoreCase(app)) {
            org.dyndns.fzoli.rccar.controller.Main.main(args);
            return true;
        }
        // ha a szerver indítását kérték, szerver indítása paraméterátadással
        if ("server".equalsIgnoreCase(app)) {
            org.dyndns.fzoli.rccar.bridge.Main.main(args);
            return true;
        }
        return false;
    }
    
    /**
     * Alkalmazásválasztó ablak.
     * Csak akkor jön létre, ha van grafikus környezet.
     */
    private static AppChooserFrame frame;
    
    /**
     * A Híd- vagy a vezérlő-alkalmazást elindító metódus.
     * Az ablak megjelenítése előtt a szótár,
     * az alkalmazásnév és a Look and Feel beállítódik.
     * @see Main
     */
    public static void main(final String[] args) {
        loadSWT(); // SWT lib betöltése, ha még nem történt meg
        setApplicationName("Mobile-RC"); // alkalmazásnév beállítása mielőtt AWT vagy SWT komponensek kerülnek használatra
        initNativeInterface(); // SWT alapú, natív interfész elindítása
        loadLanguage(); // szótár betöltése
        
        // ha legalább 1 paraméter meg lett adva
        if (args.length > 0) {
            // az első paraméter kivételével minden más
            // paramétert tartalmazó tömb létrehozása
            String[] arg = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                arg[i - 1] = args[i];
            }
            // paraméterben kért alkalmazás futtatása paraméterátadással és
            // ha az alkalmazás lefutott, kilépés a main metódusból
            if (runApp(args[0], arg)) {
                return;
            }
        }
        
        // ha nem lett paraméter megadva, vagy az első paraméter nem alkalmazás-paraméter
        if (GraphicsEnvironment.isHeadless()) { // ha csak a konzol érhető el
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // konzol bemenet olvasásához
            try {
                String answer = null; // kezdetben nincs válasz
                // addig kérdez, míg nincs helyes válasz megadva
                while (answer == null || !(answer.isEmpty() || answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("n"))) {
                    // megkérdi, induljon-e el a szerver alkalmazás
                    System.err.print(res.getString("bridge-run") + " (y/N) ");
                    answer = reader.readLine().trim(); // a megadott válasz tárolása
                }
                reader.close(); // bemenet felszabadítása
                // ha 'Y' a válasz, szerver alkalmazás indítása
                if (answer.equalsIgnoreCase("y")) runApp("server", args);
            }
            catch (IOException ex) {
                System.err.println(res.getString("input-error")); // jelzi, ha hiba történt a bemenet olvasásakor
            }
        }
        else { // ha a grafikus felület elérhető
            // jelzés a felhasználónak, hogy alkalmazásválasztás következik
            setSplashMessage(res.getString("loading"));
            // rendszer LAF beállítása
            setSystemLookAndFeel();
            // Mac OS X-en dock ikon beállítása az alkalmazásválasztó ikonjára
            setDockIcon(org.dyndns.fzoli.rccar.resource.R.getImage(null, "app-chooser.png"));
            new Thread(new Runnable() {

                @Override
                public void run() {
                    // várakozás az alkalmazásválasztó-ablak bezárására
                    while (frame == null || frame.isVisible()) {
                        try {
                            Thread.sleep(100);
                        }
                        catch (Exception ex) {
                            ;
                        }
                    }
                    String app = frame.getSelectedApp(); // a kiválasztott alkalmazás lekérése
                    frame = null; // az ablak referenciájának megszüntetése, hogy a GC törölhesse
                    // ha ki lett választva az alkalmazás, alkalmazás indítása
                    if (app != null) {
                        runApp(app, args);
                    }
                }
                
            }).start();
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    // alkalmazásválasztó ablak megjelenítése
                    frame = new AppChooserFrame();
                }
                
            });
            runNativeEventPump(); // a natív interfész eseménypumpálójának indítása
        }
    }
    
}
