package org.dyndns.fzoli.rccar.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A vezérlő konfigurációját beállító dialógusablak.
 * TODO
 * @author zoli
 */
public class ConfigEditorDialog extends JDialog {

    /**
     * A konfiguráció, amit használ az ablak.
     */
    private final Config CONFIG;
    
    /**
     * Az ablak bezárásakor lefutó eseménykezelő.
     * Meghívja az {@code onClosing} metódust.
     */
    private WindowAdapter closeListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            onClosing();
        }
        
    };
    
    /**
     * Konstruktor.
     * @param config konfiguráció, amit használ az ablak.
     */
    public ConfigEditorDialog(Config config) {
        CONFIG = config;
        addWindowListener(closeListener);
        setTitle("Kapcsolat beállítása");
        setIconImage(R.getIconImage());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(200, 100);
        setLocationRelativeTo(this);
    }

    /**
     * Beállítja az ablak modalitását.
     */
    @Override
    public void setModal(boolean modal) {
        setModalityType(modal ? ModalityType.APPLICATION_MODAL : ModalityType.MODELESS);
    }

    /**
     * Megjeleníti vagy elrejti az ablakot.
     * Ha megjelenést kértek, előtérbe kerül az ablak.
     */
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            toFront();
            repaint();
        }
    }
    
    /**
     * Az ablak bezárásakor ha módosult a konfiguráció és nincs mentve,
     * megkérdi, akarja-e menteni.
     * TODO
     */
    private void onClosing() {
        ;
    }
    
}
