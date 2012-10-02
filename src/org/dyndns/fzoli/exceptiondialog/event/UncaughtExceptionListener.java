package org.dyndns.fzoli.exceptiondialog.event;

/**
 * Eseménykezelő, ami nem kezelt kivételek esetén hívódik meg.
 * @author zoli
 */
public interface UncaughtExceptionListener {

    /**
     * A dialógusablak megjelenése előtt lefutó metódus.
     * @param e az esemény
     */
    void exceptionDialogAppears(UncaughtExceptionEvent e);
    
    /**
     * A dialógusablak eltűnése után lefutó metódus.
     */
    void exceptionDialogClosed();
    
}