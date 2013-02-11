package org.dyndns.fzoli.resource.lng;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Szöveggyűjtemény.
 * Egy adott könyvtár adott XML állománya alapján hoz létre egy szótárat.
 * @author zoli
 */
public class StringResource {
    
    /**
     * Egy nyelv fontos jellemzői.
     */
    public static class Language {
        
        public final String CODE, NAME;

        public Language(String CODE, String NAME) {
            this.CODE = CODE;
            this.NAME = NAME;
        }
        
    }
    
    /**
     * Az elérhető nyelvek listája.
     */
    private final List<Language> LANGS;
    
    /**
     * Elsődleges szótár.
     */
    private final Map<String, String> VALUES;
    
    /**
     * Másodlagos szótár.
     */
    private final Map<String, String> DEF_VALUES;
    
    /**
     * Opcionálisan használható objektum referencia.
     */
    private static StringResource sr;
    
    /**
     * Konstruktor.
     * Létrehozza a szótárat a megadott könyvtárban szereplő fájl alapján. A fájl kiterjesztése xml.
     * Ha a könyvtárban a rendszer nyelvéhez nem tartozik állomány, akkor az angol nyelv lesz használva.
     * @param dir a könyvtár, melyben az XML alapú nyelvi fájlok találhatóak
     * @throws RuntimeException ha az angol nyelvhez se tartozik állomány
     */
    public StringResource(File dir) {
        this(dir, Locale.getDefault());
    }
    
    /**
     * Konstruktor.
     * Létrehozza a szótárat a megadott könyvtárban szereplő fájl alapján. A fájl kiterjesztése xml.
     * Ha a könyvtárban a kért nyelvhez nem tartozik állomány, akkor az angol nyelv lesz használva.
     * @param dir a könyvtár, melyben az XML alapú nyelvi fájlok találhatóak
     * @param locale a kért nyelv
     * @throws RuntimeException ha az angol nyelvhez se tartozik állomány
     */
    public StringResource(File dir, Locale locale) {
        this(dir, locale, Locale.ENGLISH);
    }
    
    /**
     * Konstruktor.
     * Létrehozza a szótárat a megadott könyvtárban szereplő fájl alapján. A fájl kiterjesztése xml.
     * Ha a könyvtárban a kért nyelvhez nem tartozik állomány, akkor az alapértelmezett nyelv lesz használva.
     * @param dir a könyvtár, melyben az XML alapú nyelvi fájlok találhatóak
     * @param locale a kért nyelv
     * @param def az alapértelmezett nyelv
     * @throws RuntimeException ha az alapértelmezett nyelvhez se tartozik állomány
     */
    public StringResource(File dir, Locale locale, Locale def) {
        this(dir, "xml", locale, def);
    }
    
    /**
     * Konstruktor.
     * Létrehozza a szótárat a megadott könyvtárban szereplő fájl alapján.
     * Ha a könyvtárban a kért nyelvhez nem tartozik állomány, akkor az alapértelmezett nyelv lesz használva.
     * @param dir a könyvtár, melyben az XML alapú nyelvi fájlok találhatóak
     * @param ext a nyelvi fájlok kiterjesztése
     * @param locale a kért nyelv
     * @param def az alapértelmezett nyelv
     * @throws RuntimeException ha az alapértelmezett nyelvhez se tartozik állomány
     */
    public StringResource(File dir, String ext, Locale locale, Locale def) {
        File file = createFile(dir, ext, locale);
        File defFile = null;
        if (!file.isFile()) {
            defFile = createFile(dir, ext, def);
            if (!defFile.isFile()) throw new RuntimeException(new FileNotFoundException(defFile.getAbsolutePath()));
        }
        DEF_VALUES = file.equals(defFile) ? null : getValues(defFile);
        VALUES = getValues(file);
        LANGS = getLanguages(dir, ext);
    }
    
    /**
     * Megadja a könyvtárhoz tartozó fájl objektumot.
     * @param clazz az osztály kódja mellett lévő könyvtár
     * @param dirName a könyvtár neve
     */
    public static File getDirectory(Class clazz, String dirName) {
        return new File(clazz.getResource(dirName).getPath());
    }
    
    /**
     * A már beállított referenciát adja vissza.
     * @see #setResource(StringResource) 
     */
    public static StringResource getResource() {
        return sr;
    }
    
    /**
     * Beállít egy referenciát.
     */
    public static void setResource(StringResource sr) {
        StringResource.sr = sr;
    }
    
    /**
     * Azon nyelvek listája, melyek könyvtára és kiterjesztése megegyezik az objektuméval.
     */
    public List<Language> getLanguages() {
        return LANGS;
    }
    
    /**
     * A kulcshoz tartozó szöveget adja meg.
     * Ha a kért nyelvhez nincs találat, akkor az alapértelmezett nyelvben keres rá
     * feltéve, ha a kért nyelv nem az alapértelmezett nyelv.
     * Ha az alapértelmezett nyelvben sincs találat, null referenciával tér vissza.
     * @param key a keresett szó azonosítója, kulcsa
     */
    public String getString(String key) {
        return getString(key, null);
    }
    
    /**
     * A kulcshoz tartozó szöveget adja meg.
     * Ha a kért nyelvhez nincs találat, akkor az alapértelmezett nyelvben keres rá
     * feltéve, ha a kért nyelv nem az alapértelmezett nyelv.
     * @param key a keresett szó azonosítója, kulcsa
     * @param def ha nincs találat a a kért és az alapértelmezett nyelvben, ezzel az értékkel tér vissza
     */
    public String getString(String key, String def) {
        String result = VALUES.get(key);
        if (result == null && DEF_VALUES != null) result = DEF_VALUES.get(key);
        if (result == null) return def;
        return result;
    }
    
    /**
     * Az elérhető nyelvek listáját adja meg.
     * @param dir a könyvtár
     * @param ext a kiterjesztés
     */
    public static List<Language> getLanguages(File dir, String ext) {
        List<Language> l = new ArrayList<Language>();
        if (!dir.isDirectory()) return l;
        Locale[] locales = Locale.getAvailableLocales();
        for (Locale locale : locales) {
            String code = locale.getLanguage().toLowerCase();
            String name = locale.getDisplayLanguage().toLowerCase();
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
            if (!contains(l, code) && createFile(dir, ext, locale).isFile()) l.add(new Language(code, name));
        }
        return l;
    }
    
    /**
     * Megadja, hogy a listában benne van-e már az adott kód.
     */
    private static boolean contains(List<Language> ls, String code) {
        for (Language l : ls) {
            if (l.CODE.equalsIgnoreCase(code)) return true;
        }
        return false;
    }
    
    /**
     * Létrehozza a szótárat.
     * @param file az XML formátumú nyelvi fájl
     */
    private static Map<String, String> getValues(File file) {
        Map<String, String> values = Collections.synchronizedMap(new HashMap<String, String>());
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList strings = doc.getDocumentElement().getElementsByTagName("string");
            for (int i = 0; i < strings.getLength(); i++) {
                Node node = strings.item(i);
                values.put(((Element) node).getAttribute("name"), node.getTextContent());
            }
            return values;
        }
        catch (Exception ex) {
            ;
        }
        return values;
    }
    
    /**
     * Létrehozza a locale-hoz tartozó nyelvi fájlt.
     * @param dir a könyvtár, amiben a nyelvi fájl található
     * @param ext a nyelvi fájl kiterjesztése
     * @param locale a kért locale
     */
    private static File createFile(File dir, String ext, Locale locale) {
        if (ext == null) ext = "";
        return new File(dir, locale.getLanguage().toLowerCase() + '.' + ext);
    }
    
}
