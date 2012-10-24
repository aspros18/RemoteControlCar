package org.dyndns.fzoli.rccar.controller;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JList;
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
     * A felületi komponens modelje.
     */
    private final HostList LIST_MODEL = new HostList();
    
    /**
     * Konstruktor
     * @param alExit az ablak bezárásakor lefutó eseményfigyelő
     */
    public HostSelectionFrame(ActionListener alExit) {
        initFrame();
        setExitListener(alExit);
    }
    
    /**
     * Beállítja a komponenseket még a megjelenés előtt.
     */
    private void initFrame() {
        add(LIST);
        setMinimumSize(new Dimension(300, 200));
        pack();
        setLocationRelativeTo(this);
    }
    
    /**
     * Frissíti a felületi komponenst a modelje alapján.
     * Eltávolítja az adatokat, majd újra feltölti a friss adatokat és ha volt, beállítja az előtte kiválasztott elemet.
     */
    private void refresh() {
        String selected = LIST.getSelectedValue();
        DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) LIST.getModel();
        model.removeAllElements();
        for (String host: LIST_MODEL.getHosts()) {
            model.addElement(host);
        }
        LIST.setSelectedValue(selected, true);
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
