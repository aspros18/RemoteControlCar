package org.dyndns.fzoli.ui.systemtray;

import chrriis.dj.nativeswing.swtimpl.components.JMenuItem;

/**
 * SWT alapú menüelem.
 * @author zoli
 */
class SwtMenuItem implements MenuItem {

    private final JMenuItem ITEM;
    
    public SwtMenuItem(JMenuItem item) {
        ITEM = item;
    }

    @Override
    public void setEnabled(boolean enabled) {
        ITEM.setEnabled(enabled);
    }
    
}
