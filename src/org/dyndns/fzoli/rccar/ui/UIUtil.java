package org.dyndns.fzoli.rccar.ui;

import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.UIManager;
import static javax.swing.UIManager.getString;
import static javax.swing.UIManager.put;
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
        if (UIManager.get(key) == null) put(key, value);
    }
    
    /**
     * Létrehoz egy szótárat a kért nyelvhez és az UIManager-ben megadott, több helyen is használt szövegeket beállítja.
     */
    public static ResourceBundle createResource(String baseName, Locale locale) {
        ResourceBundle res = ResourceBundle.getBundle(baseName, locale);
        put(UIUtil.KEY_CERT_LOAD_ERROR, res.getString("cert_load_error"));
        put(UIUtil.KEY_CERT_ENTER_PASSWORD, res.getString("cert_enter_password"));
        put(UncaughtExceptionHandler.KEY_UNEXPECTED_ERROR, res.getString("unexpected_error"));
        put(UncaughtExceptionHandler.KEY_UNEXPECTED_ERROR_MSG, res.getString("unexpected_error_msg"));
        put(UncaughtExceptionHandler.KEY_CLICK_FOR_DETAILS, res.getString("click_for_details"));
        put(UncaughtExceptionHandler.KEY_CLOSE, res.getString("close"));
        put(UncaughtExceptionHandler.KEY_COPY, res.getString("copy"));
        put(UncaughtExceptionHandler.KEY_DETAILS, res.getString("details"));
        put(UncaughtExceptionHandler.KEY_EXIT, res.getString("exit"));
        put(UncaughtExceptionHandler.KEY_SELECT_ALL, res.getString("select_all"));
        return res;
    }
    
    /**
     * Megadja az elérhető nyelvek kódjait és neveit.
     * A Java források gyökérkönyvtárában keres.
     * @param bundlename a ResourceBundle baseName paramétere
     */
    public static Map<String, String> getBundleLanguages(String bundlename) {
        Set<String> lngs = getBundleLngs("", bundlename);
        Map<String, String> res = new HashMap<String, String>();
        for (Locale l : Locale.getAvailableLocales()) {
            for (String lng : lngs) {
                if (lng.equalsIgnoreCase(l.getLanguage())) {
                    String name = l.getDisplayLanguage();
                    name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                    res.put(lng, name);
                    break;
                }
            }
        }
        return res;
    }
    
    /**
     * Megadja az elérhető nyelvek kódjait.
     * Forrás: http://stackoverflow.com/questions/2685907/list-all-available-resourcebundle-files
     * @param bundlepackage a csomag neve, amiben a resource fájlok vannak
     * @param bundlename a ResourceBundle baseName paramétere
     */
    private static Set<String> getBundleLngs(final String bundlepackage, final String bundlename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        File root = new File(loader.getResource(bundlepackage.replace('.', '/')).getFile());
        
        File[] files = root.listFiles(new FilenameFilter() {
            
            @Override
            public boolean accept(File dir, String name) {
                return name.matches("^" + bundlename + "(_\\w{2}(_\\w{2})?)?\\.properties$");
            }
            
        });

        Set<String> languages = new TreeSet<String>();
        for (File file : files) {
            languages.add(file.getName().replaceAll("^" + bundlename + "(_)?|\\.properties$", ""));
        }
        return languages;
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
        return OptionPane.showPasswordInput(getString(KEY_CERT_LOAD_ERROR), getString(KEY_CERT_ENTER_PASSWORD), icon, saveEnabled, showOnTaskbar, extraText, extraCallback);
    }
    
}
