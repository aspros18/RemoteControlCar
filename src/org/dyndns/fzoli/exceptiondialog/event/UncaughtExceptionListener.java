package org.dyndns.fzoli.exceptiondialog.event;

/**
 * Eseménykezelő, ami nem kezelt kivételek esetén hívódik meg.
 * @author zoli
 */
public interface UncaughtExceptionListener {

    /**
     * A nem kezelt kivétel keletkezésekor lefutó metódus.
     * @param e az esemény
     */
    void exceptionThrown(UncaughtExceptionEvent e);
    
}