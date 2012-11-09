package org.dyndns.fzoli.rccar.controller;

import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;

/**
 * A dialógusablakok közös őse.
 * @author zoli
 */
public abstract class AbstractDialog extends JDialog {

    /**
     * Konstruktor.
     * Beállítja az ablakbezárás-eseménykezelőt.
     * @param owner a főablak
     * @param title a címsor felirata
     * @param windows az ablakok konténere
     */
    public AbstractDialog(Window owner, String title, final ControllerWindows windows) {
        super(owner, title);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (windows != null) windows.setVisible(getWindowType(), false);
            }
            
        });
    }
    
    /**
     * Az ablak típusát adja vissza.
     * Az ablak azonosításához szükséges.
     */
    public abstract WindowType getWindowType();
    
}
