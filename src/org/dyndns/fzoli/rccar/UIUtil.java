package org.dyndns.fzoli.rccar;

import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.Console;
import java.io.PrintStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
     * Bekéri a tanúsítvány jelszavát a felhasználótól.
     * Ha a grafikus felület elérhető, dialógus ablakban kéri be a jelszót,
     * egyébként megpróbálja konzolról bekérni a jelszót.
     * Ha nincs se konzol, se grafikus felület, a program kilép.
     * Ha a dialógus ablakon nem az OK-ra kattintottak, a program kilép.
     */
    public static char[] showPasswordInput(Image icon) {
        String message = "A tanúsítvány beolvasása sikertelen volt.";
        String request = "Adja meg a tanúsítvány jelszavát, ha van: ";
        if (GraphicsEnvironment.isHeadless()) {
            Console console = System.console();
            if (console == null) {
                alert("Hiba", "A tanúsítvány jelszava nem állítható be.", System.err);
                System.exit(1);
            }
            console.printf("%s%n", message);
            return console.readPassword(request);
        }
        else {
            JFrame dummyFrame = new JFrame();
            dummyFrame.setIconImage(icon);
            JPanel panel = new JPanel(new GridLayout(3, 1));
            JLabel lbMessage = new JLabel(message);
            JLabel lbRequest = new JLabel(request);
            JPasswordField pass = new JPasswordField(10);
            panel.add(lbMessage);
            panel.add(lbRequest);
            panel.add(pass);
            String[] options = new String[] {"OK", "Kilépés"};
            int option = JOptionPane.showOptionDialog(dummyFrame, panel, "Jelszó",
                JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, options, options[1]);
            if (option == 0) {
                return pass.getPassword();
            }
            else {
                System.exit(0);
            }
        }
        return null;
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
     */
    public static void alert(String title, String text, PrintStream out) {
        if (GraphicsEnvironment.isHeadless()) { // ha nem elérhető a grafikus felület
            print(title, text, out);
        }
        else { // ha van grafikus felület
            JOptionPane.showMessageDialog(null, text, title, System.err == out ? JOptionPane.ERROR_MESSAGE : JOptionPane.WARNING_MESSAGE); // dialógus ablak megjelenítése
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