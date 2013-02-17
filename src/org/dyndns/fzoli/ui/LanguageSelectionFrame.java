package org.dyndns.fzoli.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * Nyelvkiválasztó ablak.
 * A {@code ResourceBundle} osztályhoz használt {@code properties} fájlok listája alapján megjeleníti az elérhető nyelveket egy lenyíló listában.
 * Ha a nyelvet módosították, a {@link #onLanguageSelected(java.util.Locale)} metódus hívódik meg, amit implementálni kell az utódokban.
 * @author zoli
 */
public abstract class LanguageSelectionFrame extends JFrame {

    /**
     * A lenyíló listához gyorskereső szöveg.
     */
    private String text = "";
    
    /**
     * Az utóljára kijelölt nyelv.
     */
    private Object lastSelection;
    
    /**
     * Megadja, hogy inicializálódott-e már az ablak.
     */
    private boolean loaded = false;
    
    /**
     * Az elérhető nyelvek és a hozzájuk tartozó feliratok.
     */
    private final Map<Locale, String> MAP_LOCALES;
    
    /**
     * A legördülő lista modelje.
     */
    private final DefaultComboBoxModel<Locale> MODEL_LOCALES = new DefaultComboBoxModel<Locale>();
    
    /**
     * A legördülő lista kinézetét szabályozó objektum.
     */
    private final DefaultListCellRenderer LCR_LOCALES = new DefaultListCellRenderer() {

        /**
         * A felsorolás elemei nagy betűvel kezdődnek és a {@link Locale} nyelvén jelennek meg.
         */
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, getLocaleDisplayLanguage((Locale) value), index, isSelected, cellHasFocus);
        }
        
    };
    
    /**
     * A legördülő lista gyorskeresés támogatással.
     */
    private final JComboBox<Locale> CB_LOCALES = new JComboBox<Locale>(MODEL_LOCALES) {
        {
            setRenderer(LCR_LOCALES);
            setKeySelectionManager(new KeySelectionManager() {

                /**
                 * A leütött karaktert hozzáadja a gyorskereső szöveghez,
                 * ha back space vagy delete gomb lett lenyomva, a gyorskereső szöveget kiüríti, végül
                 * megkeresi a listában azt a nyelvet, amire illeszkedik a gyorskereső szöveg.
                 * Ha a gyorskereső szöveg üres, akkor a lista első elemét jelöli ki.
                 * Ha a gyorskereső szövegre nincs illeszkedés, nem változik a kijelölés.
                 * Ha nem karaktert ütöttek le (pl. F1, Ctrl, Shift), nem módosul a gyorskereső szöveg.
                 */
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

                /**
                 * Ha kijelölés történt, meghívja az eseményfeldolgozó metódust,
                 * de csak akkor, ha nem ugyan azt jelölték ki, mint ami előtte ki volt jelölve
                 * és ha az inicializálás befejeződött.
                 */
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
    
    /**
     * Konstruktor.
     * A {@code class} fájlok gyökérkönyvtárában keresi a {@code properties} fájlokat.
     * @param icon az ablak ikonja
     * @param name a {@code properties} fájlok kezdőneve (a ResourceBundle baseName paramétere)
     */
    public LanguageSelectionFrame(Image icon, String name) {
        this(icon, "", name);
    }
    
    /**
     * Konstruktor.
     * A {@code class} fájlok gyökérkönyvtárában keresi a {@code properties} fájlokat.
     * @param icon az ablak ikonja
     * @param name a {@code properties} fájlok kezdőneve (a ResourceBundle baseName paramétere)
     * @param def az alapértelmezett nyelv
     */
    public LanguageSelectionFrame(Image icon, String name, Locale def) {
        this(icon, "", name, def);
    }
    
    /**
     * Konstruktor.
     * A rendszer nyelve az alapértelmezett nyelv.
     * @param icon az ablak ikonja
     * @param pkg a csomag neve, ahol a {@code properties} fájlok vannak
     * @param name a {@code properties} fájlok kezdőneve (a ResourceBundle baseName paramétere)
     */
    public LanguageSelectionFrame(Image icon, String pkg, String name) {
        this(icon, pkg, name, null);
    }
    
    /**
     * Konstruktor.
     * @param icon az ablak ikonja
     * @param pkg a csomag neve, ahol a {@code properties} fájlok vannak
     * @param name a {@code properties} fájlok kezdőneve (a ResourceBundle baseName paramétere)
     * @param def az alapértelmezett nyelv
     */
    public LanguageSelectionFrame(Image icon, String pkg, String name, Locale def) {
        super("Languages");
        
        // nyelvek betöltése és legördülő lista feltöltése
        MAP_LOCALES = getBundleLanguages(pkg, name);
        Iterator<Locale> it = MAP_LOCALES.keySet().iterator();
        while (it.hasNext()) {
            MODEL_LOCALES.addElement(it.next());
        }
        
        // az alapértelmezett nyelv kijelölése
        CB_LOCALES.setSelectedItem(def == null ? Locale.getDefault() : def);
        lastSelection = CB_LOCALES.getSelectedItem();
        
        // felület inicializálása
        setIconImage(icon);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 3, 3, 3);
        add(new JLabel("Available languages:"), c);
        c.gridy = 1;
        c.insets = new Insets(0, 3, 3, 3);
        add(CB_LOCALES, c);
        pack();
        if (getWidth() < 200) setSize(200, getHeight());
        setLocationRelativeTo(this);
        setResizable(false);
        
        // jelzés, hogy befejeződött az inicializálás
        loaded = true;
    }
    
    /**
     * Akkor hívódik meg, ha megváltozott a kijelölt nyelv.
     * @param l az új nyelv
     */
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
    
    /**
     * Megadja, hogy a nyelvek között szerepel-e a megadott nyelv.
     * A metódus nem tesz különbséget kis- és nagybetű között.
     * @param m a nyelvek megfeleltetése
     * @param l a keresett nyelv
     */
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
    
}
