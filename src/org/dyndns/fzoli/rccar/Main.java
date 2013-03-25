package org.dyndns.fzoli.rccar;

import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import static org.dyndns.fzoli.ui.UIUtil.setApplicationName;
import static org.dyndns.fzoli.ui.UIUtil.setSystemLookAndFeel;
import org.imgscalr.Scalr;

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
public class Main extends JFrame {
    
    /**
     * Az alkalmazás indítása előtt
     * az alkalmazásnév és a Look and Feel beállítódik.
     */
    static {
        setApplicationName("Mobile-RC");
        setSystemLookAndFeel();
        loadSWT();
    }

    /**
     * A felületről kiválasztott alkalmazás típusa.
     * (<code>client</code> vagy <code>server</code>)
     */
    private String selectedApp;
    
    /**
     * Az alkalmazásválasztó-ablak konstruktora.
     * Inicializálódik az ablak felülete, majd megjelenik az ablak.
     */
    public Main() throws HeadlessException {
        super("Application chooser"); // címsor-szöveg beállítása konstruktor segítségével
        setDefaultCloseOperation(DISPOSE_ON_CLOSE); // az ablak bezárása esetén felszabadítás
        setIconImage(org.dyndns.fzoli.rccar.resource.R.getImage(null, "app-chooser.png")); // címsor-ikon beállítása
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout()); // elrendezés-menedzser beállítása GBL-ra
        c.insets = new Insets(5, 5, 5, 5); // 5x5 pixel széles margó a komponensek között
        c.fill = GridBagConstraints.BOTH; // mindkét irányba helykitöltés engedélyezés
        c.weightx = 1; // kezdetben csak szélességében van teljes helykitöltés
        
        c.gridwidth = 2; // mivel 2 gomb van, 2 cellát foglal el az alkalmazásválasztásra felszólító szöveg
        add(new JLabel("Please select an application by clicking to one of the button.", SwingConstants.CENTER), c);
        
        c.gridwidth = 1; // a gombok 1 cellát foglalnak el
        c.weighty = 1; // hosszúságukban is kitöltve a maradék helyet
        c.gridy = 1; // és a felszólító szöveg alá kerülnek
        
        class AppButton extends JButton { // az alkalmazásindító gombok osztálya
            
            public AppButton(String text, BufferedImage imgIcon, final String app) {
                super(text, new ImageIcon(Scalr.resize(imgIcon, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, 64, Scalr.OP_ANTIALIAS))); // 64x64 méretű kép használata
                setHorizontalTextPosition(SwingConstants.CENTER); // a gombfelirat a gomb közepére és ...
                setVerticalTextPosition(SwingConstants.BOTTOM); // ... a kép alá kerül
                addActionListener(new ActionListener() {
                    
                    /** Alkalmazásválasztás esetén beállítódik a kiválasztott alkalmazás, az alkalmazásválasztó-ablak megszűnik és a main metódus folytatódik tovább. */
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedApp = app;
                        Main.this.dispose();
                    }
                    
                });
            }
            
        }
        
        add(new AppButton("Controller", org.dyndns.fzoli.rccar.controller.resource.R.getIconImage(), "client"), c); // a vezérlő indító gomb létrehozása
        
        c.gridx = 1;
        add(new AppButton("Bridge", org.dyndns.fzoli.rccar.bridge.resource.R.getBridgeImage(), "server"), c); // a Híd indító gomb létrehozása
        
        pack(); // minimális méret beállítása
        setLocationRelativeTo(this); // ablak középre helyezése
        setResizable(false); // átméretezés tiltása
        setVisible(true); // megjelenítés
    }
    
    /**
     * Megadja a külső SWT-jarfájl elérhetőségét.
     * Elsőként az aktuális könyvtárban keresi, ha ott nem találja, akkor az alkalmazást tartalmazó könyvtárban keres.
     * Az SWT-jarfájloknak ezen a könyvtáron belül egy <code>lib</code> nevű könyvtárban kell lenniük a következő fájlnevek egyikével:
     * swt-win32.jar; swt-win64.jar; swt-linux32.jar; swt-linux64.jar; swt-osx32.jar; swt-osx64.jar
     * @return null, ha a fájl nem található; egyébként a platformfüggő jarfájl útvonala
     */
    private static File getSwtJar() {
        File swtFile = null;
        String osName = System.getProperty("os.name").toLowerCase();
        String swtFileNameArchPart = System.getProperty("os.arch").toLowerCase().contains("64") ? "64" : "32";
        String swtFileNameOsPart = osName.contains("win") ? "win" : osName.contains("mac") ? "osx" : osName.contains("linux") || osName.contains("nix") ? "linux" : null;
        if (swtFileNameOsPart != null) {
            String swtFileName = "swt-" + swtFileNameOsPart + swtFileNameArchPart + ".jar";
            swtFile = new File("lib", swtFileName).getAbsoluteFile();
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
        }
        return swtFile;
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
     * A Híd- vagy a vezérlő-alkalmazást elindító metódus.
     * @see Main
     */
    public static void main(String[] args) throws InterruptedException {
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
                    System.err.print("Would you like to run the server application? (y/N) ");
                    answer = reader.readLine().trim(); // a megadott válasz tárolása
                }
                reader.close(); // bemenet felszabadítása
                // ha 'Y' a válasz, szerver alkalmazás indítása
                if (answer.equalsIgnoreCase("y")) runApp("server", args);
            }
            catch (IOException ex) {
                System.err.println("Input error."); // jelzi, ha hiba történt a bemenet olvasásakor
            }
        }
        else { // ha a grafikus felület elérhető
            // alkalmazásválasztó ablak megjelenítése
            Main main = new Main();
            // várakozás az alkalmazásválasztó-ablak bezárására
            while (main.isVisible()) {
                Thread.sleep(100);
            }
            String app = main.selectedApp; // a kiválasztott alkalmazás lekérése
            main = null; // az ablak referenciájának megszüntetése, hogy a GC törölhesse
            // ha ki lett választva az alkalmazás, alkalmazás indítása
            if (app != null) {
                runApp(app, args);
            }
        }
    }
    
}
