package org.dyndns.fzoli.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
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
     * Az ablakon megjeleníthető paneleket tartalmazza.
     */
    private final IconTextPanel[] PANELS;
    
    /**
     * Konstruktor.
     * Alapértelmezetten hibaüzenetet mutat a panel.
     */
    public AbstractConnectionProgressFrame(IconTextPanel[] panels) {
        super("Kapcsolódáskezelő");
        PANELS = panels;
        
        setLayout(new GridBagLayout()); // kedvenc elrendezésmenedzserem alkalmazása
        setDefaultCloseOperation(EXIT_ON_CLOSE); // X-re kattintva vége a programnak
        setResizable(false); // átméretezés tiltása
        
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH; // teljes helykitöltés, ...
        c.weightx = 1; // ... hogy az ikon balra rendeződjön
        
        for (IconTextPanel panel : panels) {
            add(panel, c); // az összes panelt felfűzöm az ablakra
            panel.setParent(this); // beállítom az ablak referenciáját szülőnek
        }
        IconTextPanel.resizeComponents(this); // átméretezem a komponenseket
        
        setIconTextPanel(0); // az első panel lesz látható csak
        
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
     * @param index a konstruktorban átadott paneleket tartalmazó tömb indexe
     */
    public void setIconTextPanel(int index) {
        for (int i = 0; i < PANELS.length; i++) {
            PANELS[i].setVisible(i == index);
        }
    }
    
    /**
     * Beállítja az Újra gombot.
     * @param enabled true esetén engedélyezett és fókuszált, false esetén nem engedélyezett
     */
    protected void setAgainButtonEnabled(boolean enabled) {
        btAgain.setEnabled(enabled);
        if (enabled ^ !btAgain.isEnabled()) btAgain.requestFocus(); // fókusz vissza, ha tiltva volt
    }
    
    /**
     * Beállítja az Beállítások gombot.
     * @param enabled true esetén engedélyezett, false esetén nem engedélyezett
     */
    protected void setSettingsButtonEnabled(boolean enabled) {
        btSettings.setEnabled(enabled);
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
