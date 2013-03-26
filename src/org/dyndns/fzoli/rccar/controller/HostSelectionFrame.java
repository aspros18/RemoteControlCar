package org.dyndns.fzoli.rccar.controller;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import static org.dyndns.fzoli.rccar.controller.Main.getString;
import static org.dyndns.fzoli.rccar.controller.Main.showSettingFrame;
import org.dyndns.fzoli.rccar.controller.resource.R;
import org.dyndns.fzoli.rccar.controller.view.RelocalizableWindow;

/**
 * Járműválasztó ablak.
 * @author zoli
 */
public class HostSelectionFrame extends JFrame implements RelocalizableWindow {

    /**
     * A felületen megjelenő lista.
     */
    private final JList<String> LIST = new JList<String>(new DefaultListModel<String>() {

        /**
         * Ugyan az, mint az eredeti metódus, csak nem dob kivételt, ha hibás az index.
         * Azért van szükség a felüldefiniálásra, mert Linuxon a lista paint metódusa ritkán, de túlindexel valamiért.
         */
        @Override
        public String getElementAt(int index) {
            try {
                return super.getElementAt(index);
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                return null;
            }
        }
        
    });
    
    /**
     * Scroll.
     */
    private final JScrollPane PANE = new JScrollPane(LIST);
    
    /**
     * Jármű kiválasztó gomb.
     */
    private final JButton BT_SELECT = new JButton(getString("vehicle_select"));
    
    /**
     * A kapcsolatbeállító-ablakot megjelenítő gomb.
     */
    private final JButton BT_SETTINGS = new JButton(new ImageIcon(R.getImage("preferences.png"))) {
        {
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    showSettingFrame(false, null);
                }
                
            });
        }
    };
    
    /**
     * Jármű választásra kérő szöveg.
     */
    private final JLabel LB_MSG = new JLabel(getString("vehicle_select_msg"), SwingConstants.CENTER);
    
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
        setTitle(getString("vehicle_chooser"));
        setIconImage(R.getIconImage());
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 3;
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 0, 0, 0);
        
        add(LB_MSG, c);
        
        c.gridy = 1;
        c.weighty = Integer.MAX_VALUE;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        add(PANE, c);
        
        c.gridwidth = 1;
        c.gridy = 2;
        c.weighty = 1;
        c.fill = GridBagConstraints.NONE;
        
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(0, 5, 5, 0);
        BT_SETTINGS.setToolTipText(getString("connection_settings"));
        add(BT_SETTINGS, c);
        
        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        c.insets = new Insets(0, 0, 5, 0);
        add(BT_SELECT, c);
        
        c.gridx = 2;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0, 0, 5, 5);
        JLabel lbRight = new JLabel() {

            @Override
            public Dimension getPreferredSize() {
                return BT_SETTINGS.getPreferredSize();
            }
            
        };
        add(lbRight, c);
        
        setMinimumSize(new Dimension(300, 200));
        pack();
        setMinimumSize(getSize());
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
                selectVehicle();
            }
            
        });
        LIST.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectVehicle();
                }
            }

        });
    }

    /**
     * Járművet választ ki, ha van lehetőség jármű kiválasztására.
     */
    private void selectVehicle() {
        if (!BT_SELECT.isEnabled()) return;
        selected = true;
        BT_SELECT.setEnabled(false);
        ControllerModels.getData().getSender().setHostName(LIST.getSelectedValue());
    }
    
    /**
     * A felület feliratait újra beállítja.
     * Ha a nyelvet megváltoztatja a felhasználó, ez a metódus hívódik meg.
     */
    @Override
    public void relocalize() {
        setTitle(getString("vehicle_chooser"));
        BT_SELECT.setText(getString("vehicle_select"));
        LB_MSG.setText(getString("vehicle_select_msg"));
        BT_SETTINGS.setToolTipText(getString("connection_settings"));
    }
    
    /**
     * Ha az ablak megjelenését kérik, akkor még nincs kiválasztva a hoszt.
     */
    @Override
    public void setVisible(boolean b) {
        if (b) {
            selected = false;
            BT_SELECT.setEnabled(LIST.getModel().getSize() != 0);
        }
        super.setVisible(b);
    }
    
    /**
     * Frissíti a felület listáját a paraméter alapján.
     * Eltávolítja az adatokat, majd újra feltölti a friss adatokat és ha volt, beállítja az előtte kiválasztott elemet és a scrollt.
     */
    public void refresh(List<String> list) {
        if (list == null) return;
        Collections.sort(list);
        DefaultListModel<String> model = (DefaultListModel<String>) LIST.getModel();
        Point scroll = PANE.getViewport().getViewPosition();
        String selected = LIST.getSelectedValue();
        model.removeAllElements();
        for (String host: list) {
            model.addElement(host);
        }
        if (model.getSize() != 0) {
            if (model.indexOf(selected) == -1) {
                selected = model.getElementAt(0);
                scroll = new Point(0, 0);
            }
            LIST.setSelectedValue(selected, false);
            PANE.getViewport().setViewPosition(scroll);
        }
        BT_SELECT.setEnabled(model.getSize() != 0);
        LIST.invalidate(); // frissítés kezdeményezése
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
