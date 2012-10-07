package org.dyndns.fzoli.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

/**
 * Fájl megjelenítő és kiválasztó panel.
 * @author zoli
 */
public class FilePanel extends JPanel {

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

    /**
     * Fájl tallózás eseményfigyelő.
     * Ha a tallózásra kattintottak, fájlkereső ablak jelenik meg.
     * A fájl kiválasztása után, beállítja a kiválasztott fájlt.
     * Ha van már fájl beállítva, a keresőablak kijelöli a fájlt megjelenésekor.
     * Ha nincs fájl beállítva még, de már más panelen állítottak be, akkor
     * az utolsó kiválasztott fájl könyvtárából indul a keresés.
     */
    private final ActionListener alSearch = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setAcceptAllFileFilterUsed(fileFilter == null);
            fc.setMultiSelectionEnabled(false);
            fc.setFileHidingEnabled(true);
            if (file == null) {
                if (lastFile != null) {
                    fc.setCurrentDirectory(lastFile);
                }
            }
            else {
                fc.setSelectedFile(file);
                fc.setCurrentDirectory(file);
            }
            if (fileFilter != null) {
                fc.setFileFilter(fileFilter);
            }
            if (JFileChooser.APPROVE_OPTION == fc.showOpenDialog(PARENT)) {
                setFile(fc.getSelectedFile());
            }
        }

    };
    
    /**
     * Szülő komponens.
     */
    private final Component PARENT;

    /**
     * Tallózás gomb.
     * Megjeleníti a fájlkeresőt.
     */
    private final JButton btSearch = new JButton("Tallózás");
    
    /**
     * A kiválasztott fájl útvonalát jeleníti meg.
     */
    private final JTextField tfFile = new JTextField(10);

    /**
     * A panelen megjelenített fájl.
     */
    private File file;
    
    /**
     * Az utóljára kiválasztott fájl bármelyik panelen.
     */
    private static File lastFile;
    
    /**
     * Fájl szűrő.
     * A megjelenő fájlkereső ablakok szűrője.
     */
    private FileFilter fileFilter;
    
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
     * A megjelenített/beállított fájlt adja vissza.
     */
    public File getFile() {
        return file;
    }

    /**
     * Beállítja a fájlt és megjeleníti.
     * A fájl csak akkor változik, ha a megadott fájl létezik és nem könyvtár,
     * valamint ha van fájlszűrő, megfelel a szűrőfeltételnek.
     */
    public void setFile(File file) {
        if (file != null && !file.isFile()) return;
        lastFile = file;
        if (fileFilter != null && !fileFilter.accept(file)) return;
        this.file = file;
        tfFile.setText(file == null ? "" : file.getName());
    }

    /**
     * A tallózáskor megjelenő fájlkereső ablak szűrőjét állítja be.
     */
    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

}