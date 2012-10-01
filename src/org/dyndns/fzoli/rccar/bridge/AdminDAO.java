package org.dyndns.fzoli.rccar.bridge;

import java.io.File;
import java.sql.Types;
import org.h2.tools.Csv;
import org.h2.tools.SimpleResultSet;

/**
 * A híd admin adatbázisa.
 * A vezérlő programok felhasználóit állítja sorrendbe.
 * @author zoli
 */
public class AdminDAO {

    /**
     * H2 API CSV manipuláló objektuma.
     */
    private static final Csv CSV = new Csv();
    
    /**
     * Az adatbázist tároló CSV fájl.
     */
    private static final File FILE_CSV = new File(System.getProperty("user.dir"), "bridge-admin.csv");
    
    /**
     * Az értékek maximum hossza.
     */
    private static final int VAL_PRECISION = 255;
    
    /**
     * A CSV adatbázis oszlopainak kulcsai.
     */
    private static final String KEY_HOST = "HOST",
                                KEY_CONTROLLER = "CONTROLLER",
                                KEY_ORDER = "ORDER";
    
    private static boolean isNew;
    
    /**
     * Az osztály memóriába töltésekor lefutó blokk.
     * Inicializál és megadja, hogy új-e az adatbázis.
     */
    static {
        isNew = init();
    }
    
    /**
     * Ez az osztály nem példányosítható.
     */
    private AdminDAO() {
    }
    
    /**
     * @return true, ha az adatbázis most lett létrehozva, egyébként false
     */
    public static boolean isNew() {
        return isNew;
    }
    
    /**
     * @return true, ha az adatbázis-fájl létezik, egyébként false
     */
    public static boolean exists() {
        return FILE_CSV.exists();
    }
    
    /**
     * Inicializáló metódus.
     * Ha az adatbázis-fájl nem létezik, létrehozza a példa adatbázist.
     * @return true, ha létre lett hozva az adatbázis, egyébként false
     */
    private static boolean init() {
        if (!exists()) {
            SimpleResultSet rs = createResultSet();
            addRow(rs, "host", "controller", 0);
            addRow(rs, "host2", "controller2", 0);
            write(rs);
            return true;
        }
        return false;
    }
    
    /**
     * Az adatbázis módosítására használható ResultSet.
     */
    private static SimpleResultSet createResultSet() {
        SimpleResultSet rs = new SimpleResultSet();
        rs.addColumn(KEY_HOST, Types.VARCHAR, VAL_PRECISION, 0);
        rs.addColumn(KEY_CONTROLLER, Types.VARCHAR, VAL_PRECISION, 0);
        rs.addColumn(KEY_ORDER, Types.INTEGER, VAL_PRECISION, 0);
        return rs;
    }
    
    /**
     * Ellenőrzötten ad hozzá egy sort a ResultSet objektumhoz.
     * @return true, ha hozzáadódott a sor, egyébként false
     */
    private static boolean addRow(SimpleResultSet rs, String host, String controller, int order) {
        if (rs == null || host == null || controller == null) return false;
        if (host.isEmpty() || host.length() > VAL_PRECISION || controller.isEmpty() || controller.length() > VAL_PRECISION) return false;
        rs.addRow(host, controller, order);
        return true;
    }
    
    /**
     * A CSV fájlt kiüríti és kiírja a fájlba a ResultSet tartalmát.
     */
    private static boolean write(SimpleResultSet rs) {
        try {
            CSV.write(FILE_CSV.getAbsolutePath(), rs, null);
            rs.close();
            return true;
        }
        catch(Exception ex) {
            return false;
        }
    }
    
}
