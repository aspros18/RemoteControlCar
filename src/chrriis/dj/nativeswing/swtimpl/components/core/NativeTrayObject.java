/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components.core;

/**
 * An interface that every native tray object implements.
 * @author Zolt&aacute;n Farkas
 */
interface NativeTrayObject {
    
    /**
     * Returns the unique key of the tray object.
     */
    public int getKey();
    
}
