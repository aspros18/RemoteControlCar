package org.dyndns.fzoli.ui.systemtray;

import org.dyndns.fzoli.ui.SwtDisplayProvider;
import org.dyndns.fzoli.ui.SwtDisplayProvider.RunnableReturn;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;

/**
 * Az SWT SystemTray adaptere.
 * @author zoli
 */
class SwtSystemTray implements SystemTray {
    
    private final Display display = SwtDisplayProvider.getDisplay();
    
    private final Shell shell = SwtDisplayProvider.syncReturn(new RunnableReturn<Shell>() {

        @Override
        protected Shell createReturn() {
            return new Shell(display);
        }
        
    });
    
    private final Tray tray = SwtDisplayProvider.syncReturn(new RunnableReturn<Tray>() {

        @Override
        protected Tray createReturn() {
            return display.getSystemTray();
        }
        
    });
    
    @Override
    public boolean isSupported() {
        return tray != null;
    }
    
    @Override
    public TrayIcon addTrayIcon() {
        if (!isSupported()) return null;
        return new SwtTrayIcon(this, display, shell, tray);
    }
    
    @Override
    public void dispose() {
        if (isSupported()) {
            display.syncExec(new Runnable() {

                @Override
                public void run() {
                    shell.dispose();
                    tray.dispose();
                }
                
            });
        }
    }
    
}
