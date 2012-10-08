package org.dyndns.fzoli.rccar.controller;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.LookAndFeelIcon;

/**
 * Kapcsolódásjelző- és kezelő ablak.
 * Csak akkor jelenik meg, ha valamiért nem sikerült első alkalommal kapcsolódni a szerverhez.
 * A felhasználó lehetőségei:
 * - beállítja a konfigurációt: az ablak nem látható, míg be nem zárja a beállításokat
 * - újra próbálkozik kapcsolódni: indikátor jelzi a folyamatot és amíg tart, nem lehet újra próbálkozni
 * - kilép a programból: végetér a program futása
 * @author zoli
 */
public class ConnectionProgressFrame extends JFrame {

    /**
    * Olyan panel, melyen bal oldalt egy ikon található és az mellett egy szöveg.
    * Az ikon és szöveg mérete a többi panel ikonjától függ és mindnek ugyan akkora a mérete.
    * A legnagyobb ikon- illetve szövegcímke érvényesül a többi panel címkéire.
    */
    private static class ConnProgPanel extends JPanel {

        /**
         * A paneleket tartalmazó lista.
         */
        private static final List<ConnProgPanel> panels = new ArrayList<ConnProgPanel>();
        
        /**
         * A panelen megjelenő komponensek.
         */
        private final JLabel lbIcon, lbText;
        
        /**
         * Konstruktor.
         * @param icon az ikon, ami a panel bal szélén jelenik meg
         * @param text a szöveg, ami az ikon mellé kerül
         */
        public ConnProgPanel(Icon icon, String text) {
            super(new GridBagLayout());
            panels.add(this); // a panel nyílvántartásba vétele
            
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.LINE_START; // balra szélre horganyozva
            c.insets = new Insets(5, 5, 5, 5); // 5x5 pixeles margóval
            
            c.weightx = 1; // minimális térkitöltés
            c.fill = GridBagConstraints.NONE; // méretet nem váltóztatva
            lbIcon = new JLabel(icon); // ikon label létrehozása ...
            add(lbIcon, c); // ... és panelhez adás
            
            c.weightx = Integer.MAX_VALUE; // teljes teret kihasználva ...
            c.fill = GridBagConstraints.HORIZONTAL; // ... horizontálisan
            lbText = new JLabel(text); // üzenet label létrehozása
            lbText.setHorizontalAlignment(SwingConstants.LEFT); // szöveg balra igazítva
            add(lbText, c); // panelhez adás
            
            resizeComponents();
        }
        
        /**
         * Átméretezi a panelek címkéit.
         * Megkeresi a legnagyobb méretű címként és alkalmazza a többire a méretét.
         */
        private void resizeComponents() {
            Dimension iconSize = new Dimension(1, 1);
            Dimension panelSize = new Dimension(1, 1);
            for (ConnProgPanel panel : panels) {
                setMaxSize(panel.lbIcon, iconSize);
                setMaxSize(panel.lbText, panelSize);
            }
            for (ConnProgPanel panel : panels) {
                panel.lbIcon.setPreferredSize(iconSize);
                panel.lbText.setPreferredSize(panelSize);
            }
        }
        
        /**
         * Beállítja a maximum értéket.
         * @param component a komponens, amit vizsgál
         * @param size az aktuális méret, ami megnő, ha a vizsgált komponens mérete nagyobb
         */
        private static void setMaxSize(Component component, Dimension size) {
            Dimension d = component.getPreferredSize();
            if (d.getWidth() > size.getWidth()) size.width = (int) d.getWidth();
            if (d.getHeight() > size.getHeight()) size.height = (int) d.getHeight();
        }
        
    };
    
    /**
     * Folyamatjelző panel.
     */
    private final JPanel pProgress = new ConnProgPanel(R.getIndicatorImageIcon(), "Kapcsolódás folyamatban...");
    
    /**
     * Hibát kijelző panel.
     */
    private final JPanel pError = new ConnProgPanel(LookAndFeelIcon.createIcon(this, "OptionPane.errorIcon", null), "Nem sikerült kapcsolódni a szerverhez!");
    
    /**
     * TODO
     * Konstruktor.
     */
    public ConnectionProgressFrame() {
        super("Kapcsolódáskezelő");
        setIconImage(R.getIconImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        //setResizable(false);
        
        setLayout(new GridLayout(2, 1));
        
        add(pProgress);
        add(pError);
        pack();
        setLocationRelativeTo(this);
    }
    
}
