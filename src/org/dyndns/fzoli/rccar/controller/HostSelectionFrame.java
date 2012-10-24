package org.dyndns.fzoli.rccar.controller;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.model.controller.HostList;

/**
 * TODO - Járműválasztó ablak.
 * @author zoli
 */
public class HostSelectionFrame extends JFrame {

    /**
     * A felületi komponens.
     */
    private final JList<String> LIST = new JList<String>(new DefaultComboBoxModel<String>());
    
    /**
     * Scroll.
     */
    private final JScrollPane PANE = new JScrollPane(LIST);
    
    /**
     * A felületi komponens modelje.
     */
    private final HostList LIST_MODEL = new HostList();
    
    /**
     * Jármű kiválasztó gomb.
     */
    private final JButton BT_SELECT = new JButton("Kiválasztás");
    
    /**
     * Konstruktor
     * @param alExit az ablak bezárásakor lefutó eseményfigyelő
     */
    public HostSelectionFrame(ActionListener alExit) {
        initFrame();
        initComponents();
        setExitListener(alExit);
    }
    
    /**
     * Beállítja a komponenseket még a megjelenés előtt.
     */
    private void initFrame() {
        setTitle("Járműválasztó");
        setIconImage(R.getIconImage());
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 0, 0);
        add(new JLabel("Válasszon járművet a listából.", SwingConstants.CENTER), c);
        
        c.gridy = 1;
        c.weighty = Integer.MAX_VALUE;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        add(PANE, c);
        
        c.gridy = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(0, 0, 5, 0);
        add(BT_SELECT, c);
        
        setMinimumSize(new Dimension(300, 200));
        pack();
        setLocationRelativeTo(this);
    }
    
    /**
     * Komponensek alapértelmezett beállításainak felüldefiniálása és eseményfigyelők hozzáadása.
     */
    private void initComponents() {
        LIST.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        LIST.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                String host = LIST.getSelectedValue();
                BT_SELECT.setEnabled(host != null);
            }
            
        });
        BT_SELECT.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ; //TODO
            }
            
        });
    }
    
    /**
     * Frissíti a felületi komponenst a modelje alapján.
     * Eltávolítja az adatokat, majd újra feltölti a friss adatokat és ha volt, beállítja az előtte kiválasztott elemet és a scrollt.
     */
    private void refresh() {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) LIST.getModel();
        Point scroll = PANE.getViewport().getViewPosition();
        String selected = LIST.getSelectedValue();
        model.removeAllElements();
        for (String host: LIST_MODEL.getHosts()) {
            model.addElement(host);
        }
        if (model.getIndexOf(selected) == -1 && model.getSize() != 0) {
            selected = model.getElementAt(0);
            scroll = new Point(0, 0);
        }
        LIST.setSelectedValue(selected, false);
        PANE.getViewport().setViewPosition(scroll);
    }
    
    /**
     * Lista alapján beállítja a felületi komponenst.
     */
    public void setHosts(HostList list) {
        LIST_MODEL.update(list);
        refresh();
    }
    
    /**
     * A lista módosulása alapján frissíti a felületet.
     */
    public void setHost(HostList.PartialHostList change) {
        if (change != null) change.apply(LIST_MODEL);
        refresh();
    }
    
    /**
     * Beállítja a paraméterben megadott eseményfigyelőt, hogy az ablak bezárásakor fusson le.
     */
    private void setExitListener(final ActionListener alExit) {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                alExit.actionPerformed(new ActionEvent(e, 0, ""));
            }
            
        });
    }
    
}
