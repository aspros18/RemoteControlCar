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
        
        private final JLabel lbIcon;
        
        public ConnProgPanel(Icon icon, String text) {
            super(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.anchor = GridBagConstraints.LINE_START;
            c.fill = GridBagConstraints.NONE;
            c.weightx = 1;
            c.insets = new Insets(5, 5, 5, 5);
            lbIcon = new JLabel(icon);
            add(lbIcon, c);
            JLabel lbText = new JLabel(text);
            lbText.setHorizontalAlignment(SwingConstants.LEFT);
            c.weightx = Integer.MAX_VALUE;
            c.fill = GridBagConstraints.HORIZONTAL;
            add(lbText, c);
            panels.add(this);
            Dimension iconSize = new Dimension(1, 1);
            Dimension panelSize = new Dimension(1, 1);
            for (ConnProgPanel panel : panels) {
                setMaxSize(panel, panelSize);
                //setMaxSize(panel.lbIcon, iconSize);
            }
            for (ConnProgPanel panel : panels) {
                panel.setPreferredSize(panelSize);
                //panel.lbIcon.setPreferredSize(iconSize);
            }
        }
        
        private static void setMaxSize(Component c, Dimension panelSize) {
            Dimension d = c.getPreferredSize();
            if (d.getWidth() > panelSize.getWidth()) panelSize.width = (int) d.getWidth();
            if (d.getHeight() > panelSize.getHeight()) panelSize.height = (int) d.getHeight();
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
