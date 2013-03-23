package org.dyndns.fzoli.rccar;

import javax.swing.JFrame;
import org.dyndns.fzoli.rccar.ui.UIUtil;
import static org.dyndns.fzoli.ui.UIUtil.setApplicationName;
import static org.dyndns.fzoli.ui.UIUtil.setSystemLookAndFeel;

/**
 * A Híd- vagy a vezérlő-alkalmazás elindító osztálya.
 * Ha a parancsorban megadott első paraméter <code>client</code>,
 * akkor a vezérlő indul el; ha <code>server</code>, akkor a Híd.
 * Ha nincs megadva paraméter, vagy az első paraméter fentiek egyikére
 * se illeszkedik, akkor a program kilép.
 * Ha további paraméterek is meg lettek adva, az első paraméter kivételével
 * minden további paraméter átadódik a meghívandó alkalmazásnak.
 * @author zoli
 */
public class Main extends JFrame {
    
    /**
     * Az alkalmazás indítása előtt
     * az alkalmazásnév és a LookAndFeel beállítódik.
     */
    static {
        setApplicationName("Mobile-RC");
        setSystemLookAndFeel();
    }
    
    /**
     * A Híd- vagy a vezérlő-alkalmazást elindító metódus.
     * @see Main
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            String[] arg = new String[args.length - 1];
            for (int i = 1; i < args.length; i++) {
                arg[i - 1] = args[i];
            }
            if ("client".equalsIgnoreCase(args[0])) {
                org.dyndns.fzoli.rccar.controller.Main.main(arg);
                return;
            }
            if ("server".equalsIgnoreCase(args[0])) {
                org.dyndns.fzoli.rccar.bridge.Main.main(arg);
                return;
            }
        }
        UIUtil.alert("Error", "No application has specified.", System.err);
    }
    
}
