package org.dyndns.fzoli.rccar.controller;

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
 * TODO
 * Kapcsolódásjelző- és kezelő ablak.
 * Csak akkor jelenik meg, ha valamiért nem sikerült első alkalommal kapcsolódni a szerverhez.
 * A felhasználó lehetőségei:
 * - beállítja a konfigurációt: az ablak nem látható, míg be nem zárja a beállításokat
 * - újra próbálkozik kapcsolódni: indikátor jelzi a folyamatot és amíg tart, nem lehet újra próbálkozni
 * - kilép a programból: végetér a program futása
 * @author zoli
 */
public class ConnectionProgressFrame extends JFrame {

    private static class ConnProgPanel extends JPanel {

        private static final List<ConnProgPanel> panels = new ArrayList<ConnProgPanel>();
        
        public ConnProgPanel(Icon icon, String text) {
            super(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.NONE;
            c.weightx = 1;
            c.insets = new Insets(5, 5, 5, 5);
            add(new JLabel(icon), c);
            JLabel lbText = new JLabel(text);
            lbText.setHorizontalAlignment(SwingConstants.LEFT);
            c.weightx = Integer.MAX_VALUE;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(lbText, c);
            panels.add(this);
            Dimension size = new Dimension(1, 1);
            for (ConnProgPanel panel : panels) {
                Dimension d = panel.getPreferredSize();
                if (d.getWidth() > size.getWidth()) size.width = (int) d.getWidth();
                if (d.getHeight() > size.getHeight()) size.height = (int) d.getHeight();
            }
            for (ConnProgPanel panel : panels) {
                panel.setPreferredSize(size);
            }
        }
        
    };
    
    /**
     * Folyamatjelző panel.
     */
    private final JPanel pProgress = new ConnProgPanel(R.getIndicatorImageIcon(), "Kapcsolódás folyamatban...");
    
    /**
     * Hibát kijelző panel.
     */
    private final JPanel pError = new ConnProgPanel(LookAndFeelIcon.createIcon(this, "OptionPane.errorIcon", R.getErrorImage()), "Nem sikerült kapcsolódni a szerverhez!");
    
    /**
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
