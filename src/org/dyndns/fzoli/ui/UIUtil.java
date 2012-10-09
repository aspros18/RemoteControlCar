package org.dyndns.fzoli.ui;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Általános UI metódusok, amik kellhetnek több helyen is.
 * @author zoli
 */
public class UIUtil {
    
    /**
     * Általános rendszerváltozók.
     */
    private static final String LS = System.getProperty("line.separator");
    
    /**
     * Egy tályékoztató szöveget jelenít meg a felhasználónak.
     * Ha a grafikus felület elérhető, modális ablakban jelenik meg az üzenet,
     * különben a kimenet streamre megy ki a fejléc és a szöveg.
     * Ha a kimeneti stream System.err, akkor hibaüzenetes ablakikon,
     * egyébként figyelmeztetőikon kerül az ablakra.
     * @param title a fejléc
     * @param text a megjelenő szöveg
     * @param out a kimenet stream
     */
    public static void alert(String title, String text, PrintStream out) {
        alert(title, text, out, null);
    }
    
    /**
     * Egy tályékoztató szöveget jelenít meg a felhasználónak.
     * Ha a grafikus felület elérhető, modális ablakban jelenik meg az üzenet,
     * különben a kimenet streamre megy ki a fejléc és a szöveg.
     * Ha a kimeneti stream System.err, akkor hibaüzenetes ablakikon,
     * egyébként figyelmeztetőikon kerül az ablakra.
     * @param title a fejléc
     * @param text a megjelenő szöveg
     * @param out a kimenet stream
     * @param icon a megjelenő ablak fejlécében megjelenő ikon
     */
    public static void alert(String title, String text, PrintStream out, Image icon) {
        if (GraphicsEnvironment.isHeadless()) { // ha nem elérhető a grafikus felület
            print(title, text, out);
        }
        else { // ha van grafikus felület
            JFrame dummy = null;
            if (icon != null) {
                dummy = new JFrame();
                dummy.setIconImage(icon);
            }
            JOptionPane.showMessageDialog(dummy, text, title, System.err == out ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE); // dialógus ablak megjelenítése
        }
    }
    
    /**
     * Paraméter alapján előállítja a kimenetet, és a megadott kimenetre kiírja.
     * @param title a fejléc
     * @param text az üzenet
     * @patam out a kimenet
     */
    public static void print(String title, String text, PrintStream out) {
        out.println(LS + title + ':'); // fejléc, aztán ...
        out.println(text + LS); // ... kimenet streamre írás
    }
    
    /**
     * Az adott rendszer alapértelmezett kinézetét alkalmazza feltéve, ha elérhető a grafikus felület.
     */
    public static void setSystemLookAndFeel() {
        if (!GraphicsEnvironment.isHeadless()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
            catch (Exception ex) {
                ;
            }
        }
    }
    
}