package org.dyndns.fzoli.rccar.controller;

import java.awt.Dialog;
import javax.swing.JDialog;

/**
 * A kapcsolatbeállító ablak súgója.
 * @author zoli
 */
public class ConfigHelpDialog extends JDialog {

    public ConfigHelpDialog(Dialog owner) {
        super(owner, "Súgó");
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setModalityType(ModalityType.APPLICATION_MODAL);
        setSize(200, 100);
        setLocationRelativeTo(owner);
    }
    
}
