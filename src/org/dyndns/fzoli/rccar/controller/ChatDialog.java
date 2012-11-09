package org.dyndns.fzoli.rccar.controller;

import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;

/**
 *
 * @author zoli
 */
public class ChatDialog extends JDialog {

    private final JList<String> CONTROLLER_LIST = new JList<String>(new DefaultListModel<String>());
    
    public ChatDialog() {
        
    }
    
    public static void main(String[] args) {
        new ChatDialog();
    }
    
}
