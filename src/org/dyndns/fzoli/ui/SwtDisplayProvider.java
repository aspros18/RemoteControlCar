package org.dyndns.fzoli.ui;

import java.awt.GraphicsEnvironment;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tray;

/**
 * SWT Display objektum gyártó.
 * Legyártja a Display objektumot és elindítja az GUI szálat.
 * Mivel a Display nem ad lehetőséget szinkronizált inicializálásra,
 * ebben az osztályban meg lett írva.
 * @author zoli
 */
public final class SwtDisplayProvider {
    
    /**
     * Szinkronizált inicializáláshoz Runnable implementáció.
     */
    public static abstract class RunnableReturn<T> implements Runnable {
        
        /**
         * Az inicializált objektum.
         */
        private T ret;
        
        /**
         * Inicializáló metódus.
         */
        protected abstract T createReturn();

        /**
         * Az inicializálás végrehajtása után lefutó metódus.
         */
        protected void onReturn() {
            ;
        }
        
        /**
         * Az inicializálás közben keletkezett kivételt feldolgozó metódus.
         */
        protected void onException(Exception ex) {
            ;
        }
        
        /**
         * Inicializálás elvégzése.
         */
        @Override
        public final void run() {
            synchronized (this) {
                try {
                    ret = createReturn();
                }
                catch (Exception ex) {
                    onException(ex);
                }
            }
            onReturn();
        }

        /**
         * Az inicializált objektum.
         */
        public final T getReturn() {
            return ret;
        }
        
    }
    
    /**
     * SWT Display.
     * Egyszerre csak egy lehet belőle.
     */
    private static Display display;
    
    private SwtDisplayProvider() {
    }
    
    /**
     * Szinkronizált inicializálás.
     * @param r inicializáló szál
     * @return inicializált objektum, vagy null, ha nincs display létrehozva vagy nincs inicializáló szál megadva
     */
    public static <T> T syncReturn(final RunnableReturn<T> r) {
        if (display != null && r != null) {
            display.syncExec(r);
            return r.getReturn();
        }
        return null;
    }
    
    /**
     * SWT Display inicializálása és GUI frissítés elindítása.
     * Ha már egyszer inicializálták és még nincs megszüntetve, nem inicializál.
     */
    public static Display getDisplay() {
        if (display == null || display.isDisposed()) { // ha inicializálni kell
            Runnable init = new Runnable() { // inicializáló runnable

                @Override
                public void run() {
                    synchronized(this) { // runnable lefoglalása
                        if (!GraphicsEnvironment.isHeadless()) {
                            try {
                                display = new Display(); // display inicializálás, ha van GUI
                            }
                            catch (Throwable t) { // az SWT nem tölthető be
                                ;
                            }
                        }
                        notifyAll(); // jelzés, hogy kész az inicializálás
                    }
                    // GUI frissítés elindítása:
                    try {
                        while (!display.isDisposed()) {
                            if (!display.readAndDispatch()) {
                                display.sleep();
                            }
                        }
                    }
                    catch (Exception ex) {
                        ;
                    }
                }

            };
            new Thread(init).start(); // inicializálás indítása
            synchronized(init) { // runnable lefoglalása
                try {
                    init.wait(); // várakozás az inicializálás befejezéséig
                }
                catch (InterruptedException ex) {
                    ;
                }
            }
        }
        return display; // referencia átadása
    }
    
    /**
     * SWT Display megszüntetése.
     * Ha nincs display létrehozva vagy már megszünt, nem csinál semmit.
     */
    public static void dispose() {
        if (display == null || display.isDisposed()) return;
        display.syncExec(new Runnable() {

            @Override
            public void run() {
                display.dispose();
            }
            
        });
    }
    
    /**
     * Teszt.
     */
    public static void main(String[] args) {
        final Display d = getDisplay();
        Tray tray = syncReturn(new RunnableReturn<Tray>() {

            @Override
            protected Tray createReturn() {
                return d.getSystemTray();
            }
                 
        });
        System.out.println(tray.isDisposed());
        dispose(); // a display megszűnésével minden más SWT felületi elem megszűnik
        System.out.println(tray.isDisposed());
    }
    
}
