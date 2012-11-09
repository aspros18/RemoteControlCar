package org.dyndns.fzoli.rccar.controller;

import java.awt.Window;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JList;
import static org.dyndns.fzoli.rccar.controller.ControllerWindows.IC_CHAT;
import org.dyndns.fzoli.rccar.controller.ControllerWindows.WindowType;

/**
 *
 * @author zoli
 */
public class ChatDialog extends AbstractDialog {

    private final JList<String> CONTROLLER_LIST = new JList<String>(new DefaultListModel<String>());
    
    public ChatDialog(Window owner, final ControllerWindows windows) {
        super(owner, "Chat", windows);
        setIconImage(IC_CHAT.getImage());
        setSize(640, 200);
    }

    @Override
    public WindowType getWindowType() {
        return WindowType.CHAT;
    }
    
    public static void main(String[] args) {
        JDialog d = new ChatDialog(null, null);
        d.setLocationRelativeTo(d);
        d.setVisible(true);
    }
    
}
