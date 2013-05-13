/*
 * Christopher Deckers (chrriis@nextencia.net)
 * http://www.nextencia.net
 *
 * See the file "readme.txt" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */
package chrriis.dj.nativeswing.swtimpl.components;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A list that stores tray listeners.
 * Listeners can not be added to the list if the list's tray object is disposed.
 * @author Zolt&aacute;n Farkas
 */
class JTrayListenerList<T> extends ArrayList<T> {

    private final JTrayObject OWNER;
    
    JTrayListenerList(JTrayObject owner) {
        OWNER = owner;
    }

    private void checkState() {
        if (OWNER != null) OWNER.checkState();
    }
    
    @Override
    public boolean add(T e) {
        if (e == null) return false;
        checkState();
        return super.add(e);
    }

    @Override
    public void add(int index, T element) {
        if (element == null) return;
        checkState();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c == null) return false;
        checkState();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (c == null) return false;
        checkState();
        return super.addAll(index, c);
    }
    
}
