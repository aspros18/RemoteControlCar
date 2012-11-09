package org.dyndns.fzoli.rccar.controller;

import java.awt.Dimension;
import java.awt.Window;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_CHAT;

/**
 *
 * @author zoli
 */
public class ChatDialog extends JDialog {

    private final JList<String> CONTROLLER_LIST = new JList<String>(new DefaultListModel<String>());
    
    public ChatDialog(Window owner) {
        super(owner, "Chat");
        setIconImage(IC_CHAT.getImage());
        JLabel test = new JLabel();
        test.setPreferredSize(new Dimension(640, 200));
        add(test);
        pack();
    }
    
    public static void main(String[] args) {
        new ChatDialog(null).setVisible(true);
    }
    
}
