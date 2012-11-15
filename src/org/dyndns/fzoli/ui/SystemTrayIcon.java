package org.dyndns.fzoli.ui;

import java.io.InputStream;
import org.dyndns.fzoli.ui.systemtray.PopupMenu;
import org.dyndns.fzoli.ui.systemtray.SystemTray;
import org.dyndns.fzoli.ui.systemtray.SystemTrayProvider;
import org.dyndns.fzoli.ui.systemtray.TrayIcon;
import org.dyndns.fzoli.ui.systemtray.TrayIcon.IconType;

/**
 * Ha van grafikus felület, rendszerikont jelenít meg.
 * Több funkciója is van az ikonnak:
 * - a felhasználó látja, hogy a program fut még akkor is, ha háttérben van
 * - a felhasználó ha nem konzolból indította a programot, csak itt képes leállítani, ha a háttérben fut
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
            tray = SystemTrayProvider.getSystemTray();
            if (tray.isSupported()) {
                icon = tray.addTrayIcon();
                menu = icon.createPopupMenu();
                visible = true;
            }
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
     * Értéke megadja, hogy látható-e a rendszerikon.
     * A rendszerikon nem tehető láthatóvá olyan rendszeren, ahol nem támogatott.
     */
    public static boolean isVisible() {
        return visible;
    }
    
    /**
     * Menüelemet ad hozzá a rendszerikon menüjéhez, ha az támogatott.
     * @param label a menüben megjelenő szöveg
     * @param callback eseménykezelő
     */
    public static void addMenuItem(String label, Runnable callback) {
        if (isSupported()) {
            menu.addMenuItem(label, callback);
        }
    }
    
    /**
     * Checkbox Menüelemet ad hozzá a rendszerikon menüjéhez, ha az támogatott.
     * @param label a menüben megjelenő szöveg
     * @param checked kezdőérték
     * @param callback eseménykezelő
     */
    public static void addCheckboxMenuItem(String label, boolean checked, Runnable callback) {
        if (isSupported()) {
            menu.addCheckboxMenuItem(label, checked, callback);
        }
    }
    
    /**
     * A menühöz szeparátort ad hozzá, ha az támogatott.
     */
    public static void addMenuSeparator() {
        if (isSupported()) {
            menu.addSeparator();
        }
    }
    
    /**
     * Beállítja a paraméterben átadott szöveget és ikont, ha támogatva vannak a rendszerikonok.
     * @param tooltip a megjelenő szöveg, amikor az egér az ikon felett van
     * @param img az a kép, ami megjelenik az ikonban
     * @throws NullPointerException ha a kép null
     */
    public static void setIcon(String tooltip, InputStream in) {
        if (isSupported()) {
            icon.setToolTip(tooltip);
            icon.setImage(in);
        }
    }
    
    /**
     * Kérésre megjeleníti, vagy elrejti a rendszerikont.
     * A kérés csak akkor teljesül, ha támogatott a rendszerikonok használata,
     * valamint ha látható ikont eltűnésre vagy nem látható ikont megjelenésre kérnek.
     */
    public static void setVisible(boolean visible) {
        if (isSupported()) {
            SystemTrayIcon.visible = visible;
            icon.setVisible(visible);
        }
    }
    
    /**
     * Üzenetet jelenít meg buborékablakban a felhasználónak.
     * Az üzenet ikonja információközlő.
     * Ha a rendszerikon nem támogatott vagy nem látható, konzolra megy az üzenet.
     * @param title az üzenet lényege pár szóban
     * @param text a teljes üzenet
     * @return ha az üzenet a rendszerikonban jelent meg, true, ha a konzolon, false
     */
    public static boolean showMessage(String title, String text) {
        return showMessage(title, text, IconType.INFO);
    }
    
    /**
     * Üzenetet jelenít meg buborékablakban a felhasználónak.
     * Ha a rendszerikon nem támogatott vagy nem látható, konzolra megy az üzenet.
     * @param title az üzenet lényege pár szóban
     * @param text a teljes üzenet
     * @param type az ikon típusa
     * @return ha az üzenet a rendszerikonban jelent meg, true, ha a konzolon, false
     */
    public static boolean showMessage(String title, String text, IconType type) {
        return showMessage(title, text, type, null);
    }
    
    /**
     * Üzenetet jelenít meg buborékablakban a felhasználónak.
     * Ha a rendszerikon nem támogatott vagy nem látható, konzolra megy az üzenet.
     * @param title az üzenet lényege pár szóban
     * @param text a teljes üzenet
     * @param type az ikon típusa
     * @param callback opcionális eseménykezelő, ami akkor fut le, ha az üzenetre kattintanak
     * @return ha az üzenet a rendszerikonban jelent meg, true, ha a konzolon, false
     */
    public static boolean showMessage(String title, String text, IconType type, Runnable callback) {
        if (visible && isSupported()) {
            icon.displayMessage(title, text, type, callback);
            return true;
        }
        else { // ha az ikon nem támogatott vagy nem látható, konzolra megy az üzenet
            UIUtil.print(title, text, IconType.ERROR == type ? System.err : System.out);
            return false;
        }
    }
    
}
