package org.dyndns.fzoli.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
* Olyan panel, melyen bal oldalt egy ikon található és az mellett egy szöveg.
* Az ikon és szöveg mérete a többi panel ikonjától függ és mindnek ugyan akkora a mérete.
* A legnagyobb ikon- illetve szövegcímke érvényesül a többi panel címkéire.
* @author zoli
*/
public class IconTextPanel extends JPanel {

    /**
     * A paneleket tartalmazó lista.
     */
    private static final List<IconTextPanel> panels = new ArrayList<IconTextPanel>();

    /**
     * A panelen megjelenő komponensek.
     */
    private final JLabel lbIcon, lbText;

    /**
     * Az a komponens, melyre rákerül a panel.
     */
    private final Component owner;

    /**
     * Konstruktor.
     * @param owner az a komponens, melyre rákerül a panel
     * @param icon az ikon, ami a panel bal szélén jelenik meg
     * @param text a szöveg, ami az ikon mellé kerül
     */
    public IconTextPanel(Component owner, Icon icon, String text) {
        super(new GridBagLayout());
        this.owner = owner; // tulajdonos referencia megszerzése
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
     * Csak azokat a paneleket veszi figyelembe, melyek tulajdonosa megegyezik az övével.
     */
    private void resizeComponents() {
        Dimension iconSize = new Dimension(1, 1);
        Dimension panelSize = new Dimension(1, 1);
        for (IconTextPanel panel : panels) {
            if (panel.owner != owner) continue;
            setMaxSize(panel.lbIcon, iconSize);
            setMaxSize(panel.lbText, panelSize);
        }
        for (IconTextPanel panel : panels) {
            if (panel.owner != owner) continue;
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
