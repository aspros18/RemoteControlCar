package org.dyndns.fzoli.rccar.controller;

import java.awt.Dialog;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

        public ControllerFrame() {
            super("Főablak");
            setIconImage(R.getIconImage());
            JLabel lbImage = new JLabel("test");
            add(lbImage);
            pack();
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
    private final JFrame MAIN_FRAME = new ControllerFrame();
    
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
    
}
