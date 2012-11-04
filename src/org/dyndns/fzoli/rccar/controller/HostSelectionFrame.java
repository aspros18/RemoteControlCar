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
import java.util.List;
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

/**
 * Járműválasztó ablak.
 * @author zoli
 */
public class HostSelectionFrame extends JFrame {

    /**
     * A felületen megjelenő lista.
     */
    private final JList<String> LIST = new JList<String>(new DefaultComboBoxModel<String>());
    
    /**
     * Scroll.
     */
    private final JScrollPane PANE = new JScrollPane(LIST);
    
    /**
     * Jármű kiválasztó gomb.
     */
    private final JButton BT_SELECT = new JButton("Kiválasztás");
    
    /**
     * Megadja, hogy ki lett-e már választva egy hoszt.
     */
    private boolean selected;
    
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
                BT_SELECT.setEnabled(host != null && !selected);
            }
            
        });
        BT_SELECT.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selected = true;
                BT_SELECT.setEnabled(false);
                ControllerModels.getData().setHostName(LIST.getSelectedValue());
            }
            
        });
    }

    /**
     * Ha az ablak megjelenését kérik, akkor még nincs kiválasztva a hoszt.
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            selected = false;
            BT_SELECT.setEnabled(true);
        }
        super.setVisible(b);
    }
    
    /**
     * Frissíti a felület listáját a paraméter alapján.
     * Eltávolítja az adatokat, majd újra feltölti a friss adatokat és ha volt, beállítja az előtte kiválasztott elemet és a scrollt.
     */
    public void refresh(List<String> list) {
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) LIST.getModel();
        Point scroll = PANE.getViewport().getViewPosition();
        String selected = LIST.getSelectedValue();
        model.removeAllElements();
        for (String host: list) {
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
