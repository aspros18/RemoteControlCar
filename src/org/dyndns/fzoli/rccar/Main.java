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
import java.io.IOException;
import java.io.InputStreamReader;
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
 * @author zoli
 */
public class Main extends JFrame {
    
    /**
     * Az alkalmazás indítása előtt
     * az alkalmazásnév és a LookAndFeel beállítódik.
     */
    static {
        setApplicationName("Mobile-RC");
        setSystemLookAndFeel();
    }

    /**
     * A felületről kiválasztott alkalmazás típusa.
     */
    private String selectedApp;
    
    /**
     * Az alkalmazásválasztó-ablak konstruktora.
     */
    public Main() throws HeadlessException {
        super("Application chooser");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(org.dyndns.fzoli.rccar.resource.R.getImage(null, "app-chooser.png"));
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout());
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        
        c.gridwidth = 2;
        add(new JLabel("Please select an application by clicking to one of the button.", SwingConstants.CENTER), c);
        
        c.gridwidth = 1;
        c.weighty = 1;
        c.gridy = 1;
        
        class AppButton extends JButton { // az alkalmazásindító gombok osztálya
            
            public AppButton(String text, BufferedImage imgIcon, final String app) {
                super(text, new ImageIcon(Scalr.resize(imgIcon, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.AUTOMATIC, 64, Scalr.OP_ANTIALIAS)));
                setHorizontalTextPosition(SwingConstants.CENTER);
                setVerticalTextPosition(SwingConstants.BOTTOM);
                addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        selectedApp = app;
                        Main.this.dispose();
                    }
                    
                });
            }
            
        }
        
        add(new AppButton("Controller", org.dyndns.fzoli.rccar.controller.resource.R.getIconImage(), "client"), c); // vezérlő indító gomb
        
        c.gridx = 1;
        add(new AppButton("Bridge", org.dyndns.fzoli.rccar.bridge.resource.R.getBridgeImage(), "server"), c); // Híd indító gomb
        
        pack();
        setLocationRelativeTo(this);
        setResizable(false);
        setVisible(true);
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
            // ha ki lett választva az alkalmazás, alkalmazás indítása
            if (main.selectedApp != null) {
                runApp(main.selectedApp, args);
            }
        }
    }
    
}
