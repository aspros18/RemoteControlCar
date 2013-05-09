/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

/**
 * A separator line of the menu.
 * @author Zolt&aacute;n Farkas
 */
public class JMenuSeparator extends JMenuBaseItem {

    /**
     * Constructs a separator line.
     * @param parent the menu that is the parent of this tray object
     * @param key the unique key of the tray object
     */
    JMenuSeparator(JTrayBaseMenu parent, int key) {
        super(parent, key);
    }

}
