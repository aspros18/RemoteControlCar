package org.dyndns.fzoli.ui.systemtray;

import chrriis.dj.nativeswing.swtimpl.components.JTray;

/**
 * Az SWT SystemTray adaptere.
 * @author zoli
 */
class SwtSystemTray implements SystemTray {
    
    @Override
    public boolean isSupported() {
        return JTray.isSupported();
    }
    
    @Override
    public TrayIcon addTrayIcon() {
        if (!isSupported()) return null;
        return new SwtTrayIcon(this);
    }
    
    @Override
    public void dispose() {
        JTray.dispose();
    }
    
}
