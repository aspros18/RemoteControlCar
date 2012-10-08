package org.dyndns.fzoli.rccar.controller;

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.IconTextPanel;
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
     * Folyamatjelző panel.
     */
    private final JPanel pProgress = new IconTextPanel(this, R.getIndicatorImageIcon(), "Kapcsolódás folyamatban...");
    
    /**
     * Hibát kijelző panel.
     */
    private final JPanel pError = new IconTextPanel(this, LookAndFeelIcon.createIcon(this, "OptionPane.errorIcon", null), "Nem sikerült kapcsolódni a szerverhez!");
    
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
