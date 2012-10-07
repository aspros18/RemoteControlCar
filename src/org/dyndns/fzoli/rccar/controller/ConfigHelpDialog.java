package org.dyndns.fzoli.rccar.controller;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * A kapcsolatbeállító ablak súgója.
 * @author zoli
 */
public class ConfigHelpDialog extends JDialog {

    private static final JLabel taHelp = new JLabel("<html>Ahhoz, hogy kapcsolódni lehessen a szerverhez,<br>meg kell adni az elérési útvonalát, ami a címből<br>és a portból áll. A cím lehet IP cím és domain is.<br><br>A kommunikáció titkosított kapcsolaton kereszül<br>folyik, ezért a kliensnek szüksége van tanúsítványra.<br><br>A tanúsítvány három fájlból tevődik össze:<br>- kiállító<br>- nyílvános kulcs<br>- titkos kulcs<br><br>Mindhárom fájl szükséges ahhoz, hogy a kapcsolat<br>létrejöhessen a szerverrel, ezért addig nem indítható<br>a program, míg nincs mind a három fájl beállítva.</html>");
    
    public ConfigHelpDialog(Dialog owner) {
        super(owner, "Súgó"); // szülő és címsor szöveg beállítása
        setDefaultCloseOperation(HIDE_ON_CLOSE); // bezáráskor elrejtődés
        setModalityType(ModalityType.APPLICATION_MODAL); // modális dialógus
        
        taHelp.setOpaque(true); // ne legyen átlátszó
        taHelp.setBackground(Color.WHITE); // fehér háttérszín
        taHelp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 5x5-ös margó
        
        JScrollPane sp = new JScrollPane(taHelp); // a szöveg scrollozható ...
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // ... de csak vertikálisan
        add(sp); // scrollpane hozzáadása az ablakhoz
        
        pack(); // minimális méret beállítása és ...
        setMinimumSize(new Dimension(getSize().width + 30, 1)); // ... az ablakmagasság csak ennél nagyobb lehet
        setLocationRelativeTo(owner); // szülő ablak szerint középre igazítás
    }
    
}
