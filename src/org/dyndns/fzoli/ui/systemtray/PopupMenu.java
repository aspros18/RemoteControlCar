package org.dyndns.fzoli.ui.systemtray;

/**
 * A rendszerikon felugró menüje.
 * @author zoli
 */
public interface PopupMenu extends Visibility {
    
    /**
     * Szeparátor hozzáadása a menühöz.
     */
    public void addSeparator();
    
    /**
     * Menüelem hozzáadása a menühöz.
     * @param text megjelenő szöveg
     * @param r callback, ami akkor hívódik meg, ha a menüelemre kattintanak.
     */
    public void addMenuItem(String text, final Runnable r);
    
    /**
     * Checkbox menüelem hozzáadása a menühöz.
     * @param text megjelenő szöveg
     * @param checked kezdőérték
     * @param r callback, ami akkor hívódik meg, ha a menüelemre kattintanak.
     */
    public void addCheckboxMenuItem(String text, boolean checked, final Runnable r);
    
}
