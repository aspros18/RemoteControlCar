package org.dyndns.fzoli.rccar.model.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Egy lista, mely a konstruktorban átadott listának hívja meg az alábbi metódusait:
 * - {@link List#add(java.lang.Object)}
 * - {@link List#remove(java.lang.Object)}
 * - {@link List#addAll(java.util.Collection)}
 * - {@link List#clear()}
 * - {@link List#iterator()}
 * - {@link List#contains(java.lang.Object)}.
 * @author zoli
 */
public class ForwardedList<T> extends ArrayList<T> {

    /**
     * Az eredeti lista, ami frissítésre kerül.
     */
    private final List<T> l;

    /**
     * Konstruktor.
     * @param l az eredeti lista, ami frissítésre kerül
     */
    public ForwardedList(List<T> l) {
        this.l = l;
    }

    /**
     * Megmondja, hogy az adott objektum megtalálható-e az eredeti listában.
     */
    @Override
    public boolean contains(Object o) {
        if (l != null) return l.contains(o);
        return false;
    }

    /**
     * Az eredeti listát járja körbe.
     * Foreach ciklusra is érvényes!
     */
    @Override
    public Iterator<T> iterator() {
        if (l != null) return l.iterator();
        return super.iterator();
    }

    /**
     * Az eredeti lista frissítése.
     */
    @Override
    public boolean add(T e) {
        if (l != null) return l.add(e);
        return false;
    }

    /**
     * Az eredeti lista frissítése.
     */
    @Override
    public boolean remove(Object o) {
        if (l != null) return l.remove(o);
        return false;
    }

    /**
     * Az eredeti lista frissítése.
     */
    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (l != null) return l.addAll(c);
        return false;
    }

    /**
     * Az eredeti lista frissítése.
     */
    @Override
    public void clear() {
        if (l != null) l.clear();
    }

}
