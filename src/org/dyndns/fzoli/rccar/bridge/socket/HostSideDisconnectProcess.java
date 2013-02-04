package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 * A jármű-kliens szerver oldali kapcsolatfigyelő.
 * @author zoli
 */
public class HostSideDisconnectProcess extends BridgeDisconnectProcess {
    
    /**
     * A jármű munkamenete.
     */
    private HostStorage storage;
    
    public HostSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    /**
     * A jármű munkamenetét adja vissza.
     * Ha még nincs meg a referencia, megkeresi azt.
     */
    private HostStorage getHostStorage() {
        if (storage != null) return storage;
        return storage = StorageList.findHostStorageByName(getRemoteCommonName());
    }
    
    /**
     * A munkamenetben beállítja, hogy van-e időtúllépés a jármű kapcsolatában.
     * Ha az adat nem módosult, akkor nem módosítja azt.
     */
    private void setTimeout(boolean b) {
        HostStorage hs = getHostStorage();
        if (hs != null && hs.isUnderTimeout() != b) hs.setUnderTimeout(b);
    }

    /**
     * Időtúllépés esetén a munkamenetben beállítja, hogy időtúllépés történt.
     */
    @Override
    protected void onTimeout(Exception ex) throws Exception {
        super.onTimeout(ex);
        setTimeout(true);
    }

    /**
     * A kapcsolat helyreállása után a munkamenetben beállítja, hogy nincs időtúllépés.
     */
    @Override
    protected void afterTimeout() throws Exception {
        super.afterTimeout();
        setTimeout(false);
    }

    /**
     * A kapcsolat bezáródása után a munkamenetben beállítja,
     * hogy nincs időtúllépés, hogy a legközelebbi kapcsolódáskor ne okozzon gondot.
     */
    @Override
    protected void onDisconnect(Exception ex) {
        super.onDisconnect(ex);
        setTimeout(false);
    }
    
}
