package org.dyndns.fzoli.rccar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import static org.dyndns.fzoli.rccar.Main.res;
import org.dyndns.fzoli.rccar.resource.R;
import org.imgscalr.Scalr;

/**
 * Alkalmazásválasztó ablak.
 * Ha az alkalmazást paraméter megadása nélkül indítják, nem egyértelmű,
 * hogy a szervert vagy a kliens programot szeretnék-e használni.
 * Ekkor jelenik meg ez az alkalmazásválasztó ablak.
 * @author zoli
 */
public class AppChooserFrame extends JFrame {
    
    /**
     * A felületről kiválasztott alkalmazás típusa.
     * (<code>client</code> vagy <code>server</code>)
     */
    private String selectedApp;

    /**
     * Az alkalmazásválasztó-ablak konstruktora.
     * Inicializálódik az ablak felülete, majd megjelenik az ablak.
     */
    public AppChooserFrame() throws HeadlessException {
        super(res.getString("app-chooser")); // címsor-szöveg beállítása konstruktor segítségével
        setDefaultCloseOperation(EXIT_ON_CLOSE); // az ablak bezárása esetén kilépés a programból
        setIconImage(R.getImage(null, "app-chooser.png")); // címsor-ikon beállítása
        GridBagConstraints c = new GridBagConstraints();
        setLayout(new GridBagLayout()); // elrendezés-menedzser beállítása GBL-ra
        c.insets = new Insets(5, 5, 5, 5); // 5x5 pixel széles margó a komponensek között
        c.fill = GridBagConstraints.BOTH; // mindkét irányba helykitöltés engedélyezés
        c.weightx = 1; // kezdetben csak szélességében van teljes helykitöltés
        
        c.gridwidth = 2; // mivel 2 gomb van, 2 cellát foglal el az alkalmazásválasztásra felszólító szöveg
        add(new JLabel(res.getString("summary"), SwingConstants.CENTER), c);
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
                        AppChooserFrame.this.dispose();
                    }
                    
                });
            }
            
        }
        
        c.insets = new Insets(0, 5, 5, 5);
        add(new AppButton(res.getString("controller"), org.dyndns.fzoli.rccar.controller.resource.R.getIconImage(), "client"), c); // a vezérlő indító gomb létrehozása
        
        c.gridx = 1;
        c.insets = new Insets(0, 0, 5, 5);
        add(new AppButton(res.getString("bridge"), org.dyndns.fzoli.rccar.bridge.resource.R.getBridgeImage(), "server"), c); // a Híd indító gomb létrehozása
        
        pack(); // minimális méret beállítása
        setLocationRelativeTo(this); // ablak középre helyezése
        setResizable(false); // átméretezés tiltása
        setVisible(true); // megjelenítés
    }

    /**
     * Visszatér a kiválasztott alkalmazás típusával.
     * @return "client", "server" vagy null, ha még nem választottak
     */
    public String getSelectedApp() {
        return selectedApp;
    }
    
}
