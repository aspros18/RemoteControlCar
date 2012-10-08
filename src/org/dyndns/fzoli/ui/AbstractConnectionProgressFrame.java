package org.dyndns.fzoli.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Kapcsolódásjelző- és kezelő ablak.
 * Csak akkor jelenik meg, ha valamiért nem sikerült első alkalommal kapcsolódni a szerverhez.
 * A felhasználó lehetőségei:
 * - beállítja a konfigurációt: az ablak nem látható, míg be nem zárja a beállításokat
 * - újra próbálkozik kapcsolódni: indikátor jelzi a folyamatot és amíg tart, nem lehet újra próbálkozni
 * - kilép a programból: végetér a program futása
 * @author zoli
 */
public abstract class AbstractConnectionProgressFrame extends JFrame {
    
    /**
     * Az ablakon megjelenő panelek.
     */
    private static class ConnProgPanel extends IconTextPanel {

        public ConnProgPanel(Component owner, Icon icon, String text) {
            super(owner, icon, text);
            setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0)); // alsó és felső margó 5 pixel
        }
        
    }
    
    /**
     * Folyamatjelző panel.
     */
    private final IconTextPanel pProgress;
    
    /**
     * Hibát kijelző panel.
     */
    private final IconTextPanel pError;
    
    /**
     * Újra gomb.
     * Meghívja az {@code onAgain} metódust, ha kiválasztják.
     */
    private final JButton btAgain = new JButton("Újra") {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    onAgain();
                }
                
            });
        }
    };
    
    /**
     * Kapcsolatbeállítás gomb.
     * Meghívja az {@code onSettings} metódust, ha kiválasztják.
     */
    private final JButton btSettings = new JButton("Kapcsolatbeállítás") {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    onSettings();
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
     * Konstruktor.
     * Alapértelmezetten hibaüzenetet mutat a panel.
     */
    public AbstractConnectionProgressFrame(Icon progressIcon) {
        super("Kapcsolódáskezelő");
        
        // panelek példányosítása
        pProgress = new ConnProgPanel(this, progressIcon, "Kapcsolódás folyamatban...");
        pError = new ConnProgPanel(this, LookAndFeelIcon.createIcon(this, "OptionPane.errorIcon", null), "Nem sikerült kapcsolódni a szerverhez!");
        
        setLayout(new GridBagLayout()); // kedvenc elrendezésmenedzserem alkalmazása
        setDefaultCloseOperation(EXIT_ON_CLOSE); // X-re kattintva vége a programnak
        setResizable(false); // átméretezés tiltása
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH; // teljes helykitöltés, ...
        c.weightx = 1; // ... hogy az ikon balra rendeződjön
        
        add(pError, c); 
        add(pProgress, c); // mindkét panelt felfűzöm az ablakra ...
        setProgress(false); // ... de egyszerre csak az egyik látható
        
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
     * @param on true esetén az indikátor jelenik meg, egyébként a hibaüzenet.
     */
    public void setProgress(boolean on) {
        if (on) {
            pError.setVisible(false);
            pProgress.setVisible(true);
        }
        else {
            pError.setVisible(true);
            pProgress.setVisible(false);
        }
    }
    
    /**
     * Akkor hívódik meg, amikor az Újra gombot kiválasztják.
     */
    protected abstract void onAgain();
    
    /**
     * Akkor hívódik meg, amikor az Beállítások gombot kiválasztják.
     */
    protected abstract void onSettings();
    
}
