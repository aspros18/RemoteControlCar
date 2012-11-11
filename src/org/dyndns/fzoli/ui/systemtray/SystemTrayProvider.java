package org.dyndns.fzoli.ui.systemtray;

/**
 * Rendszerikon-adapter készítő.
 * Ha az SWT rendszerikon elérhető, SwtSystemTrayAdapter,
 * különben AwtSystemTrayAdapter készül.
 * @author zoli
 */
public final class SystemTrayProvider {
    
    private static SystemTrayAdapter adapter;
    
    private SystemTrayProvider() {
    }
    
    /**
     * SystemTrayAdapter referencia megszerzésére.
     * Ha már van létrehozva adapter, nem jön új létre.
     */
    public static SystemTrayAdapter getSystemTray() {
        if (adapter == null) {
            if (isSwtTrayAvailable()) adapter = new SwtSystemTrayAdapter();
            else adapter = new AwtSystemTrayAdapter();
        }
        return adapter;
    }
    
    /**
     * Megadja, hogy használható-e az SWT rendszerikon.
     */
    private static boolean isSwtTrayAvailable() {
        try {
            Class.forName("org.eclipse.swt.widgets.Tray", false, AwtSystemTrayAdapter.class.getClassLoader());
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }
    
}
