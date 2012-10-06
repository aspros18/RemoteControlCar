package org.dyndns.fzoli.rccar.controller;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.ui.FilePanel;
import org.dyndns.fzoli.ui.RegexPatternFormatter;

/**
 * A vezérlő konfigurációját beállító dialógusablak.
 * TODO
 * @author zoli
 */
public class ConfigEditorDialog extends JDialog {
    
    /**
     * A dialógusablak lapfüleinek tartalma ebbe a panelbe kerül bele.
     * Mindegyik panel átlátszó.
     * @see GridBagLayout
     */
    private static class ConfigPanel extends JPanel {
        
        public ConfigPanel() {
            setOpaque(false);
        }
        
    }
    
    /**
     * Crt fájlszűrő.
     */
    private static final FileNameExtensionFilter fnefCrt = new FileNameExtensionFilter("Tanúsítvány (*.crt)", new String[] {"crt"});
    
    /**
     * Key fájlszűrő.
     */
    private static final FileNameExtensionFilter fnefKey = new FileNameExtensionFilter("Tanúsítvány kulcs (*.key)", new String[] {"key"});
    
    /**
     * A konfiguráció, amit használ az ablak.
     */
    private final Config CONFIG;
    
    /**
     * A szerver címe írható át benne.
     */
    private final JTextField tfAddress = new JFormattedTextField(createAddressFormatter());
    
    /**
     * A szerver portja írható át benne.
     */
    private final JTextField tfPort = new JFormattedTextField(createPortFormatter());
    
    /**
     * A kiállító fájl tallózó panele.
     */
    private final FilePanel fpCa = new FilePanel(this, "Kiállító") {
        {
            setFileFilter(fnefCrt);
        }
    };
    
    /**
     * A tanúsítvány fájl tallózó panele.
     */
    private final FilePanel fpCert = new FilePanel(this, "Tanúsítvány") {
        {
            setFileFilter(fnefCrt);
        }
    };
    
    /**
     * A tanúsítvány kulcs-fájl tallózó panele.
     */
    private final FilePanel fpKey = new FilePanel(this, "Kulcs") {
        {
            setFileFilter(fnefKey);
        }
    };
    
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
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(5, 5, 5, 5); // 5 pixeles margó
            c.fill = GridBagConstraints.BOTH; // teljes helykitöltés
            
            c.gridx = 1; // első oszlop
            c.weightx = 0; // csak annyit foglal, amennyit kell
            
            c.gridy = 0; // nulladik sor
            c.gridwidth = 2; // két oszlopot foglal el a magyarázat
            JLabel lbMsg = new JLabel("<html>Ezen a lapfülen állíthatja be a híd szervernek az elérési útvonalát.</html>");
            lbMsg.setPreferredSize(new Dimension(240, 30));
            add(lbMsg, c);
            c.gridwidth = 1; // a többi elem egy oszlopot foglal el
            
            c.gridy = 1; // első sor (1, 1)
            add(new JLabel("Szerver cím:"), c);
            
            c.gridy = 2; // második sor (1, 2)
            add(new JLabel("Szerver port:"), c);
            
            c.gridx = 2; // második oszlop
            c.weightx = 1; // kitölti a maradék helyet
            
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
            setLayout(new GridLayout(3, 1));
            add(fpCa);
            add(fpCert);
            add(fpKey);
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
        initComponents();
        initDialog();
    }
    
    /**
     * Inicializálja az ablakot.
     */
    private void initDialog() {
        addWindowListener(closeListener);
        setTitle("Kapcsolatbeállító");
        setIconImage(R.getIconImage());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        add(tabbedPane);
        pack();
        setLocationRelativeTo(this);
    }
    
    /**
     * Inicializálja a komponenseket.
     */
    private void initComponents() {
        tabbedPane.setFocusable(false);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }
    
    /**
     * Betölti a konfigurációt a felület elemeibe.
     */
    private void loadConfig() {
        tfAddress.setText(CONFIG.getAddress());
        tfPort.setText(Integer.toString(CONFIG.getPort()));
        fpCa.setFile(CONFIG.getCAFile());
        fpCert.setFile(CONFIG.getCertFile());
        fpKey.setFile(CONFIG.getKeyFile());
    }
    
    /**
     * A cím maszkolására hoz létre egy formázó objektumot.
     */
    private AbstractFormatter createAddressFormatter() {
        Pattern ptAddress = Pattern.compile("^[a-z]{1}[\\w\\.]{0,18}[a-z]{1}$", Pattern.CASE_INSENSITIVE);
        RegexPatternFormatter fmAddress = new RegexPatternFormatter(ptAddress) {

            @Override
            public Object stringToValue(String string) throws ParseException {
                if (string.length() < 2) return CONFIG.getAddress();
                return ((String)super.stringToValue(string)).toLowerCase();
            }
            
        };
        fmAddress.setAllowsInvalid(false);
        return fmAddress;
    }
    
    /**
     * A port maszkolására hoz létre egy formázó objektumot.
     */
    private AbstractFormatter createPortFormatter() {
        Pattern ptPort = Pattern.compile("^[\\d]{0,5}$", Pattern.CASE_INSENSITIVE);
        RegexPatternFormatter fmPort = new RegexPatternFormatter(ptPort) {

            @Override
            public Object stringToValue(String string) throws ParseException {
                try {
                    if (string.isEmpty()) return CONFIG.getPort();
                    int number = Integer.parseInt(string);
                    if (number < 1 || number > 65536) throw new Exception();
                }
                catch (Exception ex) {
                    throw new ParseException("invalid port", 0);
                }
                return super.stringToValue(string);
            }
            
        };
        fmPort.setAllowsInvalid(false);
        return fmPort;
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
     * A konfiguráció frissül az ablak megjelenésekor.
     */
    @Override
    public void setVisible(boolean b) {
        if (b) loadConfig();
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
        if (!CONFIG.getCAFile().equals(fpCa.getFile()) ||
            !CONFIG.getCertFile().equals(fpCert.getFile()) ||
            !CONFIG.getKeyFile().equals(fpKey.getFile()) ||
            !CONFIG.getAddress().equals(tfAddress.getText()) ||
            CONFIG.getPort() != Integer.parseInt(tfPort.getText())) {
            int sel = JOptionPane.showOptionDialog(this, "Teszt", getTitle(), JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Igen", "Nem", "Mégse"}, "Mégse");
            switch (sel) {
                case 0:
                    System.exit(0);
                    break;
                case 1:
                    dispose();
                    break;
            }
        }
        else {
            dispose();
        }
    }
    
}