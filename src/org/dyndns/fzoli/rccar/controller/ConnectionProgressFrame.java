package org.dyndns.fzoli.rccar.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private final JButton btAgain = new JButton("Újra") {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setPanel(pProgress); //teszt
                }
                
            });
        }
    };
    
    /**
     * Kapcsolatbeállítás gomb.
     */
    private final JButton btSettings = new JButton("Kapcsolatbeállítás") {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setPanel(pError); //teszt
                }
                
            });
        }
    };
    
    /**
     * Kilépés gomb.
     * Rákattintva a program végetér.
     */
    private final JButton btExit = new JButton("Kilépés") {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
                
            });
        }
    };
    
    /**
     * Az a panel, amelyen cserélgetődnek a {@code IconTextPanel} komponensek.
     */
    private final JPanel pContainer = new JPanel(new GridLayout());
    
    /**
     * Konstruktor.
     * Alapértelmezetten hibaüzenetet mutat a panel.
     */
    public ConnectionProgressFrame() {
        super("Kapcsolódáskezelő");
        setIconImage(R.getIconImage()); // kicsi ikon beállítása
        setLayout(new GridBagLayout()); // kedvenc elrendezésmenedzserem alkalmazása
        setDefaultCloseOperation(EXIT_ON_CLOSE); // X-re kattintva vége a programnak
        setResizable(false); // átméretezés tiltása
        setPanel(pError); // hibaüzenet mutatása
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH; // teljes helykitöltés, ...
        c.weightx = 1; // ... hogy az ikon balra rendeződjön
        
        pContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // alsó és felső margó 5 pixel
        add(pContainer, c);
        
        c.gridy = 1; // következő sorba mennek a gombok
        JPanel pButtons = new OkCancelPanel(btAgain, btSettings, btExit, 5);
        pButtons.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // felső margó kivételével mind 5 pixel
        add(pButtons, c);
        
        pack(); // ablak méretének minimalizálása
        setLocationRelativeTo(this); // középre igazítás
        btAgain.requestFocus(); // alapértelmezett opció az Újra gomb
    }
    
    /**
     * Beállítja a látható panelt.
     */
    public void setPanel(IconTextPanel p) {
        pContainer.removeAll(); // eltávolítja a panel komponenseit
        pContainer.add(p); // hozzáadja panelhez a kért komponenst
        repaint(); // újrarajzolja az ablakot
    }
    
}
