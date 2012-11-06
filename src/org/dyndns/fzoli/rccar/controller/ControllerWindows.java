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
        
        public ControllerFrame() {
            super("Főablak");
            setIconImage(R.getIconImage());
            setLayout(new BorderLayout());
            lbImage = new JLabel("");
            lbImage.setHorizontalAlignment(SwingConstants.CENTER);
            lbImage.setPreferredSize(new Dimension(640, 480));
            add(lbImage, BorderLayout.CENTER);
            JToolBar tbButtons = new JToolBar("Opciók");
            add(tbButtons, BorderLayout.SOUTH);
            tbButtons.setFloatable(false);
            createButton(tbButtons, "Teszt", R.getIconImage());
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
         */
        public void refresh() {
            lbImage.setText(getData().getHostName());
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
