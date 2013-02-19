package org.dyndns.fzoli.rccar.controller.view.config;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.ParseException;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.dyndns.fzoli.rccar.controller.Config;
import org.dyndns.fzoli.rccar.controller.Main;
import static org.dyndns.fzoli.rccar.controller.Main.getString;
import static org.dyndns.fzoli.rccar.controller.Main.runClient;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.view.RelocalizableWindow;
import org.dyndns.fzoli.ui.FilePanel;
import org.dyndns.fzoli.ui.FrontFrame;
import org.dyndns.fzoli.ui.OkCancelPanel;
import org.dyndns.fzoli.ui.OptionPane;
import org.dyndns.fzoli.ui.RegexPatternFormatter;

/**
 * A vezérlő konfigurációját beállító dialógusablak.
 * @author zoli
 */
public class ConfigEditorFrame extends FrontFrame implements RelocalizableWindow, OkCancelPanel.OkCancelWindow {
    
    /**
     * A dialógusablak lapfüleinek tartalma ebbe a panelbe kerül bele.
     * Mindegyik panel átlátszó.
     */
    private static class ConfigPanel extends JPanel {
        
        public ConfigPanel() {
            setOpaque(false);
        }
        
        /**
         * Címkét gyárt a panelhez.
         * @param text a címke szövege
         */
        protected JLabel createLabel(final String text) {
            final JLabel lbSizer = new JLabel("<html><body style=\"width: 180px\">" + text + "</body></html>");
            return new JLabel("<html>" + text + "</html>") {

                @Override
                public Dimension getPreferredSize() {
                    Dimension d1 = super.getPreferredSize();
                    Dimension d2 = lbSizer.getPreferredSize();
                    return new Dimension(Math.min(d1.width, d2.width), d1.height);
                }
                
            };
        }
        
    }
    
    /**
     * Az ablakhoz tartozó fájl tallózó panelek osztálya.
     */
    private static class ConfigFilePanel extends FilePanel {

        /**
         * Az a könyvtár, ahonnan a programot indították.
         */
        private final String currentDir = new File(System.getProperty("user.dir")).getAbsolutePath();
        
        /**
         * Konstruktor.
         * Megjeleníti a fejlécet, a fájlútvonal-mutatót és a tallózó gombot.
         * @param text a fájlválasztó fejléce
         */
        public ConfigFilePanel(Component parent, String text) {
            super(parent, text);
        }

        /**
         * A megjelenített/beállított fájlt adja vissza.
         * Ha a fájlra lehet relatív útvonallal is hivatkozni, akkor a relatív útvonallal tér vissza.
         */
        @Override
        public File getFile() {
            File file = super.getFile(); // az eredeti fájl
            try {
                final String absFileName = file.getAbsolutePath(); // az eredeti fájl teljes útvonala
                if (absFileName.startsWith(currentDir)) { // ha a fájl a "cd" könyvtáron belül van
                    final int rootLength = currentDir.length(); // a "cd" könyvtár hossza
                    final String relFileName = absFileName.substring(rootLength + 1); // a relatív útvonal
                    file = new File(relFileName); // relatív útvonallal gyártott fájl
                }
            }
            catch (Exception ex) { // ha a fájl null vagy fájlrendszer hiba
                ;
            }
            return file;
        }
    }
    
    /**
     * Súgó ablak.
     */
    private final JDialog dialogHelp = new ConfigHelpDialog(this);
    
    /**
     * Crt fájlszűrő.
     */
    private static final FileNameExtensionFilter fnefCrt = new FileNameExtensionFilter("Tanúsítvány (*.crt)", new String[] {"crt"});
    
    /**
     * Key fájlszűrő.
     */
    private static final FileNameExtensionFilter fnefKey = new FileNameExtensionFilter("Tanúsítvány kulcs (*.key)", new String[] {"key"});
    
    /**
     * IP címre és hosztnévre és egyéb egyedi címekre is egész jól használható reguláris kifejezés.
     */
    private static final Pattern ptAddress = Pattern.compile("^[a-z\\d]{1}[\\w\\.\\d]{0,18}[a-z\\d]{1}$", Pattern.CASE_INSENSITIVE);
    
    /**
     * Port validálására használt reguláris kifejezés.
     * Minimum 1 és maximum 5 karakter, csak szám.
     */
    private static final Pattern ptPort = Pattern.compile("^[\\d]{1,5}$", Pattern.CASE_INSENSITIVE);
    
    /**
     * A konfiguráció, amit használ az ablak.
     */
    private final Config CONFIG;
    
    /**
     * Jelszótörlő gomb.
     */
    private final JButton btPasswordReset = new JButton(getString("delete")) {
        
        {
            // kattintáskor a jelszó törlése a konfigurációból és a gomb letiltása
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setEnabled(false);
                    CONFIG.setPassword(null, true);
                    Config.save(CONFIG);
                }
                
            });
        }

        @Override
        public Dimension getPreferredSize() {
            // a szükséges helynél 30 pixellel szélesebb méret
            Dimension d = super.getPreferredSize();
            return new Dimension(d.width + 30, d.height);
        }
        
    };
    
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
    private final FilePanel fpCa = new ConfigFilePanel(this, "Kiállító") {
        {
            setFileFilter(fnefCrt);
        }
    };
    
    /**
     * A tanúsítvány fájl tallózó panele.
     */
    private final FilePanel fpCert = new ConfigFilePanel(this, "Tanúsítvány") {
        {
            setFileFilter(fnefCrt);
        }
    };
    
    /**
     * A tanúsítvány kulcs-fájl tallózó panele.
     */
    private final FilePanel fpKey = new ConfigFilePanel(this, "Kulcs") {
        {
            setFileFilter(fnefKey);
        }
    };
    
    /**
     * Az ablak bezárásakor lefutó eseménykezelő.
     * Meghívja az {@code onClosing} metódust.
     */
    private final WindowAdapter closeListener = new WindowAdapter() {

        @Override
        public void windowClosing(WindowEvent e) {
            onClosing();
        }
        
    };
    
    /**
     * Erre a gombra kattintva a konfiguráció elmentődik és bezárul az ablak.
     * De csak akkor, ha érvényesek a beállítások.
     */
    private final JButton btOk = new JButton(getString("ok")) {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkConfig()) saveConfig();
                }
                
            });
        }
    };
    
    /**
     * Erre a gombra kattintva bezárul az ablak, a konfiguráció nem változik.
     */
    private final JButton btCancel = new JButton(getString("cancel")) {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    unsaveConfig();
                }
                
            });
        }
    };
    
    /**
     * Erre a gombra kattintva előjön a súgó.
     */
    private final JButton btHelp = new JButton(getString("help")) {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    dialogHelp.setVisible(true);
                }
                
            });
        }
    };
    
    /**
     * Ezen a panelen állítható be a híd szerver elérési útvonala.
     */
    private final JPanel addressPanel = new ConfigPanel() {
        {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.weighty = 1; // teljes helylefoglalás hosszúságban
            c.insets = new Insets(5, 5, 5, 5); // 5 pixeles margó
            c.fill = GridBagConstraints.HORIZONTAL; // teljes helykitöltés horizontálisan (sorkitöltés)
            
            c.gridx = 1; // első oszlop
            c.weightx = 0; // csak annyit foglal, amennyit kell
            
            c.gridy = 0; // nulladik sor
            c.gridwidth = 2; // két oszlopot foglal el a magyarázat
            add(createLabel("Ezen a lapfülön állíthatja be a híd szervernek az elérési útvonalát."), c);
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
     * Erről a panelről érhető el a jelszó törlésére használható gomb.
     */
    private final JPanel passwordResetPanel = new ConfigPanel() {
        {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 1; // teljes helyfoglalás szélességében
            c.weighty = 1; // teljes helylefoglalás hosszúságban
            
            c.insets = new Insets(5, 5, 5, 5); // 5 pixeles margó
            c.fill = GridBagConstraints.HORIZONTAL; // teljes helykitöltés horizontálisan (sorkitöltés)
            add(createLabel("Ezen a lapfülön törölheti a tanúsítvány jelszavát, ha azt régebben megadta."), c); // üzenet hozzáadása a panelhez
            
            c.gridy = 1; // a szöveg alá kerül a törlés gomb
            c.insets = new Insets(0, 5, 5, 5); // 5 pixeles margó mindenhol, kivéve felül
            c.fill = GridBagConstraints.NONE; // csak akkora helyet foglal, amennyire szüksége van
            add(btPasswordReset, c);
        }
    };
    
    /**
     * Erről a panelről érhető el a nyelv módosítására használható legördülő lista.
     */
    private final JPanel languageChooserPanel = new ConfigPanel() {
        {
            setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 1; // teljes helyfoglalás szélességében
            c.weighty = 1; // teljes helylefoglalás hosszúságban
            
            c.insets = new Insets(5, 5, 5, 5); // 5 pixeles margó
            c.fill = GridBagConstraints.HORIZONTAL; // teljes helykitöltés horizontálisan (sorkitöltés)
            add(createLabel("Ezen a lapfülön kiválaszthatja a használni kívánt nyelvet."), c); // üzenet hozzáadása a panelhez
            
            c.gridy = 1; // a szöveg alá kerül a törlés gomb
            c.insets = new Insets(0, 5, 5, 5); // 5 pixeles margó mindenhol, kivéve felül
            c.fill = GridBagConstraints.NONE; // csak akkora helyet foglal, amennyire szüksége van
            add(new JComboBox(Main.LNG_FRAME.MODEL_LOCALES) { // ugyan azzal a modellel jön létre a legördülő lista, amivel a nyelvkiválasztó ablak, így egyszerre módosulnak
                {
                    setRenderer(Main.LNG_FRAME.LCR_LOCALES); // ugyan azt a renderert használja, mint a nyelvkiválasztó ablak
                    setKeySelectionManager(Main.LNG_FRAME.createKeySelectionManager(this)); // gyorskereső létrehozása
                    setPreferredSize(new Dimension(Math.max(getPreferredSize().width, 180), getPreferredSize().height)); // 180 pixel széles (vagy nagyobb, ha kell)
                }
            }, c);
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
            addTab("Biztonság", certificatePanel);
            addTab("Jelszó", passwordResetPanel);
            addTab("Nyelv", languageChooserPanel);
        }
    };
    
    /**
     * Helyes konfiguráció beállítás kényszerítése.
     * Kezdetben inaktív.
     */
    private boolean force = false;
    
    /**
     * Az ablak megjelenésekor aktuális konfiguráció.
     * Ahhoz kell, hogy meg lehessen tudni, módosult-e a konfiguráció az ablak megjelenése óta.
     */
    private Config previousConfig;
    
    /**
     * Konstruktor.
     * @param config konfiguráció, amit használ az ablak.
     * @param wl eseménykezelő, ami előhívja a figyelmeztetéseket az ablak bezárásakor
     */
    public ConfigEditorFrame(Config config, WindowListener wl) {
        CONFIG = config;
        addWindowListener(wl);
        initComponents();
        initFrame();
    }
    
    /**
     * Inicializálja az ablakot.
     */
    private void initFrame() {
        setTitle("Kapcsolatbeállító"); // címsor szöveg beállítása
        setIconImage(R.getIconImage()); // címsor ikon beállítása
        setLayout(new GridBagLayout()); // elrendezésmenedzser megadása
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.BOTH; // mindkét irányban helykitöltés
        c.weightx = 1; // helyfoglalás szélességében ...
        c.weighty = 1; // ... és hosszúságában is
        add(tabbedPane, c); // lapfül panel hozzáadása
        
        c.gridy = 1; // következő sor
        c.weighty = 0; // minimális helyfoglalás magasságban ...
        c.fill = GridBagConstraints.HORIZONTAL; // ... és teljes szélesség elfoglalása ...
        JPanel pButton = new OkCancelPanel(this, btOk, btCancel, btHelp, 5); // ... a gombokat tartalmazó panelnek
        pButton.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5)); // margó a gombokat tartalmazó panelre
        add(pButton, c); // gombok hozzáadása az ablakhoz
        
        pack(); // legkisebb méretre állítás ...
        setMinimumSize(getSize()); // ... és ennél a méretnél csak nagyobb lehet az ablak
        setMaximumSize(new Dimension(1024, getSize().height)); // Java 1.7.0_07 még mindig bugos, de egyszer csak menni fog
        setLocationRelativeTo(this); // képernyő közepére igazítás
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // az alapértelmezett bezárás tiltása
        addWindowListener(closeListener); // bezáráskor saját metódus hívódik meg
    }
    
    /**
     * Inicializálja a komponenseket.
     */
    private void initComponents() {
        tabbedPane.setFocusable(false); // zavaró kijelölés jelzés leszedése
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 5 x 5 pixeles margó
        
        final KeyAdapter klEnter = new KeyAdapter() { // szerkeszthető mezőben enter lenyomására ugyan az történik, mint ha az OK gombra kattintanának, ha jók a beállítások

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
                    if (isConfigValid()) saveConfig(); // ha a konfiguráció helyes, mentés és ablak bezárása
                }
            }

        };
        
        tfAddress.addKeyListener(klEnter);
        tfPort.addKeyListener(klEnter);
    }

    /**
     * A felület feliratait újra beállítja.
     * Ha a nyelvet megváltoztatja a felhasználó, ez a metódus hívódik meg.
     */
    @Override
    public void relocalize() {
        // TODO
        btOk.setText(getString("ok"));
        btCancel.setText(getString(force ? "exit" : "cancel"));
        btHelp.setText(getString("help"));
        btPasswordReset.setText(getString("delete"));
    }
    
    /**
     * Megadja, hogy a helyes konfiguráció beállítása kényszerítve van-e.
     */
    public boolean isForce() {
        return force;
    }

    /**
     * Beállítja, hogy a helyes konfiguráció beállítása kényszerítve legyen-e.
     * @param force true esetén kényszerített
     */
    public void setForce(boolean force) {
        if (force != force) {
            this.force = force;
            btCancel.setText(getString(force ? "exit" : "cancel"));
        }
    }
    
    /**
     * Betölti a konfigurációt a felület elemeibe.
     */
    private void loadConfig() {
        previousConfig = Config.getInstance();
        tfAddress.setText(CONFIG.getAddress());
        tfPort.setText(Integer.toString(CONFIG.getPort()));
        fpCa.setFile(CONFIG.getCAFile());
        fpCert.setFile(CONFIG.getCertFile());
        fpKey.setFile(CONFIG.getKeyFile());
        btPasswordReset.setEnabled(CONFIG.isPasswordStored());
    }
    
    /**
     * Elmenti a konfigurációt.
     * Ha sikerült a mentés, bezárja az ablakot, egyébként figyelmezteti a felhasználót.
     * Ha van már kialakított kapcsolat a Híddal és sikerült a mentés,
     * megkérdi, akar-e a felhasználó újrakapcsolódni a hídhoz az új beállításokkal.
     */
    private void saveConfig() {
        CONFIG.setAddress(tfAddress.getText());
        CONFIG.setCAFile(fpCa.getFile());
        CONFIG.setCertFile(fpCert.getFile());
        CONFIG.setKeyFile(fpKey.getFile());
        CONFIG.setPort(Integer.parseInt(tfPort.getText()));
        if (Config.save(CONFIG)) {
            if (!previousConfig.equals(CONFIG) && Main.isConnected()) {
                int answer = OptionPane.showYesNoDialog(ConfigEditorFrame.this, "Szeretne újrakapcsolódni a szerverhez az új beállításokkal?", "Újrakapcsolódás");
                if (answer == 0) Main.reconnect();
            }
            dispose();
        }
        else {
            OptionPane.showWarningDialog(R.getIconImage(), "Nem sikerült lemezre menteni a beállításokat!", "Figyelmeztetés");
        }
    }
    
    /**
     * Bezárja az ablakot a konfiguráció mentése nélkül.
     * Ha nem megfelelő az érvényben lévő konfiguráció, de meg van követelve a helyes konfiguráció, a program leáll.
     */
    private void unsaveConfig() {
        if (isForce() && !CONFIG.isFileExists()) System.exit(0);
        dispose();
    }
    
    /**
     * Megadja, érvényes-e az aktuális beállítás.
     * Érvényes, ha mindhárom fájl be van állítva és mindkét bemenet megfelel a reguláris kifejezésüknek.
     */
    private boolean isConfigValid() {
        return isConfigValid(tfAddress.getText(), tfPort.getText(), fpCa.getFile(), fpCert.getFile(), fpKey.getFile());
    }
    
    /**
     * Megadja, érvényes-e a paraméterben megadott beállítás.
     * Érvényes, ha mindhárom fájl be van állítva és mindkét bemenet megfelel a reguláris kifejezésüknek.
     */
    private static boolean isConfigValid(String address, String port, File ca, File cert, File key) {
        return ptAddress.matcher(address).matches() &&
               ptPort.matcher(port).matches() &&
               ca != null &&
               cert != null &&
               key != null;
    }
    
    /**
     * A cím maszkolására hoz létre egy formázó objektumot.
     */
    private AbstractFormatter createAddressFormatter() {
        RegexPatternFormatter fmAddress = new RegexPatternFormatter(ptAddress) {

            @Override
            public Object stringToValue(String string) throws ParseException {
                // ha a szöveg pontra végződik vagy rövidebb két karakternél, az eredeti szöveg kerül a helyére a szerkesztés befejezésekor
                if (string.length() < 2 || string.endsWith(".")) return CONFIG.getAddress();
                return ((String)super.stringToValue(string)).toLowerCase(); // a szerkesztés befejezésekor minden karaktert kicsire cserél
            }
            
        };
        fmAddress.setAllowsInvalid(false); // nem engedi meg a nem megfelelő értékek beírását
        return fmAddress;
    }
    
    /**
     * A port maszkolására hoz létre egy formázó objektumot.
     */
    private AbstractFormatter createPortFormatter() {
        RegexPatternFormatter fmPort = new RegexPatternFormatter(ptPort) {

            @Override
            public Object stringToValue(String string) throws ParseException {
                try {
                    // ha a szöveg rövidebb 1 karakternél, az eredeti szöveg kerül a helyére a szerkesztés befejezésekor
                    if (string.length() < 1) return CONFIG.getPort();
                    // ha a szöveg nem alakítható egész számmá vagy az intervallumon kívül esik, kivételt keletkezik...
                    int number = Integer.parseInt(string); // ... itt
                    if (number < 1 || number > 65535) throw new Exception(); // ... vagy itt
                }
                catch (Exception ex) {
                    // ParseException kivétel dobása, hogy nem megfelelő az érték
                    throw new ParseException("invalid port", 0);
                }
                // ha eddig nem dobódott kivétel, még a regex kifejezés dobhat kivételt és ha dob, nem frissül a szöveg
                return super.stringToValue(string);
            }
            
        };
        fmPort.setAllowsInvalid(false); // nem engedi meg a nem megfelelő értékek beírását
        return fmPort;
    }

    /**
     * Megjeleníti vagy elrejti az ablakot.
     * Ha megjelenést kértek, előtérbe kerül az ablak.
     * A konfiguráció frissül az ablak megjelenésekor.
     */
    @Override
    public void setVisible(boolean b) {
        if (b && !isVisible()) loadConfig();
        if (b) toFront();
        super.setVisible(b);
    }

    /**
     * Megadja, hogy a gombok szövegének módosulása után legyen-e ablak újraméretezés.
     * @return ha a régi szélesség nem elég az új szövegeknek, akkor true, egyébként false
     */
    @Override
    public boolean needRepack(Rectangle r) {
        return getPreferredSize().width > r.width;
    }
    
    /**
     * Megadja, hogy az átméretezés után a magasság legyen-e újra a régi.
     * @return ha a régi magasság nagyobb az új magasságnál, akkor true, egyébként false
     */
    @Override
    public boolean restoreHeight(Rectangle r) {
        return r.height > getPreferredSize().height;
    }
    
    /**
     * Megadja, hogy az átméretezés után legyen-e ablak újrapozícionálás.
     * @return ha 15 pixel pontossággal a képernyő közepén van a téglalap, akkor true, egyébként false
     */
    @Override
    public boolean needReloc(Rectangle r) {
        return OkCancelPanel.isNearCenter(r);
    }
    
    /**
     * Beállítja, melyik lapfül legyen előtérben.
     * @param tabIndex az előtérbe kerülő lapfül indexe
     */
    public void setTabIndex(Integer tabIndex) {
        if (tabIndex != null) tabbedPane.setSelectedIndex(tabIndex);
    }
    
    /**
     * Ha a beállítások nem érvényesek, figyelmezteti a felhasználót.
     * @return true, ha érvényesek a beállítások, egyébként false
     */
    private boolean checkConfig() {
        if (!isConfigValid()) {
            String[] opts = new String[] {"OK", "Kilépés"};
            int sel = JOptionPane.showOptionDialog(this, "A beállítások nem megfelelőek!", "Figyelmeztetés", JOptionPane.NO_OPTION, JOptionPane.WARNING_MESSAGE, null, opts, opts[0]);
            if (sel != 0) unsaveConfig();
            return false;
        }
        return true;
    }

    /**
     * Bezárja az ablakot és elindítja a programot.
     */
    @Override
    public void dispose() {
        super.dispose();
        runClient(true);
    }
    
    /**
     * Az ablak bezárásakor ha módosult a konfiguráció és nincs mentve,
     * megkérdi, akarja-e menteni, egyébként biztos, hogy nincs mentés.
     * Ha a konfiguráció nem érvényes, figyelmezteti a felhasználót és nem csinál semmit.
     */
    private void onClosing() {
        if (!checkConfig()) return; // ha a beállítás nem érvényes figyelmeztetés és semmittevés
        getContentPane().requestFocus(); // fókusz átadása az ablaknak, hogy biztosan minden szerkesztés végetérjen
        if (CONFIG.equals(tfAddress.getText(), Integer.parseInt(tfPort.getText()), fpCa.getFile(), fpCert.getFile(), fpKey.getFile())) {
            unsaveConfig(); // a beállítások nem változtak, nincs mentés
        }
        else {
            // a beállítások megváltoztak, legyen mentés?
            String[] opts = new String[] {"Igen", "Nem", "Mégse"}; // az alapértelmezett opció a Mégse
            int sel = JOptionPane.showOptionDialog(this, "Menti a módosításokat?", getTitle(), JOptionPane.NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, opts, opts[2]);
            switch (sel) {
                case 0: // Igen, legyen mentés
                    saveConfig();
                    break;
                case 1: // Nem, ne legyen mentés
                    unsaveConfig();
                    break;
                case 2: // Mégse, semmittevés
                    ;
            }
        }
    }
    
}