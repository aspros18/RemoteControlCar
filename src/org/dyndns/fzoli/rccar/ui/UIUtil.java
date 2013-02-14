package org.dyndns.fzoli.rccar.ui;

import java.awt.Image;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.UIManager;
import org.dyndns.fzoli.ui.OptionPane;

/**
 * Általános UI metódusok, amik kellhetnek több helyen is.
 * @author zoli
 */
public class UIUtil extends org.dyndns.fzoli.ui.UIUtil {
    
    /**
     * Kulcs a lokalizált szöveghez.
     */
    public static final String KEY_CERT_LOAD_ERROR = "MobileRC.certLoadError",
                               KEY_CERT_ENTER_PASSWORD = "MobileRC.certEnterPassword";
    
    /**
     * Az alapértelmezett szövegek beállítása.
     */
    static {
        init(KEY_CERT_LOAD_ERROR, "Failed to load the certification.");
        init(KEY_CERT_ENTER_PASSWORD, "Enter the password of the certification:");
    }
    
    /**
     * Beállítja a kulcs értékét, de csak akkor, ha nincs még beállítva.
     */
    static void init(String key, String value) {
        if (UIManager.get(key) == null) UIManager.put(key, value);
    }
    
    /**
     * Létrehoz egy szótárat a kért nyelvhez és az UIManager-ben megadott, több helyen is használt szövegeket beállítja.
     */
    public static ResourceBundle createResource(String baseName, Locale locale) {
        ResourceBundle res = ResourceBundle.getBundle(baseName, locale);
        UIManager.put(UIUtil.KEY_CERT_LOAD_ERROR, res.getString("cert_load_error"));
        UIManager.put(UIUtil.KEY_CERT_ENTER_PASSWORD, res.getString("cert_enter_password"));
        UIManager.put(UncaughtExceptionHandler.KEY_UNEXPECTED_ERROR, res.getString("unexpected_error"));
        return res;
    }
    
    /**
     * Bekéri a tanúsítvány jelszavát a felhasználótól.
     * Ha a grafikus felület elérhető, dialógus ablakban kéri be a jelszót,
     * egyébként megpróbálja konzolról bekérni a jelszót.
     * Ha nincs se konzol, se grafikus felület, a program kilép.
     * Ha a dialógus ablakon nem az OK-ra kattintottak, a program kilép.
     * @param icon a címsorban megjelenő ikon
     * @param saveEnabled true esetén engedélyezve van a jelszó mentése
     * @param showOnTaskbar true esetén megjelenik a tálcán
     */
    public static OptionPane.PasswordData showPasswordInput(Image icon, boolean saveEnabled, boolean showOnTaskbar) {
        return showPasswordInput(icon, saveEnabled, showOnTaskbar, null, null);
    }
    
    /**
     * Bekéri a tanúsítvány jelszavát a felhasználótól.
     * Ha a grafikus felület elérhető, dialógus ablakban kéri be a jelszót,
     * egyébként megpróbálja konzolról bekérni a jelszót.
     * Ha nincs se konzol, se grafikus felület, a program kilép.
     * Ha a dialógus ablakon nem az OK-ra kattintottak, a program kilép.
     * @param icon a címsorban megjelenő ikon
     * @param saveEnabled true esetén engedélyezve van a jelszó mentése
     * @param showOnTaskbar true esetén megjelenik a tálcán
     * @param extraText a középső gomb felirata
     * @param extraCallback a középső gomb kattintására lefutó eseménykezelő
     */
    public static OptionPane.PasswordData showPasswordInput(Image icon, boolean saveEnabled, boolean showOnTaskbar, String extraText, Runnable extraCallback) {
        return OptionPane.showPasswordInput(UIManager.getString(KEY_CERT_LOAD_ERROR), UIManager.getString(KEY_CERT_ENTER_PASSWORD), icon, saveEnabled, showOnTaskbar, extraText, extraCallback);
    }
    
}
