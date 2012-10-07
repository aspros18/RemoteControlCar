package org.dyndns.fzoli.ui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Súgó, Mégse, Ok gombokat tartalmazó panel.
 * @author zoli
 */
public class OkCancelPanel extends JPanel {

    public OkCancelPanel(JButton btOk, JButton btCancel, JButton btHelp, int gap) {
        super(new GridBagLayout());
        
        GridBagConstraints pc = new GridBagConstraints();
        pc.fill = GridBagConstraints.NONE; // a gombok mérete ne változzon
        pc.gridwidth = 1; // a teljes szélesség kitöltése
        
        pc.weightx = Integer.MAX_VALUE; // a Súgó gomb tölti ki a nagy részt ...
        pc.anchor = GridBagConstraints.LINE_START; // ... és bal szélre kerül
        add(btHelp, pc);
        
        pc.weightx = 1; // a másik két gomb minimális helyet foglal el ...
        pc.anchor = GridBagConstraints.LINE_END; // ... a jobb szélen
        
        pc.insets = new Insets(0, 0, 0, gap); // jobb oldali margó beállítása
        add(btCancel, pc);
        
        pc.insets = new Insets(0, 0, 0, 0); // margó vissza eredeti állapotba
        add(btOk, pc);
        
        resizeButtons(btOk, btCancel, btHelp); // gombok átméretezése
        
    }
    
    /**
     * Átméretezi a gombokat.
     * Az összes gombnak azonos szélességet állít be.
     */
    private static void resizeButtons(JButton btOk, JButton btCancel, JButton btHelp) {
        JButton[] buttons = createButtonArray(btOk, btCancel, btHelp);
        
        // közönséges maximum kiválasztás
        double size = buttons[0].getPreferredSize().getWidth();
        for (int i = 1; i < buttons.length; i++) {
            double d = buttons[i].getPreferredSize().getWidth();
            if (d > size) size = d;
        }
        
        // kis méretnövelés
        size += 5;
        
        // új méret beállítása a gombokra
        for (JButton bt : buttons) {
            bt.setPreferredSize(new Dimension((int) size, bt.getPreferredSize().height));
        }
    }
    
    /**
     * Létrehoz egy tömböt a paraméterben átadott gombokkal, hogy iterálni lehessen őket.
     */
    private static JButton[] createButtonArray(JButton btOk, JButton btCancel, JButton btHelp) {
        final JButton[] buttons = new JButton[3];
        buttons[0] = btOk;
        buttons[1] = btCancel;
        buttons[2] = btHelp;
        return buttons;
    }
    
}
