package org.dyndns.fzoli.rccar.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.IconTextPanel;
import org.dyndns.fzoli.ui.LookAndFeelIcon;
import org.dyndns.fzoli.ui.OkCancelPanel;

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
     * Folyamatjelző panel.
     */
    public final IconTextPanel pProgress = new IconTextPanel(this, R.getIndicatorImageIcon(), "Kapcsolódás folyamatban...");
    
    /**
     * Hibát kijelző panel.
     */
    public final IconTextPanel pError = new IconTextPanel(this, LookAndFeelIcon.createIcon(this, "OptionPane.errorIcon", null), "Nem sikerült kapcsolódni a szerverhez!");
    
    /**
     * Újra gomb.
     */
    private final JButton btAgain = new JButton("Újra");
    
    /**
     * Kapcsolatbeállítás gomb.
     */
    private final JButton btSettings = new JButton("Kapcsolatbeállítás");
    
    /**
     * Kilépés gomb.
     */
    private final JButton btExit = new JButton("Kilépés");
    
    private final JPanel pContainer = new JPanel(new GridLayout());
    
    /**
     * TODO
     * Konstruktor.
     */
    public ConnectionProgressFrame() {
        super("Kapcsolódáskezelő");
        setIconImage(R.getIconImage());
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        pContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(pContainer, c);
        c.gridy = 1;
        JPanel pButtons = new OkCancelPanel(btAgain, btExit, btSettings, 5);
        pButtons.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        add(pButtons, c);
        setPanel(pError);
        pack();
        setLocationRelativeTo(this);
        btAgain.requestFocus();
    }
    
    public void setPanel(IconTextPanel p) {
        pContainer.removeAll();
        pContainer.add(p);
    }
    
}
