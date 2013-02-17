package org.dyndns.fzoli.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;

/**
 *
 * @author zoli
 */
public abstract class LanguageSelectionFrame extends JFrame {

    private String text = "";
    
    private Object lastSelection;
    
    private boolean loaded = false;
    
    private final Map<Locale, String> MAP_LOCALES;
    
    private final DefaultComboBoxModel<Locale> MODEL_LOCALES = new DefaultComboBoxModel<Locale>();
    
    private final DefaultListCellRenderer LCR_LOCALES = new DefaultListCellRenderer() {

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, getLocaleDisplayLanguage((Locale) value), index, isSelected, cellHasFocus);
        }
        
    };
    
    private final JComboBox<Locale> CB_LOCALES = new JComboBox<Locale>(MODEL_LOCALES) {
        {
            setRenderer(LCR_LOCALES);
            setKeySelectionManager(new KeySelectionManager() {

                @Override
                public int selectionForKey(char aKey, ComboBoxModel aModel) {
                    switch (KeyEvent.getExtendedKeyCodeForChar(aKey)) {
                        case KeyEvent.VK_DELETE:
                            text = "";
                            break;
                        case KeyEvent.VK_BACK_SPACE:
                            text = "";
                            break;
                        default:
                            if (Character.isLetter(aKey)) text += aKey;
                    }
                    if (text.isEmpty()) return 0;
                    Iterator<Entry<Locale, String>> it = MAP_LOCALES.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<Locale, String> e = it.next();
                        if (e.getValue().toLowerCase().startsWith(text.toLowerCase())) {
                            return MODEL_LOCALES.getIndexOf(e.getKey());
                        }
                    }
                    return getSelectedIndex();
                }
                
            });
            addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!loaded) return;
                    Locale selection = (Locale) CB_LOCALES.getSelectedItem();
                    if (lastSelection == null || selection != lastSelection) {
                        onLanguageSelected(selection);
                        lastSelection = selection;
                    }
                }
                
            });
        }
    };
    
    public LanguageSelectionFrame(Image icon, String name) {
        this(icon, "", name);
    }
    
    public LanguageSelectionFrame(Image icon, String name, Locale def) {
        this(icon, "", name, def);
    }
    
    public LanguageSelectionFrame(Image icon, String pkg, String name) {
        this(icon, pkg, name, null);
    }
    
    public LanguageSelectionFrame(Image icon, String pkg, String name, Locale def) {
        super("Nyelv");
        setIconImage(icon);
        MAP_LOCALES = getBundleLanguages(pkg, name);
        Iterator<Locale> it = MAP_LOCALES.keySet().iterator();
        while (it.hasNext()) {
            MODEL_LOCALES.addElement(it.next());
        }
        
        CB_LOCALES.setSelectedItem(def == null ? Locale.getDefault() : def);
        lastSelection = CB_LOCALES.getSelectedItem();
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        add(CB_LOCALES);
        pack();
        setLocationRelativeTo(this);
        loaded = true;
    }
    
    protected abstract void onLanguageSelected(Locale l);
    
    /**
     * Megadja az elérhető nyelvek kódjait és neveit.
     * @param bundlepackage a csomag neve, amiben a resource fájlok vannak
     * @param bundlename a ResourceBundle baseName paramétere
     */
    public static Map<Locale, String> getBundleLanguages(String bundlepackage, String bundlename) {
        Set<String> lngs = getBundleLngs(bundlepackage, bundlename);
        Map<Locale, String> res = new HashMap<Locale, String>();
        for (Locale l : Locale.getAvailableLocales()) {
            for (String lng : lngs) {
                if (lng.equalsIgnoreCase(l.getLanguage()) && !containsLanguage(res, l)) {
                    res.put(l, getLocaleDisplayLanguage(l));
                    break;
                }
            }
        }
        return res;
    }
    
    private static boolean containsLanguage(Map<Locale, String> m, Locale l) {
        Iterator<Locale> it = m.keySet().iterator();
        while (it.hasNext()) {
            if (it.next().getLanguage().equalsIgnoreCase(l.getLanguage())) return true;
        }
        return false;
    }
    
    /**
     * Megadja a Locale objektumhoz tartozó nyelvet.
     * A szöveg anyanyelvű és az első karaktere nagy karakter.
     */
    private static String getLocaleDisplayLanguage(Locale l) {
        String name = l.getDisplayLanguage();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
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
    
    public static void main(String[] args) {
        UIUtil.setSystemLookAndFeel();
        new LanguageSelectionFrame(null, "controller_lng") {

            @Override
            protected void onLanguageSelected(Locale l) {
                System.out.println(getLocaleDisplayLanguage(l));
            }
            
        }.setVisible(true);
    }
    
}
