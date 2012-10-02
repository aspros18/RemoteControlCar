package org.dyndns.fzoli.rccar.bridge;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import static org.dyndns.fzoli.rccar.UIUtil.print;
import org.dyndns.fzoli.rccar.bridge.resource.R;

/**
 * Ha van grafikus felület, rendszerikont jelenít meg.
 * Több funkciója is van az ikonnak:
 * - a felhasználó látja, hogy a program fut
 * - a felhasználó ha nem konzolból indította a programot, csak itt képes leállítani
 * - nem kezelt kivétel esetén buborékablak tályékoztatja a felhasználót, amire kattintva megtekintheti a hibát
 * @author zoli
 */
public class SystemTrayIcon {

    /**
     * A rendszerikon működését szabályzó objektumok.
     */
    private static SystemTray tray;
    private static TrayIcon icon;
    private static PopupMenu menu;
    
    /**
     * Kezdetben a rendszerikon nem látszódik.
     */
    private static boolean visible = false;
    
    /**
     * Nincs szükség az osztály példányosítására.
     */
    private SystemTrayIcon() {
    }
    
    /**
     * Osztály inicializálása.
     * Furán hangzik, de ilyen is létezik. :-)
     */
    static {
        try {
            tray = SystemTray.getSystemTray();
            menu = new PopupMenu();
            icon = new TrayIcon(R.getBridgeImage(), "Mobile-RC Híd", menu);
            icon.setImageAutoSize(true);
        }
        catch (Exception ex) {
            ;
        }
    }
    
    /**
     * Értéke megadja, hogy támogatott-e a rendszerikon az adott rendszeren.
     * @return true, ha támogatott a rendszerikon
     */
    public static boolean isSupported() {
        return icon != null;
    }
    
    /**
     * Menüelemet ad hozzá a rendszerikon menüjéhez, ha az támogatott.
     * @param label a menüben megjelenő szöveg
     * @param callback eseménykezelő
     */
    public static void addMenuItem(String label, ActionListener callback) {
        if (isSupported()) {
            MenuItem mi = new MenuItem(label);
            mi.addActionListener(callback);
            menu.add(mi);
        }
    }
    
    /**
     * Kérésre megjeleníti, vagy elrejti a rendszerikont.
     * A kérés csak akkor teljesül, ha támogatott a rendszerikonok használata,
     * valamint ha látható ikont eltűnésre vagy nem látható ikont megjelenésre kérnek.
     */
    public static void setVisible(boolean visible) {
        if (SystemTrayIcon.visible ^ visible && isSupported()) {
            synchronized (tray) { // amíg a művelet végre nem hajtódik, lefoglalom az objektumot
                try {
                    if (visible) tray.add(icon); // megjelenítés esetén hozzáadás
                    else tray.remove(icon); // elrejtés esetén eltávolítás
                    SystemTrayIcon.visible = visible; // ha minden sikerült, módosítható a láthatóságot kezelő változó
                }
                catch (Exception ex) { // kivétel esetén semmi nem változik
                    ;
                }
            }
        }
    }
    
    /**
     * Üzenetet jelenít meg buborékablakban a felhasználónak.
     * Ha a rendszerikon nem támogatott vagy nem látható, konzolra megy az üzenet.
     * @param title az üzenet lényege pár szóban
     * @param text a teljes üzenet
     * @param type az ikon típusa
     */
    public static void showMessage(String title, String text, TrayIcon.MessageType type) {
        if (visible && isSupported()) {
            icon.displayMessage("caption", "text", type);
        }
        else {
            print(title, text, TrayIcon.MessageType.ERROR == type ? System.err : System.out);
        }
    }
    
}
