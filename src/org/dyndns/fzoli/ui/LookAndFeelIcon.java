package org.dyndns.fzoli.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 *
 * @author zoli
 */
public class LookAndFeelIcon {
    
    /**
     * Visszatér egy LAF ikonnal.
     * Ha a megadott kulcshoz nem tartozik ikon, a paraméterben átadott képpel tér vissza,
     * de ha nem adtak meg paraméterben képet, bepróbálkozik a NimbusLookAndFeel témával végül az alapértelmezettel.
     * @return ha a LAF ikon null, a paraméterben átadott ikonnal tér vissza.
     */
    public static Icon createIcon(Component component, String key, Image img) {
        try {
            if (img == null) {
                Icon icon = UIManager.getIcon(key);
                img = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                icon.paintIcon(component, img.getGraphics(), 0, 0);
            }
            return new ImageIcon(img);
        }
        catch (Exception ex) {
            try {
                LookAndFeel tmp = UIManager.getLookAndFeel();
                try {
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                }
                catch (Exception e) {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                }
                if (tmp.equals(UIManager.getLookAndFeel())) return null;
                Icon icon = createIcon(component, key, null);
                UIManager.setLookAndFeel(tmp);
                return icon;
            }
            catch(Exception e) {
                ;
            }
            return null;
        }
    }
    
}
