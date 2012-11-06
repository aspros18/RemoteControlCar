package org.dyndns.fzoli.rccar.controller;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import static org.dyndns.fzoli.rccar.controller.ControllerModels.getData;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A járművel kapcsolatos ablakok konténere.
 * @author zoli
 */
public class ControllerWindows {
    
    /**
     * A főablak.
     */
    private static class ControllerFrame extends JFrame {

        /**
         * A képkockát megjelenítő címke.
         */
        private JLabel lbImage;
        
        /**
         * Vezérlőgomb.
         */
        private JButton btControll;
        
        /**
         * Vezérlőgomb ikonja, amikor átadható a vezérlés.
         */
        private static final ImageIcon icController1 = new ImageIcon(R.getImage("controller1.png"));
        
        /**
         * Vezérlőgomb ikonja, amikor kérhető a vezérlés.
         */
        private static final ImageIcon icController2 = new ImageIcon(R.getImage("controller2.png"));
        
        public ControllerFrame() {
            super("Főablak");
            setIconImage(R.getIconImage());
            setLayout(new BorderLayout());
            
            lbImage = new JLabel("Teszt");
            lbImage.setHorizontalAlignment(SwingConstants.CENTER);
            lbImage.setPreferredSize(new Dimension(640, 480));
            add(lbImage, BorderLayout.CENTER);
            
            JToolBar tbButtons = new JToolBar("Opciók");
            add(tbButtons, BorderLayout.SOUTH);
            tbButtons.setFloatable(false);
            
            btControll = createButton(tbButtons, "", icController1.getImage());
            
            pack();
        }
        
        /**
         * Panelhez gyárt gombot.
         */
        private JButton createButton(JToolBar tb, String text, Image img) {
            JButton bt = new JButton(new ImageIcon(img));
            tb.add(bt);
            bt.setToolTipText(text);
            bt.setFocusable(false);
            return bt;
        }
        
        /**
         * Frissíti az ablak tartalmát a model alapján.
         * Az alábbi táblázat alapján XNOR művelet dönti el, hogy ktív-e a gomb
         * és az első opció dönti el az ikon típusát:
         *    vezérli? akarja?  esemény
         *    i        i        lemondás aktív
         *    h        i        kérés inaktív
         *    i        h        lemondás inaktív
         *    h        h        kérés aktív
         */
        public void refresh() {
            btControll.setIcon(getData().isControlling() ? icController1 : icController2);
            btControll.setToolTipText(getData().isControlling() ? "Vezérlés átadása" : "Vezérlés kérése");
            btControll.setEnabled(!(getData().isControlling() ^ getData().isWantControl()));
        }
        
    }
    
    /**
     * A dialógusablakok közös őse.
     */
    private static class ControllerDialog extends JDialog {

        public ControllerDialog(Dialog owner, String title) {
            super(owner, title);
            setIconImage(R.getIconImage());
        }
        
    }
    
    /**
     * Főablak.
     * Tartalmazza a kameraképet és a vezérlőgombokat.
     */
    private final ControllerFrame MAIN_FRAME = new ControllerFrame();
    
    /**
     * Az ablakok pozícionálása.
     */
    public ControllerWindows() {
        //TODO
    }
    
    /**
     * Az ablakok megjelenítése/eltüntetése.
     */
    public void setVisible(boolean b) {
        MAIN_FRAME.setVisible(b);
    }
    
    /**
     * Frissíti az ablakok tartalmát a modelük alapján.
     */
    public void refresh() {
        MAIN_FRAME.refresh();
    }
    
}
