package org.dyndns.fzoli.rccar.ui;

import java.awt.Image;
import org.dyndns.fzoli.ui.OptionPane;

/**
 * Általános UI metódusok, amik kellhetnek több helyen is.
 * @author zoli
 */
public class UIUtil extends org.dyndns.fzoli.ui.UIUtil {
    
    /**
     * Bekéri a tanúsítvány jelszavát a felhasználótól.
     * Ha a grafikus felület elérhető, dialógus ablakban kéri be a jelszót,
     * egyébként megpróbálja konzolról bekérni a jelszót.
     * Ha nincs se konzol, se grafikus felület, a program kilép.
     * Ha a dialógus ablakon nem az OK-ra kattintottak, a program kilép.
     * @param icon a címsorban megjelenő ikon
     * @param saveEnabled engedélyezve legyen-e a jelszó mentése checkbox
     */
    public static OptionPane.PasswordData showPasswordInput(Image icon, boolean saveEnabled) {
        return OptionPane.showPasswordInput("A tanúsítvány beolvasása sikertelen volt.", "Adja meg a tanúsítvány jelszavát, ha van: ", icon, saveEnabled);
    }
    
}
