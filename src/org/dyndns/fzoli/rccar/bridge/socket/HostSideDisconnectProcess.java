package org.dyndns.fzoli.rccar.bridge.socket;

import org.dyndns.fzoli.rccar.model.bridge.HostStorage;
import org.dyndns.fzoli.rccar.model.bridge.StorageList;
import org.dyndns.fzoli.socket.handler.SecureHandler;

/**
 *
 * @author zoli
 */
public class HostSideDisconnectProcess extends BridgeDisconnectProcess {
    
    private HostStorage storage;
    
    public HostSideDisconnectProcess(SecureHandler handler) {
        super(handler);
    }

    private HostStorage getHostStorage() {
        if (storage != null) return storage;
        return storage = StorageList.findHostStorageByName(getRemoteCommonName());
    }
    
    private void setTimeout(boolean b) {
        HostStorage hs = getHostStorage();
        if (hs != null) hs.setUnderTimeout(b);
    }

    @Override
    protected void onTimeout(Exception ex) throws Exception {
        super.onTimeout(ex);
        setTimeout(true);
    }

    @Override
    protected void afterTimeout() throws Exception {
        super.afterTimeout();
        setTimeout(false);
    }

    @Override
    protected void onDisconnect(Exception ex) {
        super.onDisconnect(ex);
        setTimeout(false);
    }
    
}
