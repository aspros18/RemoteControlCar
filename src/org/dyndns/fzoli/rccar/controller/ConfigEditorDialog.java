package org.dyndns.fzoli.rccar.controller;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A vezérlő konfigurációját beállító dialógusablak.
 * TODO
 * @author zoli
 */
public class ConfigEditorDialog extends JDialog {

    /**
     * A dialógusablak lapfüleinek tartalma ebbe a panelbe kerül bele.
     * Mindegyik panel {@code GridBagLayout} elrendezésmenedzsert használ és
     * mindegyik panel átlátszó.
     * @see GridBagLayout
     */
    private static class ConfigPanel extends JPanel {
        
        public ConfigPanel() {
            super(new GridBagLayout());
            setOpaque(false);
        }
        
    }
    
    /**
     * A konfiguráció, amit használ az ablak.
     */
    private final Config CONFIG;
    
    private final JTextField tfAddress = new JTextField(),
                             tfPort = new JTextField();
    
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
     * Ezen a panelen állítható be a híd szerver elérési útvonala.
     */
    private final JPanel addressPanel = new ConfigPanel() {
        {
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5);
            c.fill = GridBagConstraints.BOTH;
            
            c.gridx = 1; // első oszlop
            
            c.gridy = 1; // első sor (1, 1)
            add(new JLabel("Szerver cím:"), c);
            
            c.gridy = 2; // második sor (1, 2)
            add(new JLabel("Szerver port:"), c);
            
            c.weightx = 1;
            c.gridx = 2; // második oszlop
            
            c.gridy = 1; // első sor (2, 1)
            add(tfAddress, c);
            
            c.gridy = 2; // második sor (2, 2)
            add(tfPort, c);
        }
    };
    
    /**
     * Ezen a panelen állítható be a kapcsolathoz használt tanúsítvány.
     */
    private final JPanel certificatePanel = new ConfigPanel() {
        {
            add(new JLabel("teszt2"));
        }
    };
    
    /**
     * Az ablakot teljes egészében kitöltő lapfüles panel.
     */
    private final JTabbedPane tabbedPane = new JTabbedPane() {
        {
            addTab("Útvonal", addressPanel);
            addTab("Tanúsítvány", certificatePanel);
        }
    };
    
    /**
     * Konstruktor.
     * @param config konfiguráció, amit használ az ablak.
     */
    public ConfigEditorDialog(Config config) {
        CONFIG = config;
        loadConfig();
        initDialog();
    }
    
    private void initDialog() {
        addWindowListener(closeListener);
        setTitle("Kapcsolatbeállító");
        setIconImage(R.getIconImage());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(tabbedPane);
        setSize(300, 210);
        setLocationRelativeTo(this);
    }
    
    private void loadConfig() {
        tfAddress.setText(CONFIG.getAddress());
        tfPort.setText(Integer.toString(CONFIG.getPort()));
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
        System.exit(0);
    }
    
}
