package chrriis.dj.nativeswing.swtimpl.components;

import java.util.ArrayList;
import java.util.Collection;

abstract class JTrayListenerObject {
    
    abstract void checkState();
    
}

class JTrayListenerList<T> extends ArrayList<T> {

    private final JTrayListenerObject OWNER;
    
    public JTrayListenerList(JTrayListenerObject owner) {
        OWNER = owner;
    }

    private void checkState() {
        if (OWNER != null) OWNER.checkState();
    }
    
    @Override
    public boolean add(T e) {
        checkState();
        return super.add(e);
    }

    @Override
    public void add(int index, T element) {
        checkState();
        super.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        checkState();
        return super.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        checkState();
        return super.addAll(index, c);
    }
    
}
