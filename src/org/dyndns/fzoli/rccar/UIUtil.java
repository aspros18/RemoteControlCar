package org.dyndns.fzoli.rccar;

import java.awt.GraphicsEnvironment;
import java.io.PrintStream;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Általános UI metódusok, amik kellhetnek több helyen is.
 * @author zoli
 */
public class UIUtil {
    
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
        if (GraphicsEnvironment.isHeadless()) { // ha nem elérhető a grafikus felület
            out.println(title + ':'); // fejléc, aztán ...
            out.println(text); // ... kimenet streamre írás
        }
        else { // ha van grafikus felület
            JOptionPane.showMessageDialog(null, text, title, out == System.err ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE); // dialógus ablak megjelenítése
        }
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