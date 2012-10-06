package org.dyndns.fzoli.rccar.controller;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.ParseException;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.text.DefaultFormatter;
import org.dyndns.fzoli.rccar.controller.resource.R;

/**
 * A vezérlő konfigurációját beállító dialógusablak.
 * TODO
 * @author zoli
 */
public class ConfigEditorDialog extends JDialog {

    /**
     * Reguláris kifejezésre illeszkedő szövegformázó.
     */
    private static class RegexPatternFormatter extends DefaultFormatter {

        protected java.util.regex.Matcher matcher;

        public RegexPatternFormatter(Pattern regex) {
          setOverwriteMode(false);
          matcher = regex.matcher(""); // Matcher inicializálása a regexhez
        }

        @Override
        public Object stringToValue(String string) throws ParseException {
            if (string == null)
                return null;
            matcher.reset(string); // Matcher szövegének beállítása

            if (!matcher.matches()) // Ha nem illeszkedik a szöveg, kivételt dob
                throw new ParseException("does not match regex", 0);

            // Ha a szöveg illeszkedett, akkor vissza lehet térni vele
            return super.stringToValue(string);
        }

    }
    
    /**
     * Fájl megjelenítő és kiválasztó panel.
     */
    private static class FilePanel extends JPanel {

        /**
         * Beállítja a magyar elnevezéseket a fájl tallózóhoz.
         */
        static {
            UIManager.put("FileChooser.fileNameLabelText", "Fájlnév");
            UIManager.put("FileChooser.homeFolderToolTipText", "Kezdőkönyvtár");
            UIManager.put("FileChooser.newFolderToolTipText", "Új könyvtár");
            UIManager.put("FileChooser.listViewButtonToolTipTextlist", "Lista");
            UIManager.put("FileChooser.detailsViewButtonToolTipText", "Részletek");
            UIManager.put("FileChooser.saveButtonText", "Mentés");
            UIManager.put("FileChooser.openButtonText", "Megnyitás");
            UIManager.put("FileChooser.cancelButtonText", "Mégse");
            UIManager.put("FileChooser.updateButtonText", "Frissítés");
            UIManager.put("FileChooser.helpButtonText", "Súgó");
            UIManager.put("FileChooser.saveButtonToolTipText", "Mentés");
            UIManager.put("FileChooser.openButtonToolTipText", "Megnyitás");
            UIManager.put("FileChooser.cancelButtonToolTipText", "Mégse");
            UIManager.put("FileChooser.updateButtonToolTipText", "Frissítés");
            UIManager.put("FileChooser.helpButtonToolTipText", "Súgó");
            UIManager.put("FileChooser.filesOfTypeLabelText", "Fájltípus");
            UIManager.put("FileChooser.upFolderToolTipText", "Fel");
            UIManager.put("FileChooser.acceptAllFileFilterText", "Minden fájl");
            UIManager.put("FileChooser.lookInLabelText", "Hely");
            UIManager.put("FileChooser.listViewButtonAccessibleName", "Lista");
            UIManager.put("FileChooser.detailsViewButtonAccessibleName", "Részletek");
            UIManager.put("FileChooser.upFolderAccessibleName", "Fel");
            UIManager.put("FileChooser.homeFolderAccessibleName", "Kezdőkönyvtár");
            UIManager.put("FileChooser.fileNameHeaderText", "Név"); 
            UIManager.put("FileChooser.fileSizeHeaderText", "Méret"); 
            UIManager.put("FileChooser.fileTypeHeaderText", "Típus"); 
            UIManager.put("FileChooser.fileDateHeaderText", "Dátum"); 
            UIManager.put("FileChooser.fileAttrHeaderText", "Tulajdonságok"); 
            UIManager.put("FileChooser.openDialogTitleText","Megnyitás");
            UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        }
        
        private final Component PARENT;
        
        private File file;
        
        private final JButton btSearch = new JButton("Tallózás");
        private final JTextField tfFile = new JTextField(20);
        
        /**
         * Fájl tallózás eseményfigyelő.
         * Ha a tallózásra kattintottak, fájlkereső ablak jelenik meg.
         * A fájl kiválasztása után, beállítja a kiválasztott fájlt.
         */
        private final ActionListener alSearch = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                System.out.println(fc.showOpenDialog(PARENT));
            }
            
        };
        
        /**
         * Konstruktor.
         * Megjeleníti a fejlécet, a fájlútvonal-mutatót és a tallózó gombot.
         * @param text a fájlválasztó fejléce
         */
        public FilePanel(Component parent, String text) {
            super(new GridBagLayout());
            PARENT = parent;
            setOpaque(false);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(2, 2, 2, 2);
            c.gridwidth = 1;
            c.fill = GridBagConstraints.BOTH;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 2;
            add(new JLabel("<html>" + text + ":</html>"), c);
            c.weightx = 1;
            c.gridy = 1;
            add(tfFile, c);
            c.gridx = 1;
            add(btSearch, c);
            tfFile.setEditable(false);
            btSearch.addActionListener(alSearch);
        }
        
        /**
         * A beállított fájlt adja vissza.
         */
        public File getFile() {
            return file;
        }
        
        /**
         * Beállítja a fájlt.
         */
        public void setFile(File file) {
            this.file = file;
            tfFile.setText(file.toString());
        }
        
    }
    
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
    
    /**
     * A szerver címe írható át benne.
     */
    private final JTextField tfAddress = new JFormattedTextField(createAddressFormatter());
    
    /**
     * A szerver portja írható át benne.
     */
    private final JTextField tfPort = new JFormattedTextField(createPortFormatter());
    
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
            c.insets = new Insets(5, 5, 5, 5); // 5 pixeles margó
            c.fill = GridBagConstraints.BOTH; // teljes helykitöltés
            
            c.gridx = 1; // első oszlop
            c.weightx = 0; // csak annyit foglal, amennyit kell
            
            c.gridy = 0; // nulladik sor
            c.gridwidth = 2; // két oszlopot foglal el a magyarázat
            add(new JLabel("<html>Ezen a lapfülen állíthatja be a híd szervernek az elérési útvonalát.</html>"), c);
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
            setLayout(new GridLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.BOTH;
            add(new FilePanel(this, "Teszt"));
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
        setSize(300, 210);
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
     * Betölti a konfigurációt a felület elemeibe.
     */
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