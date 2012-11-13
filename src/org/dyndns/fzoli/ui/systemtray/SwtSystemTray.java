package org.dyndns.fzoli.ui.systemtray;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;

/**
 * Az SWT SystemTray adaptere.
 * @author zoli
 */
class SwtSystemTray implements SystemTray {
    
    private final Display display = new Display();
    private final Shell shell = new Shell(display);
    private final Tray tray = display.getSystemTray();
    
    @Override
    public boolean isSupported() {
        return tray != null;
    }
    
    @Override
    public TrayIcon addTrayIcon() {
        if (!isSupported()) return null;
        return new SwtTrayIcon(display, shell, tray);
    }
    
    @Override
    public void dispose() {
        if (isSupported()) {
            display.syncExec(new Runnable() {

                @Override
                public void run() {
                    shell.dispose();
                }
                
            });
        }
    }
    
    @Override
    public void start() {
        start(null);
    }
    
    @Override
    public void start(Thread t) {
        if (t != null) t.start();
        if (isSupported()) {
            try {
                while (!shell.isDisposed()) {
                    try {
                        if (!display.readAndDispatch()) {
                            display.sleep();
                        }
                    }
                    catch (Exception ex) {
                        break;
                    }
                }
                display.dispose();
            }
            catch (Exception ex) {
                ;
            }
        }
        else {
            if (t != null) t.start();
        }
    }
    
}
