package org.dyndns.fzoli.rccar.controller;

import java.awt.Dimension;
import java.awt.Window;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_CHAT;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;

/**
 * Chatablak.
 * @author zoli
 */
public class ChatDialog extends AbstractDialog {

    /**
     * A vezérlők listája.
     */
    private final JList<String> LIST_CONTROLLERS = new JList<String>(new DefaultListModel<String>());
    
    /**
     * Az üzenetkijelző és üzenetküldő panel.
     */
    private final JPanel PANEL_MESSAGES = new JPanel();
    
    public ChatDialog(Window owner, final ControllerWindows windows) {
        super(owner, "Chat", windows);
        setIconImage(IC_CHAT.getImage());
        
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, PANEL_MESSAGES, LIST_CONTROLLERS);
        pane.setContinuousLayout(true);
        pane.setDividerSize(2);
        pane.setBorder(null);
        add(pane);
        
        PANEL_MESSAGES.setPreferredSize(new Dimension(470, 200));
        LIST_CONTROLLERS.setPreferredSize(new Dimension(170 - 2, 200));
        pack();
    }

    /**
     * Az ablak típusát adja vissza.
     * Az ablak azonosításához szükséges.
     */
    @Override
    public WindowType getWindowType() {
        return WindowType.CHAT;
    }
    
    /**
     * Teszt.
     */
    public static void main(String[] args) {
        JDialog d = new ChatDialog(null, null);
        d.setLocationRelativeTo(d);
        d.setVisible(true);
    }
    
}
