package org.dyndns.fzoli.rccar.controller.view.config;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
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

    /**
     * A szöveg egyszerű HTML kódja.
     */
    private static final JLabel taHelp = new JLabel("<html><span style=\"font-size: 11px\"><p style=\"margin: 5px 0px 5px 0px\"><b>Útvonal</b></p>Ahhoz, hogy kapcsolódni lehessen a szerverhez,<br>meg kell adni az elérési útvonalát, ami a címből<br>és a portból áll. A cím lehet IP cím és domain is.<p style=\"margin: 5px 0px 5px 0px\"><b>Tanúsítvány</b></p><p style=\"margin: 0px 0px 5px 0px\">A kommunikáció titkosított kapcsolaton keresztül (SSL)<br>folyik, ezért a kliensnek szüksége van tanúsítványra.</p>A tanúsítvány három fájlból tevődik össze:<br>- Kiállító <i>(CA tanúsítvány)</i><br>- Tanúsítvány <i>(nyílvános kulcs)</i><br>- Kulcs <i>(titkos kulcs)</i><p style=\"margin: 5px 0px 5px 0px\"><b>Mi ez?</b></p>Mindhárom fájl szükséges ahhoz, hogy a kapcsolat<br>létrejöhessen a szerverrel, ezért addig nem indítható<br>a program, míg nincs az útvonal és mindhárom fájl beállítva.</span></html>");
    
    public ConfigHelpDialog(Window owner) {
        super(owner, "Súgó"); // szülő és címsor szöveg beállítása
        setDefaultCloseOperation(HIDE_ON_CLOSE); // bezáráskor elrejtődés
        setModalityType(ModalityType.APPLICATION_MODAL); // modális dialógus
        
        taHelp.setOpaque(true); // ne legyen átlátszó
        taHelp.setBackground(Color.WHITE); // fehér háttérszín
        taHelp.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // 5x5-ös margó
        
        JScrollPane sp = new JScrollPane(taHelp); // a szöveg scrollozható ...
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); // ... de csak vertikálisan
        sp.setBorder(null); // mindkét border eltüntetése
        sp.setViewportBorder(null);
        add(sp); // scrollpane hozzáadása az ablakhoz
        
        pack(); // minimális méret beállítása és ...
        setMinimumSize(new Dimension(getSize().width + 30, 1)); // ... az ablakmagasság csak ennél nagyobb lehet
        setLocationRelativeTo(owner); // szülő ablak szerint középre igazítás
    }
    
}
